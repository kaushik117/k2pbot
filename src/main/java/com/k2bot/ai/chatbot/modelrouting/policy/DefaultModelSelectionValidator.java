package com.k2bot.ai.chatbot.modelrouting.policy;

import com.k2bot.ai.chatbot.modelrouting.api.ModelSelectionValidator;
import com.k2bot.ai.chatbot.modelrouting.exception.ModelSelectionValidationException;
import com.k2bot.ai.chatbot.modelrouting.model.ModelSelectionResult;
import com.k2bot.ai.chatbot.modelrouting.model.RoutingInput;
import org.springframework.stereotype.Component;

@Component
public class DefaultModelSelectionValidator implements ModelSelectionValidator {

    @Override
    public void validate(ModelSelectionResult result, RoutingInput input) {
        if (result == null) {
            throw new ModelSelectionValidationException("ModelSelectionResult must not be null");
        }
        if (isBlank(result.getModelName())) {
            throw new ModelSelectionValidationException(
                    "Selected model name must not be blank for assistant: "
                            + input.getResolvedConfig().getAssistantCode());
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
