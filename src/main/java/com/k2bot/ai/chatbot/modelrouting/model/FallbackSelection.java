package com.k2bot.ai.chatbot.modelrouting.model;

public class FallbackSelection {

    private final String provider;
    private final String modelName;
    private final String fallbackReason;

    public FallbackSelection(String provider, String modelName, String fallbackReason) {
        this.provider = provider;
        this.modelName = modelName;
        this.fallbackReason = fallbackReason;
    }

    public String getProvider() {
        return provider;
    }

    public String getModelName() {
        return modelName;
    }

    public String getFallbackReason() {
        return fallbackReason;
    }
}
