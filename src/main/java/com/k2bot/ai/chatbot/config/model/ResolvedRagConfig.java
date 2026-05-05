package com.k2bot.ai.chatbot.config.model;

import java.util.Map;

public final class ResolvedRagConfig {

    private final boolean enabled;
    private final String defaultKnowledgeBaseId;
    private final Integer topK;
    private final Double similarityThreshold;
    private final String retrievalStrategy;
    private final boolean citationsEnabled;
    private final boolean groundedAnswerRequired;
    private final Map<String, String> metadataFilters;

    public ResolvedRagConfig(
            boolean enabled,
            String defaultKnowledgeBaseId,
            Integer topK,
            Double similarityThreshold,
            String retrievalStrategy,
            boolean citationsEnabled,
            boolean groundedAnswerRequired,
            Map<String, String> metadataFilters) {
        this.enabled = enabled;
        this.defaultKnowledgeBaseId = defaultKnowledgeBaseId;
        this.topK = topK;
        this.similarityThreshold = similarityThreshold;
        this.retrievalStrategy = retrievalStrategy;
        this.citationsEnabled = citationsEnabled;
        this.groundedAnswerRequired = groundedAnswerRequired;
        this.metadataFilters = metadataFilters;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public String getDefaultKnowledgeBaseId() {
        return defaultKnowledgeBaseId;
    }

    public Integer getTopK() {
        return topK;
    }

    public Double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public String getRetrievalStrategy() {
        return retrievalStrategy;
    }

    public boolean isCitationsEnabled() {
        return citationsEnabled;
    }

    public boolean isGroundedAnswerRequired() {
        return groundedAnswerRequired;
    }

    public Map<String, String> getMetadataFilters() {
        return metadataFilters;
    }
}
