package com.k2bot.ai.chatbot.prompt.builder;

import com.k2bot.ai.chatbot.config.model.ResolvedPromptConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedResponseConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedSafetyConfig;
import com.k2bot.ai.chatbot.prompt.api.PromptInstructionComposer;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class DefaultPromptInstructionComposer implements PromptInstructionComposer {

    private final GroundedModeInstructionBuilder groundedModeInstructionBuilder;
    private final ResponseFormatInstructionBuilder responseFormatInstructionBuilder;
    private final ToolInstructionBuilder toolInstructionBuilder;

    public DefaultPromptInstructionComposer(
            GroundedModeInstructionBuilder groundedModeInstructionBuilder,
            ResponseFormatInstructionBuilder responseFormatInstructionBuilder,
            ToolInstructionBuilder toolInstructionBuilder) {
        this.groundedModeInstructionBuilder = groundedModeInstructionBuilder;
        this.responseFormatInstructionBuilder = responseFormatInstructionBuilder;
        this.toolInstructionBuilder = toolInstructionBuilder;
    }

    @Override
    public List<String> composeSystemInstructions(PromptAssemblyInput input) {
        List<String> instructions = new ArrayList<>();

        ResolvedPromptConfig promptConfig = input.getResolvedConfig().getPromptConfig();
        if (promptConfig != null && promptConfig.getGuardrailInstructions() != null) {
            for (String guardrail : promptConfig.getGuardrailInstructions()) {
                if (guardrail != null && !guardrail.isBlank()) {
                    instructions.add(guardrail);
                }
            }
        }

        ResolvedSafetyConfig safetyConfig = input.getResolvedConfig().getSafetyConfig();
        if (safetyConfig != null
                && safetyConfig.getDisallowedTopics() != null
                && !safetyConfig.getDisallowedTopics().isEmpty()) {
            instructions.add("Do not discuss the following topics: "
                    + String.join(", ", safetyConfig.getDisallowedTopics()) + ".");
        }

        groundedModeInstructionBuilder.build(input).ifPresent(instructions::add);

        responseFormatInstructionBuilder.build(input).ifPresent(instructions::add);

        return instructions;
    }

    @Override
    public List<String> composeDeveloperInstructions(PromptAssemblyInput input) {
        List<String> instructions = new ArrayList<>();

        toolInstructionBuilder.build(input).ifPresent(instructions::add);

        ResolvedResponseConfig responseConfig = input.getResolvedConfig().getResponseConfig();
        if (responseConfig != null && responseConfig.isCitationRequired()) {
            instructions.add("Always include citations referencing the source documents used in your response.");
        }

        return instructions;
    }
}
