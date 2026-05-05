package com.k2bot.ai.chatbot.config.validator;

import com.k2bot.ai.chatbot.config.api.RawConfigValidationRule;
import com.k2bot.ai.chatbot.config.exception.AssistantConfigValidationException;
import com.k2bot.ai.chatbot.config.model.RawAssistantConfigBundle;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class PromptTemplateValidationRule implements RawConfigValidationRule {

    private static final Logger log = LoggerFactory.getLogger(PromptTemplateValidationRule.class);

    @Override
    public void validate(RawAssistantConfigBundle bundle) {
        String code = bundle.getAssistant().getAssistantCode();

        if (bundle.getPromptTemplate() == null) {
            log.warn("No active prompt template found for assistantCode={} — assistant will run without a system prompt", code);
            return;
        }

        String systemPrompt = bundle.getPromptTemplate().getSystemPromptTemplate();
        if (systemPrompt == null || systemPrompt.isBlank()) {
            throw new AssistantConfigValidationException(code,
                    "prompt template exists but systemPromptTemplate is blank");
        }
    }
}
