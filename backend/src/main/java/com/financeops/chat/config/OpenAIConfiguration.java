package com.financeops.chat.config;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import java.time.Duration;

@Configuration
class OpenAIConfiguration {
    /** Created on first chat call so billing remains available before a key is configured. */
    @Bean(destroyMethod = "close")
    @Lazy
    OpenAIClient openAIClient(@Value("${financeops.chat.api-key}") String apiKey) {
        return OpenAIOkHttpClient.builder()
            .apiKey(apiKey)
            .timeout(Duration.ofSeconds(45))
            .maxRetries(0)
            .build();
    }
}
