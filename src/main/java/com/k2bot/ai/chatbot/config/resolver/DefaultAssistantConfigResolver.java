package com.k2bot.ai.chatbot.config.resolver;

import com.k2bot.ai.chatbot.config.api.AssistantConfigResolver;
import com.k2bot.ai.chatbot.config.exception.AssistantConfigResolutionException;
import com.k2bot.ai.chatbot.config.model.FallbackPolicy;
import com.k2bot.ai.chatbot.config.model.RawAssistantConfigBundle;
import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedMemoryConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedModelRoute;
import com.k2bot.ai.chatbot.config.model.ResolvedModelRoutingConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedPromptConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedRagConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedResponseConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedSafetyConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolDefinition;
import com.k2bot.ai.chatbot.config.model.RuntimeConfigOverride;
import com.k2bot.ai.chatbot.persistence.entity.MemoryPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;
import com.k2bot.ai.chatbot.persistence.entity.ModelRouteEntity;
import com.k2bot.ai.chatbot.persistence.entity.PromptTemplateEntity;
import com.k2bot.ai.chatbot.persistence.entity.RagPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.ResponsePolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.RouteType;
import com.k2bot.ai.chatbot.persistence.entity.SafetyPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.TenantAssistantOverrideEntity;
import com.k2bot.ai.chatbot.persistence.entity.ToolPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.ToolType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import tools.jackson.core.JacksonException;
import tools.jackson.core.type.TypeReference;
import tools.jackson.databind.ObjectMapper;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component
public class DefaultAssistantConfigResolver implements AssistantConfigResolver {

    private static final Logger log = LoggerFactory.getLogger(DefaultAssistantConfigResolver.class);

    private static final TypeReference<Map<String, Object>> MAP_TYPE = new TypeReference<>() {};

    // Platform defaults
    private static final double DEFAULT_TEMPERATURE = 0.7;
    private static final int DEFAULT_MAX_INPUT_TOKENS = 8192;
    private static final int DEFAULT_MAX_OUTPUT_TOKENS = 2048;
    private static final int DEFAULT_TOP_K = 5;
    private static final double DEFAULT_SIMILARITY_THRESHOLD = 0.7;
    private static final int DEFAULT_MESSAGE_WINDOW_SIZE = 10;
    private static final int DEFAULT_TTL_MINUTES = 60;
    private static final int DEFAULT_MAX_TOOL_CALLS = 5;
    private static final int DEFAULT_TOOL_TIMEOUT_MS = 30000;
    private static final String DEFAULT_TONE = "professional";
    private static final String DEFAULT_FORMAT = "text";
    private static final String DEFAULT_RETRIEVAL_STRATEGY = "similarity";

    private final ObjectMapper objectMapper;

