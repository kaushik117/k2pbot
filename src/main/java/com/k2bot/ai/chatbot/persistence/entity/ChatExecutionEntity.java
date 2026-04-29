package com.k2bot.ai.chatbot.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "ai_chat_execution", indexes = {
        @Index(name = "idx_ai_chat_execution_session", columnList = "session_id"),
        @Index(name = "idx_ai_chat_execution_assistant", columnList = "assistant_code")
})
public class ChatExecutionEntity {

    @Id
    @Column(name = "request_id", length = 100, nullable = false)
    private String requestId;

    @Column(name = "session_id", length = 100)
    private String sessionId;

    @Column(name = "tenant_id", length = 100)
    private String tenantId;

    @Column(name = "assistant_code", length = 100)
    private String assistantCode;

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "config_version", length = 50)
    private String configVersion;

    @Column(name = "selected_provider", length = 100)
    private String selectedProvider;

    @Column(name = "selected_model", length = 100)
    private String selectedModel;

    @Column(name = "knowledge_base_id", length = 100)
    private String knowledgeBaseId;

    @Enumerated(EnumType.STRING)
    @Column(name = "memory_store_type", length = 32)
    private MemoryStoreType memoryStoreType;

    @Lob
    @Column(name = "enabled_tools_json")
    private String enabledToolsJson;

    @Column(name = "streaming_enabled")
    private Boolean streamingEnabled;

    @Column(name = "success")
    private Boolean success;

    @Column(name = "error_code", length = 100)
    private String errorCode;

    @Column(name = "error_message", length = 1024)
    private String errorMessage;

    @Column(name = "input_tokens")
    private Integer inputTokens;

    @Column(name = "output_tokens")
    private Integer outputTokens;

    @Column(name = "latency_ms")
    private Long latencyMs;

    @Column(name = "started_at")
    private Instant startedAt;

    @Column(name = "completed_at")
    private Instant completedAt;

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

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getAssistantCode() {
        return assistantCode;
    }

    public void setAssistantCode(String assistantCode) {
        this.assistantCode = assistantCode;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public String getSelectedProvider() {
        return selectedProvider;
    }

    public void setSelectedProvider(String selectedProvider) {
        this.selectedProvider = selectedProvider;
    }

    public String getSelectedModel() {
        return selectedModel;
    }

    public void setSelectedModel(String selectedModel) {
        this.selectedModel = selectedModel;
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

    public String getEnabledToolsJson() {
        return enabledToolsJson;
    }

    public void setEnabledToolsJson(String enabledToolsJson) {
        this.enabledToolsJson = enabledToolsJson;
    }

    public Boolean getStreamingEnabled() {
        return streamingEnabled;
    }

    public void setStreamingEnabled(Boolean streamingEnabled) {
        this.streamingEnabled = streamingEnabled;
    }

    public Boolean getSuccess() {
        return success;
    }

    public void setSuccess(Boolean success) {
        this.success = success;
    }

    public String getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(String errorCode) {
        this.errorCode = errorCode;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

    public Integer getInputTokens() {
        return inputTokens;
    }

    public void setInputTokens(Integer inputTokens) {
        this.inputTokens = inputTokens;
    }

    public Integer getOutputTokens() {
        return outputTokens;
    }

    public void setOutputTokens(Integer outputTokens) {
        this.outputTokens = outputTokens;
    }

    public Long getLatencyMs() {
        return latencyMs;
    }

    public void setLatencyMs(Long latencyMs) {
        this.latencyMs = latencyMs;
    }

    public Instant getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(Instant startedAt) {
        this.startedAt = startedAt;
    }

    public Instant getCompletedAt() {
        return completedAt;
    }

    public void setCompletedAt(Instant completedAt) {
        this.completedAt = completedAt;
    }
}
