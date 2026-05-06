package com.k2bot.ai.chatbot.modelrouting.support;

public class ModelChatOptions {

    private String provider;
    private String model;
    private Double temperature;
    private Integer maxInputTokens;
    private Integer maxOutputTokens;
    private boolean streamingEnabled;

    public String getProvider() {
        return provider;
    }

    public void setProvider(String provider) {
        this.provider = provider;
    }

    public String getModel() {
        return model;
    }

    public void setModel(String model) {
        this.model = model;
    }

    public Double getTemperature() {
        return temperature;
    }

    public void setTemperature(Double temperature) {
        this.temperature = temperature;
    }

    public Integer getMaxInputTokens() {
        return maxInputTokens;
    }

    public void setMaxInputTokens(Integer maxInputTokens) {
        this.maxInputTokens = maxInputTokens;
    }

    public Integer getMaxOutputTokens() {
        return maxOutputTokens;
    }

    public void setMaxOutputTokens(Integer maxOutputTokens) {
        this.maxOutputTokens = maxOutputTokens;
    }

    public boolean isStreamingEnabled() {
        return streamingEnabled;
    }

    public void setStreamingEnabled(boolean streamingEnabled) {
        this.streamingEnabled = streamingEnabled;
    }
}
