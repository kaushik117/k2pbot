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
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "ai_assistant_model_route", indexes = {
        @Index(name = "idx_ai_model_route_assistant", columnList = "assistant_id")
})
public class ModelRouteEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assistant_id", nullable = false)
    private AssistantEntity assistant;

    @Column(name = "route_name", length = 100)
    private String routeName;

    @Enumerated(EnumType.STRING)
    @Column(name = "route_type", length = 32)
    private RouteType routeType;

    @Column(name = "min_prompt_length")
    private Integer minPromptLength;

    @Column(name = "max_prompt_length")
    private Integer maxPromptLength;

    @Column(name = "rag_enabled_only")
    private Boolean ragEnabledOnly;

    @Column(name = "tools_required_only")
    private Boolean toolsRequiredOnly;

    @Column(name = "structured_output_only")
    private Boolean structuredOutputOnly;

    @Column(name = "target_provider", length = 100)
    private String targetProvider;

    @Column(name = "target_model", length = 100)
    private String targetModel;

    @Column(name = "max_input_tokens")
    private Integer maxInputTokens;

    @Column(name = "temperature")
    private Double temperature;

    @Column(name = "priority")
    private Integer priority;

    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

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

    public String getRouteName() {
        return routeName;
    }

    public void setRouteName(String routeName) {
        this.routeName = routeName;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public void setRouteType(RouteType routeType) {
        this.routeType = routeType;
    }

    public Integer getMinPromptLength() {
        return minPromptLength;
    }

    public void setMinPromptLength(Integer minPromptLength) {
        this.minPromptLength = minPromptLength;
    }

    public Integer getMaxPromptLength() {
        return maxPromptLength;
    }

    public void setMaxPromptLength(Integer maxPromptLength) {
        this.maxPromptLength = maxPromptLength;
    }

    public Boolean getRagEnabledOnly() {
        return ragEnabledOnly;
    }

    public void setRagEnabledOnly(Boolean ragEnabledOnly) {
        this.ragEnabledOnly = ragEnabledOnly;
    }

    public Boolean getToolsRequiredOnly() {
        return toolsRequiredOnly;
    }

    public void setToolsRequiredOnly(Boolean toolsRequiredOnly) {
        this.toolsRequiredOnly = toolsRequiredOnly;
    }

    public Boolean getStructuredOutputOnly() {
        return structuredOutputOnly;
    }

    public void setStructuredOutputOnly(Boolean structuredOutputOnly) {
        this.structuredOutputOnly = structuredOutputOnly;
    }

    public String getTargetProvider() {
        return targetProvider;
    }

    public void setTargetProvider(String targetProvider) {
        this.targetProvider = targetProvider;
    }

    public String getTargetModel() {
        return targetModel;
    }

    public void setTargetModel(String targetModel) {
        this.targetModel = targetModel;
    }

    public Integer getMaxInputTokens() {
        return maxInputTokens;
    }

    public void setMaxInputTokens(Integer maxInputTokens) {
        this.maxInputTokens = maxInputTokens;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getPriority() {
        return priority;
    }

    public void setPriority(Integer priority) {
        this.priority = priority;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
