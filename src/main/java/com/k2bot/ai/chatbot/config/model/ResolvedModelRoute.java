package com.k2bot.ai.chatbot.config.model;

import com.k2bot.ai.chatbot.persistence.entity.RouteType;

public final class ResolvedModelRoute {

    private final String routeName;
    private final RouteType routeType;
    private final Integer minPromptLength;
    private final Integer maxPromptLength;
    private final boolean ragEnabledOnly;
    private final boolean toolsRequiredOnly;
    private final boolean structuredOutputOnly;
    private final String targetProvider;
    private final String targetModel;
    private final Integer maxInputTokens;
    private final Double temperature;
    private final int priority;

    public ResolvedModelRoute(
            String routeName,
            RouteType routeType,
            Integer minPromptLength,
            Integer maxPromptLength,
            boolean ragEnabledOnly,
            boolean toolsRequiredOnly,
            boolean structuredOutputOnly,
            String targetProvider,
            String targetModel,
            Integer maxInputTokens,
            Double temperature,
            int priority) {
        this.routeName = routeName;
        this.routeType = routeType;
        this.minPromptLength = minPromptLength;
        this.maxPromptLength = maxPromptLength;
        this.ragEnabledOnly = ragEnabledOnly;
        this.toolsRequiredOnly = toolsRequiredOnly;
        this.structuredOutputOnly = structuredOutputOnly;
        this.targetProvider = targetProvider;
        this.targetModel = targetModel;
        this.maxInputTokens = maxInputTokens;
        this.temperature = temperature;
        this.priority = priority;
    }

    public String getRouteName() {
        return routeName;
    }

    public RouteType getRouteType() {
        return routeType;
    }

    public Integer getMinPromptLength() {
        return minPromptLength;
    }

    public Integer getMaxPromptLength() {
        return maxPromptLength;
    }

    public boolean isRagEnabledOnly() {
        return ragEnabledOnly;
    }

    public boolean isToolsRequiredOnly() {
        return toolsRequiredOnly;
    }

    public boolean isStructuredOutputOnly() {
        return structuredOutputOnly;
    }

    public String getTargetProvider() {
        return targetProvider;
    }

    public String getTargetModel() {
        return targetModel;
    }

    public Integer getMaxInputTokens() {
        return maxInputTokens;
    }

    public Double getTemperature() {
        return temperature;
    }

    public int getPriority() {
        return priority;
    }
}
