package com.k2bot.ai.chatbot.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_assistant_memory_policy", indexes = {
        @Index(name = "idx_ai_memory_policy_assistant", columnList = "assistant_id", unique = true)
})
public class MemoryPolicyEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assistant_id", nullable = false, unique = true)
    private AssistantEntity assistant;

    @Column(name = "memory_enabled")
    private Boolean memoryEnabled;

    @Enumerated(EnumType.STRING)
    @Column(name = "store_type", length = 32)
    private MemoryStoreType storeType;

    @Column(name = "message_window_size")
    private Integer messageWindowSize;

    @Column(name = "ttl_minutes")
    private Integer ttlMinutes;

    @Column(name = "persist_chat_history")
    private Boolean persistChatHistory;

    @Column(name = "summarize_old_messages")
    private Boolean summarizeOldMessages;

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

    public Boolean getMemoryEnabled() {
        return memoryEnabled;
    }

    public void setMemoryEnabled(Boolean memoryEnabled) {
        this.memoryEnabled = memoryEnabled;
    }

    public MemoryStoreType getStoreType() {
        return storeType;
    }

    public void setStoreType(MemoryStoreType storeType) {
        this.storeType = storeType;
    }

    public Integer getMessageWindowSize() {
        return messageWindowSize;
    }

    public void setMessageWindowSize(Integer messageWindowSize) {
        this.messageWindowSize = messageWindowSize;
    }

    public Integer getTtlMinutes() {
        return ttlMinutes;
    }

    public void setTtlMinutes(Integer ttlMinutes) {
        this.ttlMinutes = ttlMinutes;
    }

    public Boolean getPersistChatHistory() {
        return persistChatHistory;
    }

    public void setPersistChatHistory(Boolean persistChatHistory) {
        this.persistChatHistory = persistChatHistory;
    }

    public Boolean getSummarizeOldMessages() {
        return summarizeOldMessages;
    }

    public void setSummarizeOldMessages(Boolean summarizeOldMessages) {
        this.summarizeOldMessages = summarizeOldMessages;
    }
}
