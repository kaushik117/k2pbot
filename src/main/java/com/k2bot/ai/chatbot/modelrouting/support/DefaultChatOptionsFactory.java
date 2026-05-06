package com.k2bot.ai.chatbot.modelrouting.support;

import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedResponseConfig;
import com.k2bot.ai.chatbot.modelrouting.api.ChatOptionsFactory;
import com.k2bot.ai.chatbot.modelrouting.model.ModelSelectionResult;
import org.springframework.stereotype.Component;

@Component
public class DefaultChatOptionsFactory implements ChatOptionsFactory {

    @Override
    public ModelChatOptions create(ModelSelectionResult selectionResult, ResolvedAssistantConfig config) {
        ModelChatOptions options = new ModelChatOptions();
        options.setProvider(selectionResult.getProvider());
        options.setModel(selectionResult.getModelName());
        options.setTemperature(selectionResult.getTemperature());
        options.setMaxInputTokens(selectionResult.getMaxInputTokens());
        options.setMaxOutputTokens(resolveMaxOutputTokens(selectionResult, config));
        options.setStreamingEnabled(selectionResult.isStreamingEnabled());
        return options;
    }

    private Integer resolveMaxOutputTokens(ModelSelectionResult result, ResolvedAssistantConfig config) {
        if (result.getMaxOutputTokens() != null) {
            return result.getMaxOutputTokens();
        }
        ResolvedResponseConfig responseConfig = config != null ? config.getResponseConfig() : null;
        if (responseConfig != null) {
            return responseConfig.getMaxOutputTokens();
        }
        return null;
    }
}