    public DefaultAssistantConfigResolver(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ResolvedAssistantConfig resolve(RawAssistantConfigBundle bundle, RuntimeConfigOverride override) {
        String assistantCode = bundle.getAssistant().getAssistantCode();
        log.debug("Resolving config for assistantCode={} tenantId={}", assistantCode, bundle.getTenantId());

        try {
            ResolvedPromptConfig promptConfig = resolvePromptConfig(bundle);
            ResolvedModelRoutingConfig modelRoutingConfig = resolveModelRoutingConfig(bundle, override);
            ResolvedRagConfig ragConfig = resolveRagConfig(bundle, override);
            ResolvedMemoryConfig memoryConfig = resolveMemoryConfig(bundle, override);
            ResolvedToolConfig toolConfig = resolveToolConfig(bundle, override);
            ResolvedSafetyConfig safetyConfig = resolveSafetyConfig(bundle);
            ResolvedResponseConfig responseConfig = resolveResponseConfig(bundle, override);

            return new ResolvedAssistantConfig(
                    assistantCode,
                    bundle.getTenantId(),
                    bundle.getAssistant().getName(),
                    Boolean.TRUE.equals(bundle.getAssistant().getActive()),
                    promptConfig,
                    modelRoutingConfig,
                    ragConfig,
                    memoryConfig,
                    toolConfig,
                    safetyConfig,
                    responseConfig,
                    bundle.getAssistant().getConfigVersion(),
                    Instant.now()
            );
        } catch (AssistantConfigResolutionException e) {
            throw e;
        } catch (Exception e) {
            throw new AssistantConfigResolutionException(assistantCode, "Unexpected error during resolution", e);
        }
    }

    private ResolvedPromptConfig resolvePromptConfig(RawAssistantConfigBundle bundle) {
        String systemTemplate = null;
        String developerTemplate = null;
        Map<String, String> variables = new LinkedHashMap<>();
        List<String> guardrails = new ArrayList<>();
        String version = null;

        PromptTemplateEntity pt = bundle.getPromptTemplate();
        if (pt != null) {
            systemTemplate = pt.getSystemPromptTemplate();
            developerTemplate = pt.getDeveloperPromptTemplate();
            if (pt.getPromptVariables() != null) variables.putAll(pt.getPromptVariables());
            if (pt.getGuardrailInstructions() != null) guardrails.addAll(pt.getGuardrailInstructions());
            version = pt.getVersion();
        }

        return new ResolvedPromptConfig(systemTemplate, developerTemplate, variables, guardrails, version);
    }

    private ResolvedModelRoutingConfig resolveModelRoutingConfig(RawAssistantConfigBundle bundle, RuntimeConfigOverride override) {
        String defaultModel = null;
        String defaultProvider = null;
        double temperature = DEFAULT_TEMPERATURE;
        int maxInputTokens = DEFAULT_MAX_INPUT_TOKENS;
        FallbackPolicy fallbackPolicy = FallbackPolicy.USE_DEFAULT_MODEL;

        // Derive defaults from the first (lowest priority) simple route if available
        List<ResolvedModelRoute> routes = new ArrayList<>();
        for (ModelRouteEntity route : bundle.getModelRoutes()) {
            routes.add(mapRoute(route));
            if (defaultModel == null && route.getTargetModel() != null) {
                defaultModel = route.getTargetModel();
                defaultProvider = route.getTargetProvider();
            }
            if (route.getMaxInputTokens() != null) maxInputTokens = route.getMaxInputTokens();
            if (route.getTemperature() != null) temperature = route.getTemperature();
        }

        // Apply tenant override for model routing
        for (TenantAssistantOverrideEntity to : bundle.getTenantOverrides()) {
            if ("MODEL_HINT".equals(to.getOverrideType())) {
                Map<String, Object> payload = parseJson(to.getOverridePayloadJson());
                if (payload.containsKey("model")) defaultModel = (String) payload.get("model");
                if (payload.containsKey("provider")) defaultProvider = (String) payload.get("provider");
            }
        }

        // Apply runtime override
        if (override != null && override.getModelHint() != null) {
            defaultModel = override.getModelHint();
        }

        return new ResolvedModelRoutingConfig(defaultModel, defaultProvider, routes, fallbackPolicy, maxInputTokens, temperature);
    }

    private ResolvedModelRoute mapRoute(ModelRouteEntity entity) {
        return new ResolvedModelRoute(
                entity.getRouteName(),
                entity.getRouteType() != null ? entity.getRouteType() : RouteType.SIMPLE,
                entity.getMinPromptLength(),
                entity.getMaxPromptLength(),
                Boolean.TRUE.equals(entity.getRagEnabledOnly()),
                Boolean.TRUE.equals(entity.getToolsRequiredOnly()),
                Boolean.TRUE.equals(entity.getStructuredOutputOnly()),
                entity.getTargetProvider(),
                entity.getTargetModel(),
                entity.getMaxInputTokens() != null ? entity.getMaxInputTokens() : DEFAULT_MAX_INPUT_TOKENS,
                entity.getTemperature() != null ? entity.getTemperature() : DEFAULT_TEMPERATURE,
                entity.getPriority() != null ? entity.getPriority() : 100
        );
    }

    private ResolvedRagConfig resolveRagConfig(RawAssistantConfigBundle bundle, RuntimeConfigOverride override) {
        boolean enabled = false;
        String knowledgeBaseId = null;
        int topK = DEFAULT_TOP_K;
        double similarityThreshold = DEFAULT_SIMILARITY_THRESHOLD;
        String retrievalStrategy = DEFAULT_RETRIEVAL_STRATEGY;
        boolean citationsEnabled = false;
        boolean groundedAnswerRequired = false;
        Map<String, String> metadataFilters = new LinkedHashMap<>();

        RagPolicyEntity rag = bundle.getRagPolicy();
        if (rag != null) {
            if (rag.getRagEnabled() != null) enabled = rag.getRagEnabled();
            knowledgeBaseId = rag.getDefaultKnowledgeBaseId();
            if (rag.getTopK() != null) topK = rag.getTopK();
            if (rag.getSimilarityThreshold() != null) similarityThreshold = rag.getSimilarityThreshold();
            if (rag.getRetrievalStrategy() != null) retrievalStrategy = rag.getRetrievalStrategy();
            if (rag.getCitationsEnabled() != null) citationsEnabled = rag.getCitationsEnabled();
            if (rag.getGroundedAnswerRequired() != null) groundedAnswerRequired = rag.getGroundedAnswerRequired();
            if (rag.getMetadataFilters() != null) metadataFilters.putAll(rag.getMetadataFilters());
        }

        for (TenantAssistantOverrideEntity to : bundle.getTenantOverrides()) {
            if ("RAG_POLICY".equals(to.getOverrideType())) {
                Map<String, Object> payload = parseJson(to.getOverridePayloadJson());
                if (payload.containsKey("ragEnabled")) enabled = (Boolean) payload.get("ragEnabled");
                if (payload.containsKey("defaultKnowledgeBaseId")) knowledgeBaseId = (String) payload.get("defaultKnowledgeBaseId");
                if (payload.containsKey("topK")) topK = ((Number) payload.get("topK")).intValue();
                if (payload.containsKey("similarityThreshold")) similarityThreshold = ((Number) payload.get("similarityThreshold")).doubleValue();
            }
        }

        if (override != null && override.getKnowledgeBaseId() != null) {
            knowledgeBaseId = override.getKnowledgeBaseId();
            if (!enabled) enabled = true;
        }

        return new ResolvedRagConfig(enabled, knowledgeBaseId, topK, similarityThreshold,
                retrievalStrategy, citationsEnabled, groundedAnswerRequired, metadataFilters);
    }

    private ResolvedMemoryConfig resolveMemoryConfig(RawAssistantConfigBundle bundle, RuntimeConfigOverride override) {
        boolean enabled = false;
        MemoryStoreType storeType = MemoryStoreType.NONE;
        int messageWindowSize = DEFAULT_MESSAGE_WINDOW_SIZE;
        int ttlMinutes = DEFAULT_TTL_MINUTES;
        boolean persistChatHistory = true;
        boolean summarizeOldMessages = false;

        MemoryPolicyEntity mem = bundle.getMemoryPolicy();
        if (mem != null) {
            if (mem.getMemoryEnabled() != null) enabled = mem.getMemoryEnabled();
            if (mem.getStoreType() != null) storeType = mem.getStoreType();
            if (mem.getMessageWindowSize() != null) messageWindowSize = mem.getMessageWindowSize();
            if (mem.getTtlMinutes() != null) ttlMinutes = mem.getTtlMinutes();
            if (mem.getPersistChatHistory() != null) persistChatHistory = mem.getPersistChatHistory();
            if (mem.getSummarizeOldMessages() != null) summarizeOldMessages = mem.getSummarizeOldMessages();
        }

        for (TenantAssistantOverrideEntity to : bundle.getTenantOverrides()) {
            if ("MEMORY_POLICY".equals(to.getOverrideType())) {
                Map<String, Object> payload = parseJson(to.getOverridePayloadJson());
                if (payload.containsKey("memoryEnabled")) enabled = (Boolean) payload.get("memoryEnabled");
                if (payload.containsKey("messageWindowSize")) messageWindowSize = ((Number) payload.get("messageWindowSize")).intValue();
                if (payload.containsKey("ttlMinutes")) ttlMinutes = ((Number) payload.get("ttlMinutes")).intValue();
            }
        }

        if (override != null && override.getMemoryStoreType() != null) {
            storeType = override.getMemoryStoreType();
            if (!enabled && storeType != MemoryStoreType.NONE) enabled = true;
        }

        return new ResolvedMemoryConfig(enabled, storeType, messageWindowSize, ttlMinutes, persistChatHistory, summarizeOldMessages);
    }

    private ResolvedToolConfig resolveToolConfig(RawAssistantConfigBundle bundle, RuntimeConfigOverride override) {
        List<ResolvedToolDefinition> allowedTools = new ArrayList<>();
        boolean allowRuntimeSubset = true;
        int maxToolCalls = DEFAULT_MAX_TOOL_CALLS;
        int toolTimeoutMs = DEFAULT_TOOL_TIMEOUT_MS;

        for (ToolPolicyEntity tp : bundle.getToolPolicies()) {
            allowedTools.add(new ResolvedToolDefinition(
                    tp.getToolName(),
                    tp.getToolType() != null ? tp.getToolType() : ToolType.LOCAL_BEAN,
                    Boolean.TRUE.equals(tp.getRequiresApproval()),
                    tp.getTimeoutMs() != null ? tp.getTimeoutMs() : DEFAULT_TOOL_TIMEOUT_MS
            ));
            if (tp.getTimeoutMs() != null) toolTimeoutMs = tp.getTimeoutMs();
        }

        if (override != null && override.getEnabledToolNames() != null && !override.getEnabledToolNames().isEmpty()) {
            List<String> requested = override.getEnabledToolNames();
            allowedTools = allowedTools.stream()
                    .filter(t -> requested.contains(t.getToolName()))
                    .toList();
        }

        boolean toolsEnabled = !allowedTools.isEmpty();
        return new ResolvedToolConfig(toolsEnabled, allowedTools, allowRuntimeSubset, maxToolCalls, toolTimeoutMs);
    }

    private ResolvedSafetyConfig resolveSafetyConfig(RawAssistantConfigBundle bundle) {
        boolean blockUnknownTools = true;
        boolean blockWithoutRagWhenGrounded = true;
        boolean allowDirectAnswer = true;
        boolean maskSensitiveData = false;
        List<String> disallowedTopics = new ArrayList<>();

        SafetyPolicyEntity safety = bundle.getSafetyPolicy();
        if (safety != null) {
            if (safety.getBlockUnknownTools() != null) blockUnknownTools = safety.getBlockUnknownTools();
            if (safety.getBlockWithoutRagWhenGroundedMode() != null) blockWithoutRagWhenGrounded = safety.getBlockWithoutRagWhenGroundedMode();
            if (safety.getAllowDirectModelAnswerWithoutContext() != null) allowDirectAnswer = safety.getAllowDirectModelAnswerWithoutContext();
            if (safety.getMaskSensitiveDataInLogs() != null) maskSensitiveData = safety.getMaskSensitiveDataInLogs();
            if (safety.getDisallowedTopics() != null) disallowedTopics.addAll(safety.getDisallowedTopics());
        }

        return new ResolvedSafetyConfig(blockUnknownTools, blockWithoutRagWhenGrounded,
                allowDirectAnswer, maskSensitiveData, disallowedTopics);
    }

    private ResolvedResponseConfig resolveResponseConfig(RawAssistantConfigBundle bundle, RuntimeConfigOverride override) {
        String tone = DEFAULT_TONE;
        String format = DEFAULT_FORMAT;
        boolean citationRequired = false;
        boolean markdownEnabled = true;
        boolean streamEnabled = false;
        int maxOutputTokens = DEFAULT_MAX_OUTPUT_TOKENS;

        ResponsePolicyEntity resp = bundle.getResponsePolicy();
        if (resp != null) {
            if (resp.getDefaultTone() != null) tone = resp.getDefaultTone();
            if (resp.getDefaultFormat() != null) format = resp.getDefaultFormat();
            if (resp.getCitationRequired() != null) citationRequired = resp.getCitationRequired();
            if (resp.getMarkdownEnabled() != null) markdownEnabled = resp.getMarkdownEnabled();
            if (resp.getStreamEnabled() != null) streamEnabled = resp.getStreamEnabled();
            if (resp.getMaxOutputTokens() != null) maxOutputTokens = resp.getMaxOutputTokens();
        }

        for (TenantAssistantOverrideEntity to : bundle.getTenantOverrides()) {
            if ("RESPONSE_POLICY".equals(to.getOverrideType())) {
                Map<String, Object> payload = parseJson(to.getOverridePayloadJson());
                if (payload.containsKey("defaultTone")) tone = (String) payload.get("defaultTone");
                if (payload.containsKey("markdownEnabled")) markdownEnabled = (Boolean) payload.get("markdownEnabled");
                if (payload.containsKey("maxOutputTokens")) maxOutputTokens = ((Number) payload.get("maxOutputTokens")).intValue();
            }
        }

        if (override != null && override.getStreamingEnabled() != null) {
            streamEnabled = override.getStreamingEnabled();
        }

        return new ResolvedResponseConfig(tone, format, citationRequired, markdownEnabled, streamEnabled, maxOutputTokens);
    }

    private Map<String, Object> parseJson(String json) {
        if (json == null || json.isBlank()) {
            return Collections.emptyMap();
        }
        try {
            return objectMapper.readValue(json, MAP_TYPE);
        } catch (JacksonException e) {
            log.warn("Failed to parse tenant override payload JSON, skipping: {}", e.getMessage());
            return Collections.emptyMap();
        }
    }
}
