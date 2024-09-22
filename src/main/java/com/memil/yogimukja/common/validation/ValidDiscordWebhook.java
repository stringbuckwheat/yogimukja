package com.memil.yogimukja.common.validation;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.FIELD, ElementType.PARAMETER})
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = DiscordWebhookUrlValidator.class)
public @interface ValidDiscordWebhook {
    String message() default "디스코드 웹훅 주소를 확인해주세요";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
