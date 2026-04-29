package com.k2bot.ai.chatbot.persistence.entity;

import com.k2bot.ai.chatbot.persistence.converter.JsonMapConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.util.LinkedHashMap;
import java.util.Map;

@Entity
@Table(name = "ai_knowledge_base", indexes = {
        @Index(name = "idx_ai_knowledge_base_kbid", columnList = "knowledge_base_id", unique = true)
})
public class KnowledgeBaseEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "knowledge_base_id", nullable = false, length = 100)
    private String knowledgeBaseId;

    @Column(name = "name", nullable = false, length = 256)
    private String name;

    @Column(name = "vector_store_type", length = 100)
    private String vectorStoreType;

    @Column(name = "embedding_model", length = 100)
    private String embeddingModel;

    @Column(name = "connection_ref", length = 256)
    private String connectionRef;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "metadata_filter_policy")
    private Map<String, String> metadataFilterPolicy = new LinkedHashMap<>();

    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getKnowledgeBaseId() {
        return knowledgeBaseId;
    }

    public void setKnowledgeBaseId(String knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getVectorStoreType() {
        return vectorStoreType;
    }

    public void setVectorStoreType(String vectorStoreType) {
        this.vectorStoreType = vectorStoreType;
    }

    public String getEmbeddingModel() {
        return embeddingModel;
    }

    public void setEmbeddingModel(String embeddingModel) {
        this.embeddingModel = embeddingModel;
    }

    public String getConnectionRef() {
        return connectionRef;
    }

    public void setConnectionRef(String connectionRef) {
        this.connectionRef = connectionRef;
    }

    public Map<String, String> getMetadataFilterPolicy() {
        return metadataFilterPolicy;
    }

    public void setMetadataFilterPolicy(Map<String, String> metadataFilterPolicy) {
        this.metadataFilterPolicy = metadataFilterPolicy;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
