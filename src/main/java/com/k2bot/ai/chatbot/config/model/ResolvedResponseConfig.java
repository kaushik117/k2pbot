package com.k2bot.ai.chatbot.config.model;

public final class ResolvedResponseConfig {

    private final String defaultTone;
    private final String defaultFormat;
    private final boolean citationRequired;
    private final boolean markdownEnabled;
    private final boolean streamEnabled;
    private final Integer maxOutputTokens;

    public ResolvedResponseConfig(
            String defaultTone,
            String defaultFormat,
            boolean citationRequired,
            boolean markdownEnabled,
            boolean streamEnabled,
            Integer maxOutputTokens) {
        this.defaultTone = defaultTone;
        this.defaultFormat = defaultFormat;
        this.citationRequired = citationRequired;
        this.markdownEnabled = markdownEnabled;
        this.streamEnabled = streamEnabled;
        this.maxOutputTokens = maxOutputTokens;
    }

    public String getDefaultTone() {
        return defaultTone;
    }

    public String getDefaultFormat() {
        return defaultFormat;
    }

    public boolean isCitationRequired() {
        return citationRequired;
    }

    public boolean isMarkdownEnabled() {
        return markdownEnabled;
    }

    public boolean isStreamEnabled() {
        return streamEnabled;
    }

    public Integer getMaxOutputTokens() {
        return maxOutputTokens;
    }
}
