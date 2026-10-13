package com.financeops.chat.config;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.Nonnull;
import org.jspecify.annotations.NonNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.ChatClientResponse;
import org.springframework.ai.chat.client.advisor.api.CallAdvisor;
import org.springframework.ai.chat.client.advisor.api.CallAdvisorChain;
import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.Usage;
import org.jspecify.annotations.Nullable;
import org.springframework.util.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/** Logs each synchronous model call, including provider-reported usage when available. */
final class LlmLoggingAdvisor implements CallAdvisor {
    private static final Logger log = LoggerFactory.getLogger(LlmLoggingAdvisor.class);
    private static final ObjectMapper JSON = new ObjectMapper().setDefaultPropertyInclusion(JsonInclude.Include.ALWAYS);


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
        context.put("provider", request.context().get("provider"));
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
            event.put("usage", usage(chat == null ? null : chat.getMetadata().getUsage()));
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
            write(event, true);
            throw exception;
        }
    }

    private void setOptionsContext(ChatClientRequest request, LinkedHashMap<String, Object> context) {
        var options = request.prompt().getOptions();
        if (options == null) {
            return;
        }

        if (StringUtils.hasText(options.getModel())) {
            context.put("model", options.getModel());
        }

        if (options.getMaxTokens() != null) {
            context.put("maxTokens", options.getMaxTokens());
        }

        if (options.getTemperature() != null) {
            context.put("temperature", options.getTemperature());
        }

        if (options.getTopP() != null) {
            context.put("topP", options.getTopP());
        }

        if (options.getTopK() != null) {
            context.put("topK", options.getTopK());
        }

        if (options.getFrequencyPenalty() != null) {
            context.put("frequencyPenalty", options.getFrequencyPenalty());
        }

        if (options.getPresencePenalty() != null) {
            context.put("presencePenalty", options.getPresencePenalty());
        }

        if (options.getStopSequences() != null) {
            context.put("stopSequences", options.getStopSequences());
        }
    }

    private Map<String, Object> usage(@Nullable Usage usage) {
        // EmptyUsage represents unavailable counts, rather than a measured zero.
        boolean available = usage != null && !(usage instanceof EmptyUsage);
        var counts = new LinkedHashMap<String, Object>();
        counts.put("inputTokens", available ? usage.getPromptTokens() : null);
        counts.put("outputTokens", available ? usage.getCompletionTokens() : null);
        counts.put("totalTokens", available ? usage.getTotalTokens() : null);
        counts.put("cachedInputTokens", available ? usage.getCacheReadInputTokens() : null);
        counts.put("cacheWriteInputTokens", available ? usage.getCacheWriteInputTokens() : null);
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
