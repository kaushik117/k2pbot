package com.k2bot.ai.chatbot.prompt.builder;

import com.k2bot.ai.chatbot.config.model.ResolvedToolConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolDefinition;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Component
public class ToolInstructionBuilder {

    private static final String TOOL_PREAMBLE =
            "You have access to tools that you can use to assist with the user's request. " +
            "Use tools when they provide better accuracy or up-to-date information.";

    public Optional<String> build(PromptAssemblyInput input) {
        ResolvedToolConfig toolConfig = input.getResolvedConfig().getToolConfig();
        if (toolConfig == null || !toolConfig.isEnabled()) {
            return Optional.empty();
        }

        List<ResolvedToolDefinition> allowedTools = toolConfig.getAllowedTools();
        if (allowedTools == null || allowedTools.isEmpty()) {
            return Optional.empty();
        }

        List<String> toolNames = allowedTools.stream()
                .map(ResolvedToolDefinition::getToolName)
                .filter(name -> name != null && !name.isBlank())
                .collect(Collectors.toList());

        if (toolNames.isEmpty()) {
            return Optional.of(TOOL_PREAMBLE);
        }

        return Optional.of(TOOL_PREAMBLE + " Available tools: " + String.join(", ", toolNames) + ".");
    }
}
