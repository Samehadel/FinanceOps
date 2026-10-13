package com.financeops.chat.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.openai.core.ObjectMappers;
import com.openai.errors.OpenAIServiceException;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.DefaultUsage;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** Logs each synchronous model call, including provider-reported usage when available. */
final class LlmLoggingAdvisor implements CallAdvisor {
    private static final Logger log = LoggerFactory.getLogger(LlmLoggingAdvisor.class);
    private static final ObjectMapper JSON = ObjectMappers.jsonMapper().copy().setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS);


    @Override
    @NonNull
    public String getName() { return "LlmLoggingAdvisor"; }

    @Override
    public int getOrder() { return 0; }

    @Override
    @Nonnull
    public ChatClientResponse adviseCall(ChatClientRequest request, @Nonnull CallAdvisorChain chain) {
        var context = new LinkedHashMap<String, Object>();
        context.put("callId", UUID.randomUUID().toString());
        context.put("provider", "openai");
        context.put("tenantId", request.context().get("tenantId"));
        setOptionsContext(request, context);
        var payload = new LinkedHashMap<String, Object>();
        payload.put("messages", request.prompt().getInstructions().stream().map(message -> {
            var entry = new LinkedHashMap<String, Object>();
            entry.put("role", message.getMessageType().getValue());
            entry.put("content", message.getText());
            return entry;
        }).toList());
        // Explicit projection excludes credentials and HTTP headers from options.
        var start = new LinkedHashMap<>(context);
        start.put("event", "llm.request");
        start.put("request", payload);
        write(start, false);
        long startedAt = System.nanoTime();
        try {
            var response = chain.nextCall(request);
            var event = new LinkedHashMap<>(context);
            event.put("event", "llm.response");
            event.put("durationMs", elapsed(startedAt));
            var chat = response.chatResponse();
            JsonNode nativeUsage = chat != null && chat.getMetadata().getUsage() instanceof DefaultUsage usage
                ? JSON.valueToTree(usage.getNativeUsage()) : null;
            event.put("usage", usage(nativeUsage));
            if (chat != null) {
                event.put("responseId", chat.getMetadata().getId());
                event.put("responseModel", chat.getMetadata().getModel());
                event.put("response", chat.getResults().stream().map(result -> {
                    var entry = new LinkedHashMap<String, Object>();
                    entry.put("content", result.getOutput().getText());
                    entry.put("metadata", result.getOutput().getMetadata());
                    entry.put("finishReason", result.getMetadata().getFinishReason());
                    return entry;
                }).toList());
            } else {
                event.put("response", null);
            }
            write(event, false);
            return response;
        } catch (RuntimeException exception) {
            var event = new LinkedHashMap<>(context);
            event.put("event", "llm.failure");
            event.put("durationMs", elapsed(startedAt));
            event.put("errorType", exception.getClass().getSimpleName());
            event.put("usage", usage(null));
            event.put("response", null);
            if (exception instanceof OpenAIServiceException providerException) {
                event.put("httpStatus", providerException.statusCode());
                event.put("response", providerException.body());
            }
            write(event, true);
            throw exception;
        }
    }

    private void setOptionsContext(ChatClientRequest request, LinkedHashMap<String, Object> context) {
        var options = (OpenAiChatOptions) request.prompt().getOptions();
        if (options == null) {
            return;
        }

        if (StringUtils.hasText(options.getModel())) {
            context.put("model", options.getModel());
        }

        if (StringUtils.hasText(options.getReasoningEffort())) {
            context.put("reasoning", options.getReasoningEffort());
        }

        if (options.getMaxCompletionTokens() != null) {
            context.put("maxCompletionTokens", options.getMaxCompletionTokens());
        }

        if (options.getStore() != null) {
            context.put("store", options.getStore());
        }
    }

    private Map<String, Object> usage(JsonNode usage) {
        var counts = new LinkedHashMap<String, Object>();
        counts.put("inputTokens", usage == null ? null : usage.get("prompt_tokens"));
        counts.put("outputTokens", usage == null ? null : usage.get("completion_tokens"));
        counts.put("totalTokens", usage == null ? null : usage.get("total_tokens"));
        counts.put("cachedInputTokens", usage == null ? null : usage.path("prompt_tokens_details").get("cached_tokens"));
        counts.put("reasoningTokens", usage == null ? null : usage.path("completion_tokens_details").get("reasoning_tokens"));
        return counts;
    }

    private long elapsed(long startedAt) {
        return TimeUnit.NANOSECONDS.toMillis(System.nanoTime() - startedAt);
    }

    private void write(Map<String, Object> event, boolean failed) {
        try {
            String json = JSON.writeValueAsString(event);
            if (failed) log.warn("LLM {}", json);
            else log.info("LLM {}", json);
        } catch (Exception exception) {
            // Logging must not replace the model result or provider error.
            log.warn("LLM log serialization failed: callId={} event={}", event.get("callId"), event.get("event"));
        }
    }
}
