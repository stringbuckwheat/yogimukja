package com.memil.yogimukja.recommend.controller;

import com.memil.yogimukja.recommend.scheduler.LunchRecommendationScheduler;
import com.memil.yogimukja.recommend.service.DiscordWebhookServiceImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
@Slf4j
@RequiredArgsConstructor
public class RecommendController {
    private final DiscordWebhookServiceImpl discordWebhookService;
    private final LunchRecommendationScheduler scheduler;

    @GetMapping("/api/recommend")
    public String sendWebhook(@RequestParam(name = "message") String message) {
        //         String webhookUrl = "https://discord.com/api/webhooks/{webhook-id}/{webhook-token}";
//        String webhookUrl = "https://discord.com/api/webhooks/1287138095929495553/xDD2-D2ZHjZndHTBdnK6xTp-zVtmA6rZ4Vrzu_PGi2iPvqBVkOiMR_m18A5jt-AQIvCx";
//        return discordWebhookService.sendMessage(webhookUrl, message);

        scheduler.sendLunchRecommendations();
        return "디코 확인~";
    }
}
