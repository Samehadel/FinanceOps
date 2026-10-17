package com.financeops.chat.config;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.observation.ObservationRegistry;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import java.time.Duration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

@Configuration
@RequiredArgsConstructor
class OpenAIConfiguration {

    private final ObservationRegistry observationRegistry;
    private final MeterRegistry meterRegistry;

    @Value("${financeops.chat.model}")
    private String model;

    /** Lazy initialization keeps billing available before an API key is configured. */
    @Bean
    @Lazy
    ChatClient chatClient() {
        var options = OpenAiChatOptions.builder()
                .model(model)
                .store(false)
                .reasoningEffort("none")
                .maxCompletionTokens(2048)
                .timeout(Duration.ofSeconds(45))
                .maxRetries(0)
                .build();
        var chatModel = OpenAiChatModel.builder()
                .options(options)
                .observationRegistry(observationRegistry)
                .meterRegistry(meterRegistry)
                .build();
        return ChatClient.builder(chatModel, observationRegistry, null, null)
            .defaultAdvisors(new SimpleLoggerAdvisor())
            .defaultAdvisors(advisors -> advisors.param("provider", "openai"))
            .build();
    }
}
