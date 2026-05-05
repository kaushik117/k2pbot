package com.k2bot.ai.chatbot.config.validator;

import com.k2bot.ai.chatbot.config.api.RawConfigValidationRule;
import com.k2bot.ai.chatbot.config.exception.AssistantConfigValidationException;
import com.k2bot.ai.chatbot.config.model.RawAssistantConfigBundle;
import org.springframework.stereotype.Component;

@Component
public class AssistantActiveValidationRule implements RawConfigValidationRule {

    @Override
    public void validate(RawAssistantConfigBundle bundle) {
        String code = bundle.getAssistant().getAssistantCode();

        if (bundle.getAssistant().getAssistantCode() == null || bundle.getAssistant().getAssistantCode().isBlank()) {
            throw new AssistantConfigValidationException("UNKNOWN", "assistantCode must not be blank");
        }

        if (!Boolean.TRUE.equals(bundle.getAssistant().getActive())) {
            throw new AssistantConfigValidationException(code, "assistant is not active");
        }

        if (bundle.getAssistant().getName() == null || bundle.getAssistant().getName().isBlank()) {
            throw new AssistantConfigValidationException(code, "assistant name must not be blank");
        }
    }
}
