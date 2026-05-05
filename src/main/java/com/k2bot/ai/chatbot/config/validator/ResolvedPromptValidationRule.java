package com.k2bot.ai.chatbot.config.validator;

import com.k2bot.ai.chatbot.config.api.ResolvedConfigValidationRule;
import com.k2bot.ai.chatbot.config.exception.AssistantConfigValidationException;
import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import org.springframework.stereotype.Component;

@Component
public class ResolvedPromptValidationRule implements ResolvedConfigValidationRule {

    @Override
    public void validate(ResolvedAssistantConfig resolvedConfig) {
        String code = resolvedConfig.getAssistantCode();

        if (resolvedConfig.getPromptConfig() == null) {
            throw new AssistantConfigValidationException(code, "resolved promptConfig must not be null");
        }

        if (resolvedConfig.getModelRoutingConfig() == null) {
            throw new AssistantConfigValidationException(code, "resolved modelRoutingConfig must not be null");
        }
    }
}
