package com.k2bot.ai.chatbot.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_rag_retrieval_audit", indexes = {
        @Index(name = "idx_ai_rag_audit_request", columnList = "request_id"),
        @Index(name = "idx_ai_rag_audit_session", columnList = "session_id")
})
public class RagRetrievalAuditEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "knowledge_base_id", length = 100)
    private String knowledgeBaseId;

    @Column(name = "retrieved_document_count")
    private Integer retrievedDocumentCount;

    @Column(name = "top_k")
    private Integer topK;

    @Column(name = "similarity_threshold")
    private Double similarityThreshold;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "grounded_mode")
    private Boolean groundedMode;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getKnowledgeBaseId() {
        return knowledgeBaseId;
    }

    public void setKnowledgeBaseId(String knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }

    public Integer getRetrievedDocumentCount() {
        return retrievedDocumentCount;
    }

    public void setRetrievedDocumentCount(Integer retrievedDocumentCount) {
        this.retrievedDocumentCount = retrievedDocumentCount;
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

    public Long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public Boolean getGroundedMode() {
        return groundedMode;
    }

    public void setGroundedMode(Boolean groundedMode) {
        this.groundedMode = groundedMode;
    }
}
