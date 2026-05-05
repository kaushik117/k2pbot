package com.k2bot.ai.chatbot.config.service;

import com.k2bot.ai.chatbot.config.api.AssistantConfigCache;
import com.k2bot.ai.chatbot.config.api.AssistantConfigLoader;
import com.k2bot.ai.chatbot.config.api.AssistantConfigProvider;
import com.k2bot.ai.chatbot.config.api.AssistantConfigResolver;
import com.k2bot.ai.chatbot.config.api.AssistantConfigValidator;
import com.k2bot.ai.chatbot.config.model.FallbackPolicy;
import com.k2bot.ai.chatbot.config.model.RawAssistantConfigBundle;
import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedMemoryConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedModelRoutingConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedRagConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedResponseConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolDefinition;
import com.k2bot.ai.chatbot.config.model.RuntimeConfigOverride;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class DefaultAssistantConfigProvider implements AssistantConfigProvider {

    private static final Logger log = LoggerFactory.getLogger(DefaultAssistantConfigProvider.class);

    private final AssistantConfigLoader loader;
    private final AssistantConfigResolver resolver;
    private final AssistantConfigValidator validator;
    private final AssistantConfigCache cache;

    public DefaultAssistantConfigProvider(
            AssistantConfigLoader loader,
            AssistantConfigResolver resolver,
            AssistantConfigValidator validator,
            AssistantConfigCache cache) {
        this.loader = loader;
        this.resolver = resolver;
        this.validator = validator;
        this.cache = cache;
    }

    @Override
    public ResolvedAssistantConfig getResolvedConfig(String assistantCode, String tenantId, RuntimeConfigOverride override) {
        String cacheKey = buildCacheKey(assistantCode, tenantId);

        ResolvedAssistantConfig baseConfig = cache.get(cacheKey).orElseGet(() -> {
            log.debug("Cache miss — loading and resolving config for assistantCode={} tenantId={}", assistantCode, tenantId);
            RawAssistantConfigBundle bundle = loader.load(assistantCode, tenantId);
            validator.validate(bundle);
            ResolvedAssistantConfig resolved = resolver.resolve(bundle, null);
            validator.validateResolved(resolved);
            cache.put(cacheKey, resolved);
            return resolved;
        });

        if (override == null || override.isEmpty()) {
            return baseConfig;
        }

        log.debug("Applying runtime override for assistantCode={}", assistantCode);
        return applyRuntimeOverride(baseConfig, override);
    }

    @Override
    public void evict(String assistantCode, String tenantId) {
        cache.evict(buildCacheKey(assistantCode, tenantId));
    }

    @Override
    public void evictAll() {
        cache.evictAll();
    }

    private String buildCacheKey(String assistantCode, String tenantId) {
        return assistantCode + ":" + (tenantId != null ? tenantId : "default");
    }

    private ResolvedAssistantConfig applyRuntimeOverride(ResolvedAssistantConfig base, RuntimeConfigOverride override) {
        ResolvedModelRoutingConfig routing = base.getModelRoutingConfig();
        if (override.getModelHint() != null) {
            routing = new ResolvedModelRoutingConfig(
                    override.getModelHint(),
                    base.getModelRoutingConfig().getDefaultProvider(),
                    base.getModelRoutingConfig().getRoutes(),
                    base.getModelRoutingConfig().getFallbackPolicy() != null
                            ? base.getModelRoutingConfig().getFallbackPolicy()
                            : FallbackPolicy.USE_DEFAULT_MODEL,
                    base.getModelRoutingConfig().getDefaultMaxInputTokens(),
                    base.getModelRoutingConfig().getDefaultTemperature()
            );
        }

        ResolvedRagConfig rag = base.getRagConfig();
        if (override.getKnowledgeBaseId() != null) {
            rag = new ResolvedRagConfig(
                    true,
                    override.getKnowledgeBaseId(),
                    base.getRagConfig().getTopK(),
                    base.getRagConfig().getSimilarityThreshold(),
                    base.getRagConfig().getRetrievalStrategy(),
                    base.getRagConfig().isCitationsEnabled(),
                    base.getRagConfig().isGroundedAnswerRequired(),
                    base.getRagConfig().getMetadataFilters()
            );
        }

        ResolvedMemoryConfig memory = base.getMemoryConfig();
        if (override.getMemoryStoreType() != null) {
            MemoryStoreType storeType = override.getMemoryStoreType();
            memory = new ResolvedMemoryConfig(
                    storeType != MemoryStoreType.NONE,
                    storeType,
                    base.getMemoryConfig().getMessageWindowSize(),
                    base.getMemoryConfig().getTtlMinutes(),
                    base.getMemoryConfig().isPersistChatHistory(),
                    base.getMemoryConfig().isSummarizeOldMessages()
            );
        }

        ResolvedToolConfig tools = base.getToolConfig();
        if (override.getEnabledToolNames() != null && !override.getEnabledToolNames().isEmpty()) {
            List<String> requested = override.getEnabledToolNames();
            List<ResolvedToolDefinition> filtered = new ArrayList<>();
            for (ResolvedToolDefinition t : base.getToolConfig().getAllowedTools()) {
                if (requested.contains(t.getToolName())) {
                    filtered.add(t);
                }
            }
            tools = new ResolvedToolConfig(
                    !filtered.isEmpty(),
                    filtered,
                    base.getToolConfig().isAllowRuntimeSubsetSelection(),
                    base.getToolConfig().getMaxToolCallsPerRequest(),
                    base.getToolConfig().getToolTimeoutMs()
            );
        }

        ResolvedResponseConfig response = base.getResponseConfig();
        if (override.getStreamingEnabled() != null) {
            response = new ResolvedResponseConfig(
                    base.getResponseConfig().getDefaultTone(),
                    base.getResponseConfig().getDefaultFormat(),
                    base.getResponseConfig().isCitationRequired(),
                    base.getResponseConfig().isMarkdownEnabled(),
                    override.getStreamingEnabled(),
                    base.getResponseConfig().getMaxOutputTokens()
            );
        }

        return new ResolvedAssistantConfig(
                base.getAssistantCode(),
                base.getTenantId(),
                base.getAssistantName(),
                base.isActive(),
                base.getPromptConfig(),
                routing,
                rag,
                memory,
                tools,
                base.getSafetyConfig(),
                response,
                base.getConfigVersion(),
                base.getResolvedAt()
        );
    }
}
