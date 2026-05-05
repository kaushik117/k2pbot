package com.k2bot.ai.chatbot.prompt.builder;

import com.k2bot.ai.chatbot.config.model.ResolvedRagConfig;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class GroundedModeInstructionBuilder {

    private static final String GROUNDED_INSTRUCTION =
            "You MUST base your answer strictly on the provided context documents. " +
            "If the context does not contain sufficient information to answer the question, " +
            "explicitly state that you cannot answer based on available information. " +
            "Do not use prior knowledge or make assumptions beyond what is provided.";

    public Optional<String> build(PromptAssemblyInput input) {
        ResolvedRagConfig ragConfig = input.getResolvedConfig().getRagConfig();
        if (ragConfig != null && ragConfig.isGroundedAnswerRequired()) {
            return Optional.of(GROUNDED_INSTRUCTION);
        }
        return Optional.empty();
    }
}
