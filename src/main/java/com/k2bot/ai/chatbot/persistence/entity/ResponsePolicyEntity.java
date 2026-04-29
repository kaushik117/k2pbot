package com.k2bot.ai.chatbot.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_assistant_response_policy", indexes = {
        @Index(name = "idx_ai_response_policy_assistant", columnList = "assistant_id", unique = true)
})
public class ResponsePolicyEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assistant_id", nullable = false, unique = true)
    private AssistantEntity assistant;

    @Column(name = "default_tone", length = 50)
    private String defaultTone;

    @Column(name = "default_format", length = 50)
    private String defaultFormat;

    @Column(name = "citation_required")
    private Boolean citationRequired;

    @Column(name = "markdown_enabled")
    private Boolean markdownEnabled;

    @Column(name = "stream_enabled")
    private Boolean streamEnabled;

    @Column(name = "max_output_tokens")
    private Integer maxOutputTokens;

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

    public String getDefaultTone() {
        return defaultTone;
    }

    public void setDefaultTone(String defaultTone) {
        this.defaultTone = defaultTone;
    }

    public String getDefaultFormat() {
        return defaultFormat;
    }

    public void setDefaultFormat(String defaultFormat) {
        this.defaultFormat = defaultFormat;
    }

    public Boolean getCitationRequired() {
        return citationRequired;
    }

    public void setCitationRequired(Boolean citationRequired) {
        this.citationRequired = citationRequired;
    }

    public Boolean getMarkdownEnabled() {
        return markdownEnabled;
    }

    public void setMarkdownEnabled(Boolean markdownEnabled) {
        this.markdownEnabled = markdownEnabled;
    }

    public Boolean getStreamEnabled() {
        return streamEnabled;
    }

    public void setStreamEnabled(Boolean streamEnabled) {
        this.streamEnabled = streamEnabled;
    }

    public Integer getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(Integer maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }
}
