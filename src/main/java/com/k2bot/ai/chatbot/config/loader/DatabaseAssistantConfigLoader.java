package com.k2bot.ai.chatbot.config.loader;

import com.k2bot.ai.chatbot.config.api.AssistantConfigLoader;
import com.k2bot.ai.chatbot.config.exception.AssistantConfigNotFoundException;
import com.k2bot.ai.chatbot.config.model.RawAssistantConfigBundle;
import com.k2bot.ai.chatbot.persistence.entity.AssistantEntity;
import com.k2bot.ai.chatbot.persistence.entity.MemoryPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.ModelRouteEntity;
import com.k2bot.ai.chatbot.persistence.entity.PromptTemplateEntity;
import com.k2bot.ai.chatbot.persistence.entity.RagPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.ResponsePolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.SafetyPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.TenantAssistantOverrideEntity;
import com.k2bot.ai.chatbot.persistence.entity.ToolPolicyEntity;
import com.k2bot.ai.chatbot.persistence.repository.AssistantRepository;
import com.k2bot.ai.chatbot.persistence.repository.MemoryPolicyRepository;
import com.k2bot.ai.chatbot.persistence.repository.ModelRouteRepository;
import com.k2bot.ai.chatbot.persistence.repository.PromptTemplateRepository;
import com.k2bot.ai.chatbot.persistence.repository.RagPolicyRepository;
import com.k2bot.ai.chatbot.persistence.repository.ResponsePolicyRepository;
import com.k2bot.ai.chatbot.persistence.repository.SafetyPolicyRepository;
import com.k2bot.ai.chatbot.persistence.repository.TenantAssistantOverrideRepository;
import com.k2bot.ai.chatbot.persistence.repository.ToolPolicyRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;

@Component
public class DatabaseAssistantConfigLoader implements AssistantConfigLoader {

    private static final Logger log = LoggerFactory.getLogger(DatabaseAssistantConfigLoader.class);

    private final AssistantRepository assistantRepository;
    private final PromptTemplateRepository promptTemplateRepository;
    private final ModelRouteRepository modelRouteRepository;
    private final MemoryPolicyRepository memoryPolicyRepository;
    private final RagPolicyRepository ragPolicyRepository;
    private final ToolPolicyRepository toolPolicyRepository;
    private final SafetyPolicyRepository safetyPolicyRepository;
    private final ResponsePolicyRepository responsePolicyRepository;
    private final TenantAssistantOverrideRepository tenantOverrideRepository;

    public DatabaseAssistantConfigLoader(
            AssistantRepository assistantRepository,
            PromptTemplateRepository promptTemplateRepository,
            ModelRouteRepository modelRouteRepository,
            MemoryPolicyRepository memoryPolicyRepository,
            RagPolicyRepository ragPolicyRepository,
            ToolPolicyRepository toolPolicyRepository,
            SafetyPolicyRepository safetyPolicyRepository,
            ResponsePolicyRepository responsePolicyRepository,
            TenantAssistantOverrideRepository tenantOverrideRepository) {
        this.assistantRepository = assistantRepository;
        this.promptTemplateRepository = promptTemplateRepository;
        this.modelRouteRepository = modelRouteRepository;
        this.memoryPolicyRepository = memoryPolicyRepository;
        this.ragPolicyRepository = ragPolicyRepository;
        this.toolPolicyRepository = toolPolicyRepository;
        this.safetyPolicyRepository = safetyPolicyRepository;
        this.responsePolicyRepository = responsePolicyRepository;
        this.tenantOverrideRepository = tenantOverrideRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public RawAssistantConfigBundle load(String assistantCode, String tenantId) {
        log.debug("Loading raw config bundle for assistantCode={} tenantId={}", assistantCode, tenantId);

        AssistantEntity assistant = assistantRepository
                .findByAssistantCodeAndActiveTrue(assistantCode)
                .orElseThrow(() -> new AssistantConfigNotFoundException(assistantCode, tenantId));

        Long assistantId = assistant.getId();

        PromptTemplateEntity promptTemplate = promptTemplateRepository
                .findByAssistantIdAndActiveTrue(assistantId)
                .orElse(null);

        List<ModelRouteEntity> modelRoutes = modelRouteRepository
                .findByAssistantIdAndActiveTrueOrderByPriorityAsc(assistantId);

        MemoryPolicyEntity memoryPolicy = memoryPolicyRepository
                .findByAssistantId(assistantId)
                .orElse(null);

        RagPolicyEntity ragPolicy = ragPolicyRepository
                .findByAssistantId(assistantId)
                .orElse(null);

        List<ToolPolicyEntity> toolPolicies = toolPolicyRepository
                .findByAssistantIdAndEnabledTrue(assistantId);

        SafetyPolicyEntity safetyPolicy = safetyPolicyRepository
                .findByAssistantId(assistantId)
                .orElse(null);

        ResponsePolicyEntity responsePolicy = responsePolicyRepository
                .findByAssistantId(assistantId)
                .orElse(null);

        List<TenantAssistantOverrideEntity> tenantOverrides = tenantId != null
                ? tenantOverrideRepository.findByTenantIdAndAssistantIdAndActiveTrue(tenantId, assistantId)
                : Collections.emptyList();

        log.debug("Loaded raw config bundle: promptTemplate={}, routes={}, toolPolicies={}, tenantOverrides={}",
                promptTemplate != null, modelRoutes.size(), toolPolicies.size(), tenantOverrides.size());

        return new RawAssistantConfigBundle(
                tenantId,
                assistant,
                promptTemplate,
                modelRoutes,
                memoryPolicy,
                ragPolicy,
                toolPolicies,
                safetyPolicy,
                responsePolicy,
                tenantOverrides
        );
    }
}
