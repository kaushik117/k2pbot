package com.k2bot.ai.chatbot.config.exception;

public class AssistantConfigNotFoundException extends RuntimeException {

    private final String assistantCode;
    private final String tenantId;

    public AssistantConfigNotFoundException(String assistantCode, String tenantId) {
        super("No active assistant config found for assistantCode='" + assistantCode + "' tenantId='" + tenantId + "'");
        this.assistantCode = assistantCode;
        this.tenantId = tenantId;
    }

    public String getAssistantCode() {
        return assistantCode;
    }

    public String getTenantId() {
        return tenantId;
    }
}
