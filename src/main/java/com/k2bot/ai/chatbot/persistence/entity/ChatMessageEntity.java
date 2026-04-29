package com.k2bot.ai.chatbot.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_chat_message", indexes = {
        @Index(name = "idx_ai_chat_message_session", columnList = "session_id"),
        @Index(name = "idx_ai_chat_message_request", columnList = "request_id")
})
public class ChatMessageEntity extends BaseAuditEntity {

    @Id
    @Column(name = "message_id", length = 100, nullable = false)
    private String messageId;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "session_id", nullable = false)
    private ChatSessionEntity session;

    @Column(name = "request_id", length = 100)
    private String requestId;

    @Enumerated(EnumType.STRING)
    @Column(name = "message_role", length = 32)
    private MessageRole messageRole;

    @Lob
    @Column(name = "content")
    private String content;

    @Column(name = "selected_model", length = 100)
    private String selectedModel;

    @Column(name = "prompt_version", length = 50)
    private String promptVersion;

    @Column(name = "config_version", length = 50)
    private String configVersion;

    @Column(name = "token_count")
    private Integer tokenCount;

    @Column(name = "finish_reason", length = 100)
    private String finishReason;

    @Column(name = "has_citations")
    private Boolean hasCitations;

    public String getMessageId() {
        return messageId;
    }

    public void setMessageId(String messageId) {
        this.messageId = messageId;
    }

    public ChatSessionEntity getSession() {
        return session;
    }

    public void setSession(ChatSessionEntity session) {
        this.session = session;
    }

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public MessageRole getMessageRole() {
        return messageRole;
    }

    public void setMessageRole(MessageRole messageRole) {
        this.messageRole = messageRole;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getSelectedModel() {
        return selectedModel;
    }

    public void setSelectedModel(String selectedModel) {
        this.selectedModel = selectedModel;
    }

    public String getPromptVersion() {
        return promptVersion;
    }

    public void setPromptVersion(String promptVersion) {
        this.promptVersion = promptVersion;
    }

    public String getConfigVersion() {
        return configVersion;
    }

    public void setConfigVersion(String configVersion) {
        this.configVersion = configVersion;
    }

    public Integer getTokenCount() {
        return tokenCount;
    }

    public void setTokenCount(Integer tokenCount) {
        this.tokenCount = tokenCount;
    }

    public String getFinishReason() {
        return finishReason;
    }

    public void setFinishReason(String finishReason) {
        this.finishReason = finishReason;
    }

    public Boolean getHasCitations() {
        return hasCitations;
    }

    public void setHasCitations(Boolean hasCitations) {
        this.hasCitations = hasCitations;
    }
}
