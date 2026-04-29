package com.k2bot.ai.chatbot.persistence.entity;

import com.k2bot.ai.chatbot.persistence.converter.JsonMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "ai_assistant_rag_policy", indexes = {
        @Index(name = "idx_ai_rag_policy_assistant", columnList = "assistant_id", unique = true)
})
public class RagPolicyEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assistant_id", nullable = false, unique = true)
    private AssistantEntity assistant;

    @Column(name = "rag_enabled")
    private Boolean ragEnabled;

    @Column(name = "default_knowledge_base_id", length = 100)
    private String defaultKnowledgeBaseId;

    @Column(name = "top_k")
    private Integer topK;

    @Column(name = "similarity_threshold")
    private Double similarityThreshold;

    @Column(name = "retrieval_strategy", length = 50)
    private String retrievalStrategy;

    @Column(name = "citations_enabled")
    private Boolean citationsEnabled;

    @Column(name = "grounded_answer_required")
    private Boolean groundedAnswerRequired;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "metadata_filters")
    private Map<String, String> metadataFilters = new LinkedHashMap<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssistantEntity getAssistant() {
        return assistant;
    }

    public void setAssistant(AssistantEntity assistant) {
        this.assistant = assistant;
    }

    public Boolean getRagEnabled() {
        return ragEnabled;
    }

    public void setRagEnabled(Boolean ragEnabled) {
        this.ragEnabled = ragEnabled;
    }

    public String getDefaultKnowledgeBaseId() {
        return defaultKnowledgeBaseId;
    }

    public void setDefaultKnowledgeBaseId(String defaultKnowledgeBaseId) {
        this.defaultKnowledgeBaseId = defaultKnowledgeBaseId;
    }

    public Integer getTopK() {
        return topK;
    }

    public void setTopK(Integer topK) {
        this.topK = topK;
    }

    public Double getSimilarityThreshold() {
        return similarityThreshold;
    }

    public void setSimilarityThreshold(Double similarityThreshold) {
        this.similarityThreshold = similarityThreshold;
    }

    public String getRetrievalStrategy() {
        return retrievalStrategy;
    }

    public void setRetrievalStrategy(String retrievalStrategy) {
        this.retrievalStrategy = retrievalStrategy;
    }

    public Boolean getCitationsEnabled() {
        return citationsEnabled;
    }

    public void setCitationsEnabled(Boolean citationsEnabled) {
        this.citationsEnabled = citationsEnabled;
    }

    public Boolean getGroundedAnswerRequired() {
        return groundedAnswerRequired;
    }

    public void setGroundedAnswerRequired(Boolean groundedAnswerRequired) {
        this.groundedAnswerRequired = groundedAnswerRequired;
    }

    public Map<String, String> getMetadataFilters() {
        return metadataFilters;
    }

    public void setMetadataFilters(Map<String, String> metadataFilters) {
        this.metadataFilters = metadataFilters;
    }
}
