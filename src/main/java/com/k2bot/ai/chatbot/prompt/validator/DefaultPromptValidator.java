package com.k2bot.ai.chatbot.prompt.validator;

import com.k2bot.ai.chatbot.prompt.api.PromptValidator;
import com.k2bot.ai.chatbot.prompt.exception.PromptValidationException;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyResult;
import com.k2bot.ai.chatbot.prompt.model.PromptRenderMetadata;
import org.springframework.stereotype.Component;

@Component
public class DefaultPromptValidator implements PromptValidator {

    @Override
    public void validate(PromptAssemblyResult result) {
        if (result == null) {
            throw new PromptValidationException("PromptAssemblyResult must not be null");
        }
        if (result.getSystemPrompt() == null || result.getSystemPrompt().isBlank()) {
            throw new PromptValidationException("System prompt must not be blank");
        }
        if (result.getUserPrompt() == null || result.getUserPrompt().isBlank()) {
            throw new PromptValidationException("User prompt must not be blank");
        }

        PromptRenderMetadata metadata = result.getMetadata();
        if (metadata != null && metadata.isGroundedMode() && !metadata.isRagInstructionsInjected()) {
            throw new PromptValidationException(
                    "Grounded mode is active but RAG instructions were not injected into the prompt");
        }
    }
}
