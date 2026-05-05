package com.k2bot.ai.chatbot.config.model;

import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;

import java.util.List;

public class RuntimeConfigOverride {

    private String knowledgeBaseId;
    private MemoryStoreType memoryStoreType;
    private List<String> enabledToolNames;
    private String modelHint;
    private Boolean streamingEnabled;

    public RuntimeConfigOverride() {
    }

    public boolean isEmpty() {
        return knowledgeBaseId == null
                && memoryStoreType == null
                && (enabledToolNames == null || enabledToolNames.isEmpty())
                && modelHint == null
                && streamingEnabled == null;
    }

    public String getKnowledgeBaseId() {
        return knowledgeBaseId;
    }

    public void setKnowledgeBaseId(String knowledgeBaseId) {
        this.knowledgeBaseId = knowledgeBaseId;
    }

    public MemoryStoreType getMemoryStoreType() {
        return memoryStoreType;
    }

    public void setMemoryStoreType(MemoryStoreType memoryStoreType) {
        this.memoryStoreType = memoryStoreType;
    }

    public List<String> getEnabledToolNames() {
        return enabledToolNames;
    }

    public void setEnabledToolNames(List<String> enabledToolNames) {
        this.enabledToolNames = enabledToolNames;
    }

    public String getModelHint() {
        return modelHint;
    }

    public void setModelHint(String modelHint) {
        this.modelHint = modelHint;
    }

    public Boolean getStreamingEnabled() {
        return streamingEnabled;
    }

    public void setStreamingEnabled(Boolean streamingEnabled) {
        this.streamingEnabled = streamingEnabled;
    }
}
