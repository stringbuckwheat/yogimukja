package com.memil.yogimukja.common.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

public class DiscordWebhookUrlValidator implements ConstraintValidator<ValidDiscordWebhook, String> {
    private static final String DISCORD_WEBHOOK_URL_PREFIX =
            "https://discord.com/api/webhooks/";


    @Override
    public boolean isValid(String url, ConstraintValidatorContext context) {
        if (url == null || url.isEmpty()) {
            return false;
        }

        return url.startsWith(DISCORD_WEBHOOK_URL_PREFIX);
    }
}
