package com.k2bot.ai.chatbot.prompt.resolver;

import com.k2bot.ai.chatbot.config.model.ResolvedPromptConfig;
import com.k2bot.ai.chatbot.modelrouting.model.ModelSelectionResult;
import com.k2bot.ai.chatbot.orchestration.model.ChatRequest;
import com.k2bot.ai.chatbot.prompt.api.PromptVariableResolver;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;
import org.springframework.stereotype.Component;

import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class DefaultPromptVariableResolver implements PromptVariableResolver {

    @Override
    public Map<String, Object> resolve(PromptAssemblyInput input) {
        Map<String, Object> vars = new LinkedHashMap<>();

        ResolvedPromptConfig promptConfig = input.getResolvedConfig().getPromptConfig();
        if (promptConfig != null && promptConfig.getDefaultVariables() != null) {
            vars.putAll(promptConfig.getDefaultVariables());
        }

        ChatRequest request = input.getChatRequest();
        if (request != null) {
            vars.put("assistantCode", emptyIfNull(request.getAssistantCode()));
            vars.put("tenantId", emptyIfNull(request.getTenantId()));
            vars.put("sessionId", emptyIfNull(request.getSessionId()));
            vars.put("userId", emptyIfNull(request.getUserId()));
            vars.put("locale", emptyIfNull(request.getLocale()));
            vars.put("channel", emptyIfNull(request.getChannel()));
            if (request.getContext() != null) {
                vars.putAll(request.getContext());
            }
        }

        ModelSelectionResult modelSelection = input.getModelSelectionResult();
        if (modelSelection != null) {
            vars.put("selectedModel", emptyIfNull(modelSelection.getModelName()));
            vars.put("selectedProvider", emptyIfNull(modelSelection.getProvider()));
        }

        if (input.getResolvedRuntimeVariables() != null) {
            vars.putAll(input.getResolvedRuntimeVariables());
        }

        return vars;
    }

    private String emptyIfNull(String value) {
        return value != null ? value : "";
    }
}
