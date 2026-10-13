package com.financeops.chat.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import java.time.Duration;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.AdvisorParams;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;

@Configuration
class OpenAIConfiguration {
    /** Lazy initialization keeps billing available before an API key is configured. */
    @Bean
    @Lazy
    ChatClient chatClient(@Value("${financeops.chat.model}") String model) {
        var options = OpenAiChatOptions.builder()
                .model(model)
                .store(false)
                .reasoningEffort("low")
                .maxCompletionTokens(2048)
                .timeout(Duration.ofSeconds(45))
                .maxRetries(0)
                .build();
        var chatModel = OpenAiChatModel.builder()
                .options(options)
                .build();
        return ChatClient.builder(chatModel)
            .defaultAdvisors(new LlmLoggingAdvisor())
            .defaultAdvisors(AdvisorParams.toolCallingAdvisorAutoRegister(false))
            .build();
    }
}
