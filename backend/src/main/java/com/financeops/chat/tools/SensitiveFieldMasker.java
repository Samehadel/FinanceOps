package com.financeops.chat.tools;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.financeops.billing.contract.BillingFacts.CustomerView;
import com.financeops.billing.contract.BillingFacts.InvoiceView;
import com.financeops.billing.contract.BillingFacts.PaymentView;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;

/** One instance per reply; its key and restoration map never leave the application. */
@Slf4j
public final class SensitiveFieldMasker {
    public static final String TOOL_CONTEXT_KEY = "sensitiveFieldMasker";
    public enum Action { MASK, REMOVE }

    /** Source field and semantic property use generated enums, so renames fail compilation. */
    public record FieldPolicy(Enum<?> source, Enum<?> property, Action action) {
        public FieldPolicy {
            java.util.Objects.requireNonNull(source);
            java.util.Objects.requireNonNull(property);
            java.util.Objects.requireNonNull(action);
        }
    }

    // Invoice/payment customer references share the CustomerView property namespace.
    private static final List<FieldPolicy> POLICIES = List.of(
        new FieldPolicy(InvoiceView.Fields.customerName, CustomerView.Fields.name, Action.MASK),
        new FieldPolicy(InvoiceView.Fields.customerId, CustomerView.Fields.id, Action.REMOVE),
        new FieldPolicy(InvoiceView.Fields.customerEmail, CustomerView.Fields.email, Action.REMOVE),
        new FieldPolicy(CustomerView.Fields.name, CustomerView.Fields.name, Action.MASK),
        new FieldPolicy(CustomerView.Fields.id, CustomerView.Fields.id, Action.REMOVE),
        new FieldPolicy(CustomerView.Fields.email, CustomerView.Fields.email, Action.REMOVE),
        new FieldPolicy(PaymentView.Fields.customerName, CustomerView.Fields.name, Action.MASK)
    );
    // LocalDate must remain a scalar, rather than Jackson's default [year, month, day] array.
    private static final ObjectMapper JSON = new ObjectMapper().findAndRegisterModules()
        .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    private static final String TOKEN_PATTERN = "__PRIVATE_[0-9a-f]{64}__";
    private final List<FieldPolicy> policies;
    private final SecretKeySpec key;
    private final Map<String, String> textReplacements = new ConcurrentHashMap<>();
    private final Map<String, String> originals = new ConcurrentHashMap<>();

    public SensitiveFieldMasker() {
        this(POLICIES);
    }

    public SensitiveFieldMasker(List<FieldPolicy> policies) {
        this.policies = List.copyOf(policies);
        if (policies.stream().map(FieldPolicy::source).distinct().count() != policies.size()) {
            throw new IllegalArgumentException("Each source field must have exactly one policy");
        }
        byte[] secret = new byte[32];
        new SecureRandom().nextBytes(secret);
        key = new SecretKeySpec(secret, "HmacSHA256");
    }

    public void register(Enum<?> field, String value) {
        // Removal applies to the source property only; never register its value for text replacement.
        policies.stream().filter(policy -> policy.source().equals(field) && policy.action() == Action.MASK).findFirst()
            .ifPresent(policy -> protect(policy, value));
    }

    private String protect(FieldPolicy policy, String value) {
        if (!StringUtils.hasText(value)) {
            return value;
        }
        String token = token(policy.property(), value);
        String existing = originals.putIfAbsent(token, value);
        if (existing != null && !existing.equals(value)) {
            throw new IllegalStateException("Sensitive value token collision");
        }
        // Free text has no entity information. Choose a stable token if multiple properties
        // share a value. Removed properties never participate in this map.
        textReplacements.merge(value, token, (left, right) -> left.compareTo(right) <= 0 ? left : right);
        return token;
    }

    private String token(Enum<?> property, String value) {
        try {
            // Mac is mutable, so each computation gets its own instance; only the key is shared.
            Mac mac = Mac.getInstance("HmacSHA256");
            mac.init(key);
            update(mac, property.getDeclaringClass().getName());
            update(mac, property.name());
            update(mac, value);
            return "__PRIVATE_" + HexFormat.of().formatHex(mac.doFinal()) + "__";
        } catch (GeneralSecurityException exception) {
            throw new IllegalStateException("Unable to mask sensitive value", exception);
        }
    }

