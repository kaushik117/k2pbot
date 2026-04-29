package com.k2bot.ai.chatbot.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;

import java.time.Instant;

@Entity
@Table(name = "ai_chat_session", indexes = {
        @Index(name = "idx_ai_chat_session_tenant_user", columnList = "tenant_id,user_id"),
        @Index(name = "idx_ai_chat_session_assistant", columnList = "assistant_code")
})
public class ChatSessionEntity extends BaseAuditEntity {

    @Id
    @Column(name = "session_id", length = 100, nullable = false)
    private String sessionId;

    @Column(name = "tenant_id", length = 100)
    private String tenantId;

    @Column(name = "assistant_code", length = 100)
    private String assistantCode;

    @Column(name = "user_id", length = 100)
    private String userId;

    @Column(name = "title", length = 256)
    private String title;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 32)
    private SessionStatus status;

    @Column(name = "last_message_at")
    private Instant lastMessageAt;

    @Column(name = "locale", length = 20)
    private String locale;

    @Column(name = "channel", length = 50)
    private String channel;

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

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public SessionStatus getStatus() {
        return status;
    }

    public void setStatus(SessionStatus status) {
        this.status = status;
    }

    public Instant getLastMessageAt() {
        return lastMessageAt;
    }

    public void setLastMessageAt(Instant lastMessageAt) {
        this.lastMessageAt = lastMessageAt;
    }

    public String getLocale() {
        return locale;
    }

    public void setLocale(String locale) {
        this.locale = locale;
    }

    public String getChannel() {
        return channel;
    }

    public void setChannel(String channel) {
        this.channel = channel;
    }
}
