package com.k2bot.ai.chatbot.prompt.service;

import com.k2bot.ai.chatbot.config.model.ResolvedPromptConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedRagConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedResponseConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolConfig;
import com.k2bot.ai.chatbot.prompt.api.PromptAssemblyService;
import com.k2bot.ai.chatbot.prompt.api.PromptInstructionComposer;
import com.k2bot.ai.chatbot.prompt.api.PromptTemplateRenderer;
import com.k2bot.ai.chatbot.prompt.api.PromptValidator;
import com.k2bot.ai.chatbot.prompt.api.PromptVariableResolver;
import com.k2bot.ai.chatbot.prompt.exception.PromptAssemblyException;
import com.k2bot.ai.chatbot.prompt.exception.PromptTemplateNotFoundException;
import com.k2bot.ai.chatbot.prompt.exception.PromptValidationException;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyResult;
import com.k2bot.ai.chatbot.prompt.model.PromptRenderMetadata;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class DefaultPromptAssemblyService implements PromptAssemblyService {

    private final PromptVariableResolver promptVariableResolver;
    private final PromptTemplateRenderer promptTemplateRenderer;
    private final PromptInstructionComposer promptInstructionComposer;
    private final PromptValidator promptValidator;

    public DefaultPromptAssemblyService(
            PromptVariableResolver promptVariableResolver,
            PromptTemplateRenderer promptTemplateRenderer,
            PromptInstructionComposer promptInstructionComposer,
            PromptValidator promptValidator) {
        this.promptVariableResolver = promptVariableResolver;
        this.promptTemplateRenderer = promptTemplateRenderer;
        this.promptInstructionComposer = promptInstructionComposer;
        this.promptValidator = promptValidator;
    }

    @Override
    public PromptAssemblyResult assemble(PromptAssemblyInput input) {
        try {
            ResolvedPromptConfig promptConfig = input.getResolvedConfig().getPromptConfig();
            if (promptConfig == null) {
                throw new PromptTemplateNotFoundException(
                        "No prompt config found for assistant: "
                                + input.getResolvedConfig().getAssistantCode());
            }

            Map<String, Object> variables = promptVariableResolver.resolve(input);

            List<String> systemInstructions = promptInstructionComposer.composeSystemInstructions(input);
            List<String> developerInstructions = promptInstructionComposer.composeDeveloperInstructions(input);

            String systemTemplate = orEmpty(promptConfig.getSystemPromptTemplate());
            String systemBody = promptTemplateRenderer.render(systemTemplate, variables);
            String systemPrompt = appendInstructions(systemBody, systemInstructions);

            String developerTemplate = orEmpty(promptConfig.getDeveloperPromptTemplate());
            String developerBody = developerTemplate.isBlank()
                    ? ""
                    : promptTemplateRenderer.render(developerTemplate, variables);
            String developerPrompt = appendInstructions(developerBody, developerInstructions);

            String userPrompt = input.getChatRequest() != null
                    ? orEmpty(input.getChatRequest().getMessage())
                    : "";

            PromptRenderMetadata metadata = buildMetadata(input, systemInstructions, developerInstructions);

            PromptAssemblyResult result = new PromptAssemblyResult(
                    systemPrompt,
                    developerPrompt.isBlank() ? null : developerPrompt,
                    userPrompt,
                    variables,
                    promptConfig.getPromptVersion(),
                    metadata
            );

            promptValidator.validate(result);

            return result;

        } catch (PromptAssemblyException | PromptTemplateNotFoundException | PromptValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new PromptAssemblyException(
                    "Failed to assemble prompt for assistant: "
                            + input.getResolvedConfig().getAssistantCode(), e);
        }
    }

    private PromptRenderMetadata buildMetadata(
            PromptAssemblyInput input,
            List<String> systemInstructions,
            List<String> developerInstructions) {

        ResolvedRagConfig ragConfig = input.getResolvedConfig().getRagConfig();
        boolean groundedMode = ragConfig != null && ragConfig.isGroundedAnswerRequired();

        ResolvedToolConfig toolConfig = input.getResolvedConfig().getToolConfig();
        boolean toolsEnabled = toolConfig != null && toolConfig.isEnabled();
        boolean toolInstructionsInjected = toolsEnabled && !developerInstructions.isEmpty();

        ResolvedResponseConfig responseConfig = input.getResolvedConfig().getResponseConfig();
        boolean responseFormatInjected = responseConfig != null && !systemInstructions.isEmpty();

        return new PromptRenderMetadata(
                groundedMode,
                toolInstructionsInjected,
                responseFormatInjected,
                false,
                groundedMode
        );
    }

    private String appendInstructions(String base, List<String> instructions) {
        if (instructions.isEmpty()) {
            return base;
        }
        StringBuilder sb = new StringBuilder(base);
        for (String instruction : instructions) {
            if (instruction != null && !instruction.isBlank()) {
                if (sb.length() > 0) {
                    sb.append("\n");
                }
                sb.append(instruction);
            }
        }
        return sb.toString();
    }

    private String orEmpty(String value) {
        return value != null ? value : "";
    }
}