    private void update(Mac mac, String value) {
        byte[] bytes = value.getBytes(StandardCharsets.UTF_8);
        mac.update(ByteBuffer.allocate(Integer.BYTES).putInt(bytes.length).array());
        mac.update(bytes);
    }

    /** Sanitize a copy of typed billing facts; never fall back to an unprotected result. */
    public String maskResult(Object result) {
        try {
            JsonNode tree = JSON.valueToTree(result);
            protectFields(result, tree);
            protectStrings(tree);
            return JSON.writeValueAsString(tree);
        } catch (Exception exception) {
            log.error("Unable to mask tool result", exception);
            throw new IllegalStateException("Unable to mask tool result", exception);
        }
    }

    private void protectFields(Object source, JsonNode tree) throws ReflectiveOperationException {
        if (source == null) {
            return;
        }
        if (source.getClass().isRecord() && tree instanceof ObjectNode object) {
            for (var component : source.getClass().getRecordComponents()) {
                Object value = component.getAccessor().invoke(source);
                FieldPolicy policy = policies.stream().filter(candidate ->
                    candidate.source().getDeclaringClass().getEnclosingClass().equals(source.getClass())
                        && candidate.source().name().equals(component.getName())).findFirst().orElse(null);
                if (!object.has(component.getName())) {
                    // Reject serialization aliases until an explicit mapping is supported.
                    throw new IllegalStateException("Record field is missing from tool JSON");
                }
                JsonNode node = object.get(component.getName());
                if (policy != null) {
                    if (policy.action() == Action.REMOVE) {
                        object.remove(component.getName());
                    } else if (!node.isNull()) {
                        if (!node.isValueNode()) {
                            throw new IllegalArgumentException("Masked fields must contain scalar values");
                        }
                        object.put(component.getName(), protect(policy, node.asText()));
                    }
                } else {
                    protectFields(value, node);
                }
            }
        } else if (source instanceof Iterable<?> values && tree instanceof ArrayNode array) {
            int index = 0;
            for (Object value : values) {
                protectFields(value, array.get(index++));
            }
        } else if (source instanceof Map<?, ?> values && tree instanceof ObjectNode object) {
            for (var entry : values.entrySet()) {
                protectFields(entry.getValue(), object.get(String.valueOf(entry.getKey())));
            }
        } else if (tree.isContainerNode()) {
            throw new IllegalArgumentException("Tool containers must be records, iterables, or maps of typed values");
        }
    }

    private void protectStrings(JsonNode node) {
        if (node instanceof ObjectNode object) {
            for (var entry : object.properties()) {
                if (entry.getValue().isTextual()) {
                    object.put(entry.getKey(), maskText(entry.getValue().asText()));
                } else {
                    protectStrings(entry.getValue());
                }
            }
        } else if (node instanceof ArrayNode array) {
            for (int index = 0; index < array.size(); index++) {
                if (array.get(index).isTextual()) {
                    array.set(index, JSON.getNodeFactory().textNode(maskText(array.get(index).asText())));
                } else {
                    protectStrings(array.get(index));
                }
            }
        }
    }

    public String maskText(String text) {
        // Entries are never deleted; concurrent additions may be picked up by a later call.
        if (textReplacements.isEmpty()) {
            return text;
        }
        String alternatives = textReplacements.keySet().stream()
            .sorted((left, right) -> Integer.compare(right.length(), left.length()))
            .map(Pattern::quote).collect(java.util.stream.Collectors.joining("|"));
        // Keep already-generated tokens intact, even if a protected value is a substring of one.
        return Pattern.compile(TOKEN_PATTERN + "|" + alternatives).matcher(text)
            .replaceAll(match -> Matcher.quoteReplacement(textReplacements.getOrDefault(match.group(), match.group())));
    }

    public String restore(String text) {
        return Pattern.compile(TOKEN_PATTERN).matcher(text).replaceAll(match -> {
            String value = originals.get(match.group());
            return Matcher.quoteReplacement(value == null ? match.group() : value);
        });
    }
}
