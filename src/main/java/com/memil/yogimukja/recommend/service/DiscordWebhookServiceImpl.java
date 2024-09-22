package com.memil.yogimukja.recommend.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Slf4j
@Service
public class DiscordWebhookServiceImpl {
    private final WebClient webClient;

    public DiscordWebhookServiceImpl(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl("https://discord.com").build();
    }

    // 메시지 전송
    public Mono<String> sendMessage(String webHookUrl, String content) {
        return webClient.post()
                .uri(webHookUrl)
                .bodyValue(new DiscordWebhookMessage(content))
                .retrieve()
                .bodyToMono(String.class);
    }

    private static class DiscordWebhookMessage {
        private final String content;

        public DiscordWebhookMessage(String content) {
            this.content = content;
        }

        public String getContent() {
            return content;
        }
    }
}
