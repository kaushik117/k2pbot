package com.k2bot.ai.chatbot.orchestration.model;

import com.k2bot.ai.chatbot.config.model.RuntimeConfigOverride;

import java.util.Map;

public class ChatRequest {

    private String assistantCode;
    private String tenantId;
    private String sessionId;
    private String userId;
    private String message;
    private String locale;
    private String channel;
    private Map<String, Object> context;
    private RuntimeConfigOverride runtimeOverride;

    public String getAssistantCode() {
        return assistantCode;
    }

    public void setAssistantCode(String assistantCode) {
        this.assistantCode = assistantCode;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
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

    public Map<String, Object> getContext() {
        return context;
    }

    public void setContext(Map<String, Object> context) {
        this.context = context;
    }

    public RuntimeConfigOverride getRuntimeOverride() {
        return runtimeOverride;
    }

    public void setRuntimeOverride(RuntimeConfigOverride runtimeOverride) {
        this.runtimeOverride = runtimeOverride;
    }
}
