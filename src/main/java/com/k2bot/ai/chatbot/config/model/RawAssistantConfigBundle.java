package com.k2bot.ai.chatbot.config.model;

import com.k2bot.ai.chatbot.persistence.entity.AssistantEntity;
import com.k2bot.ai.chatbot.persistence.entity.MemoryPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.ModelRouteEntity;
import com.k2bot.ai.chatbot.persistence.entity.PromptTemplateEntity;
import com.k2bot.ai.chatbot.persistence.entity.RagPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.ResponsePolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.SafetyPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.TenantAssistantOverrideEntity;
import com.k2bot.ai.chatbot.persistence.entity.ToolPolicyEntity;

import java.util.List;

public class RawAssistantConfigBundle {

    private final String tenantId;
    private final AssistantEntity assistant;
    private final PromptTemplateEntity promptTemplate;
    private final List<ModelRouteEntity> modelRoutes;
    private final MemoryPolicyEntity memoryPolicy;
    private final RagPolicyEntity ragPolicy;
    private final List<ToolPolicyEntity> toolPolicies;
    private final SafetyPolicyEntity safetyPolicy;
    private final ResponsePolicyEntity responsePolicy;
    private final List<TenantAssistantOverrideEntity> tenantOverrides;

    public RawAssistantConfigBundle(
            String tenantId,
            AssistantEntity assistant,
            PromptTemplateEntity promptTemplate,
            List<ModelRouteEntity> modelRoutes,
            MemoryPolicyEntity memoryPolicy,
            RagPolicyEntity ragPolicy,
            List<ToolPolicyEntity> toolPolicies,
            SafetyPolicyEntity safetyPolicy,
            ResponsePolicyEntity responsePolicy,
            List<TenantAssistantOverrideEntity> tenantOverrides) {
        this.tenantId = tenantId;
        this.assistant = assistant;
        this.promptTemplate = promptTemplate;
        this.modelRoutes = modelRoutes;
        this.memoryPolicy = memoryPolicy;
        this.ragPolicy = ragPolicy;
        this.toolPolicies = toolPolicies;
        this.safetyPolicy = safetyPolicy;
        this.responsePolicy = responsePolicy;
        this.tenantOverrides = tenantOverrides;
    }

    public String getTenantId() {
        return tenantId;
    }

    public AssistantEntity getAssistant() {
        return assistant;
    }

    public PromptTemplateEntity getPromptTemplate() {
        return promptTemplate;
    }

    public List<ModelRouteEntity> getModelRoutes() {
        return modelRoutes;
    }

    public MemoryPolicyEntity getMemoryPolicy() {
        return memoryPolicy;
    }

    public RagPolicyEntity getRagPolicy() {
        return ragPolicy;
    }

    public List<ToolPolicyEntity> getToolPolicies() {
        return toolPolicies;
    }

    public SafetyPolicyEntity getSafetyPolicy() {
        return safetyPolicy;
    }

    public ResponsePolicyEntity getResponsePolicy() {
        return responsePolicy;
    }

    public List<TenantAssistantOverrideEntity> getTenantOverrides() {
        return tenantOverrides;
    }
}
