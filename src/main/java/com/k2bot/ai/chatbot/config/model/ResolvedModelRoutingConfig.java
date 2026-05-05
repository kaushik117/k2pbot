package com.k2bot.ai.chatbot.config.model;

import java.util.List;

public final class ResolvedModelRoutingConfig {

    private final String defaultModel;
    private final String defaultProvider;
    private final List<ResolvedModelRoute> routes;
    private final FallbackPolicy fallbackPolicy;
    private final Integer defaultMaxInputTokens;
    private final Double defaultTemperature;

    public ResolvedModelRoutingConfig(
            String defaultModel,
            String defaultProvider,
            List<ResolvedModelRoute> routes,
            FallbackPolicy fallbackPolicy,
            Integer defaultMaxInputTokens,
            Double defaultTemperature) {
        this.defaultModel = defaultModel;
        this.defaultProvider = defaultProvider;
        this.routes = routes;
        this.fallbackPolicy = fallbackPolicy;
        this.defaultMaxInputTokens = defaultMaxInputTokens;
        this.defaultTemperature = defaultTemperature;
    }

    public String getDefaultModel() {
        return defaultModel;
    }

    public String getDefaultProvider() {
        return defaultProvider;
    }

    public List<ResolvedModelRoute> getRoutes() {
        return routes;
    }

    public FallbackPolicy getFallbackPolicy() {
        return fallbackPolicy;
    }

    public Integer getDefaultMaxInputTokens() {
        return defaultMaxInputTokens;
    }

    public Double getDefaultTemperature() {
        return defaultTemperature;
    }
}
