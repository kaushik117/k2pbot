package com.k2bot.ai.chatbot.config.validator;

import com.k2bot.ai.chatbot.config.api.RawConfigValidationRule;
import com.k2bot.ai.chatbot.config.exception.AssistantConfigValidationException;
import com.k2bot.ai.chatbot.config.model.RawAssistantConfigBundle;
import com.k2bot.ai.chatbot.persistence.entity.RagPolicyEntity;
import org.springframework.stereotype.Component;

@Component
public class RagPolicyValidationRule implements RawConfigValidationRule {

    @Override
    public void validate(RawAssistantConfigBundle bundle) {
        RagPolicyEntity rag = bundle.getRagPolicy();
        if (rag == null) {
            return;
        }

        String code = bundle.getAssistant().getAssistantCode();

        if (Boolean.TRUE.equals(rag.getRagEnabled())
                && (rag.getDefaultKnowledgeBaseId() == null || rag.getDefaultKnowledgeBaseId().isBlank())) {
            throw new AssistantConfigValidationException(code,
                    "RAG is enabled but no defaultKnowledgeBaseId is configured");
        }

        if (rag.getTopK() != null && rag.getTopK() <= 0) {
            throw new AssistantConfigValidationException(code,
                    "RAG topK must be greater than 0, got: " + rag.getTopK());
        }

        if (rag.getSimilarityThreshold() != null
                && (rag.getSimilarityThreshold() < 0.0 || rag.getSimilarityThreshold() > 1.0)) {
            throw new AssistantConfigValidationException(code,
                    "RAG similarityThreshold must be between 0.0 and 1.0, got: " + rag.getSimilarityThreshold());
        }
    }
}
