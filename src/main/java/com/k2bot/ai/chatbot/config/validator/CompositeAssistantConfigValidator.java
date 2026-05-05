package com.k2bot.ai.chatbot.config.validator;

import com.k2bot.ai.chatbot.config.api.AssistantConfigValidator;
import com.k2bot.ai.chatbot.config.api.RawConfigValidationRule;
import com.k2bot.ai.chatbot.config.api.ResolvedConfigValidationRule;
import com.k2bot.ai.chatbot.config.model.RawAssistantConfigBundle;
import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CompositeAssistantConfigValidator implements AssistantConfigValidator {

    private static final Logger log = LoggerFactory.getLogger(CompositeAssistantConfigValidator.class);

    private final List<RawConfigValidationRule> rawRules;
    private final List<ResolvedConfigValidationRule> resolvedRules;

    public CompositeAssistantConfigValidator(
            List<RawConfigValidationRule> rawRules,
            List<ResolvedConfigValidationRule> resolvedRules) {
        this.rawRules = rawRules;
        this.resolvedRules = resolvedRules;
    }

    @Override
    public void validate(RawAssistantConfigBundle rawBundle) {
        String code = rawBundle.getAssistant().getAssistantCode();
        log.debug("Running {} raw validation rules for assistantCode={}", rawRules.size(), code);
        for (RawConfigValidationRule rule : rawRules) {
            rule.validate(rawBundle);
        }
    }

    @Override
    public void validateResolved(ResolvedAssistantConfig resolvedConfig) {
        String code = resolvedConfig.getAssistantCode();
        log.debug("Running {} resolved validation rules for assistantCode={}", resolvedRules.size(), code);
        for (ResolvedConfigValidationRule rule : resolvedRules) {
            rule.validate(resolvedConfig);
        }
    }
}
