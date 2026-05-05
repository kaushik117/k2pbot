package com.k2bot.ai.chatbot.config.resolver;

import com.k2bot.ai.chatbot.config.model.FallbackPolicy;
import com.k2bot.ai.chatbot.config.model.RawAssistantConfigBundle;
import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.RuntimeConfigOverride;
import com.k2bot.ai.chatbot.persistence.entity.AssistantEntity;
import com.k2bot.ai.chatbot.persistence.entity.MemoryPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;
import com.k2bot.ai.chatbot.persistence.entity.PromptTemplateEntity;
import com.k2bot.ai.chatbot.persistence.entity.RagPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.ResponsePolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.SafetyPolicyEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import tools.jackson.databind.ObjectMapper;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultAssistantConfigResolverTest {

    private DefaultAssistantConfigResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new DefaultAssistantConfigResolver(new ObjectMapper());
    }

    @Test
    void resolve_withMinimalBundle_returnsPlatformDefaults() {
        RawAssistantConfigBundle bundle = minimalBundle("test-bot", "tenant-1");

        ResolvedAssistantConfig config = resolver.resolve(bundle, null);

        assertThat(config.getAssistantCode()).isEqualTo("test-bot");
        assertThat(config.getTenantId()).isEqualTo("tenant-1");
        assertThat(config.isActive()).isTrue();
        assertThat(config.getMemoryConfig().isEnabled()).isFalse();
        assertThat(config.getMemoryConfig().getStoreType()).isEqualTo(MemoryStoreType.NONE);
        assertThat(config.getMemoryConfig().getMessageWindowSize()).isEqualTo(10);
        assertThat(config.getRagConfig().isEnabled()).isFalse();
        assertThat(config.getRagConfig().getTopK()).isEqualTo(5);
        assertThat(config.getToolConfig().isEnabled()).isFalse();
        assertThat(config.getResponseConfig().getDefaultTone()).isEqualTo("professional");
        assertThat(config.getModelRoutingConfig().getRoutes()).isEmpty();
        assertThat(config.getResolvedAt()).isNotNull();
    }

    @Test
    void resolve_withMemoryPolicy_appliesEntityValues() {
        AssistantEntity assistant = buildAssistant("mem-bot");
        MemoryPolicyEntity memPolicy = new MemoryPolicyEntity();
        memPolicy.setMemoryEnabled(true);
        memPolicy.setStoreType(MemoryStoreType.IN_MEMORY);
        memPolicy.setMessageWindowSize(20);
        memPolicy.setTtlMinutes(120);
        memPolicy.setPersistChatHistory(false);
        memPolicy.setSummarizeOldMessages(true);

        RawAssistantConfigBundle bundle = new RawAssistantConfigBundle(
                "tenant-1", assistant, null, Collections.emptyList(),
                memPolicy, null, Collections.emptyList(), null, null, Collections.emptyList()
        );

        ResolvedAssistantConfig config = resolver.resolve(bundle, null);

        assertThat(config.getMemoryConfig().isEnabled()).isTrue();
        assertThat(config.getMemoryConfig().getStoreType()).isEqualTo(MemoryStoreType.IN_MEMORY);
        assertThat(config.getMemoryConfig().getMessageWindowSize()).isEqualTo(20);
        assertThat(config.getMemoryConfig().getTtlMinutes()).isEqualTo(120);
        assertThat(config.getMemoryConfig().isPersistChatHistory()).isFalse();
        assertThat(config.getMemoryConfig().isSummarizeOldMessages()).isTrue();
    }

    @Test
    void resolve_withRagPolicy_appliesEntityValues() {
        AssistantEntity assistant = buildAssistant("rag-bot");
        RagPolicyEntity ragPolicy = new RagPolicyEntity();
        ragPolicy.setRagEnabled(true);
        ragPolicy.setDefaultKnowledgeBaseId("kb-finance");
        ragPolicy.setTopK(3);
        ragPolicy.setSimilarityThreshold(0.85);
        ragPolicy.setCitationsEnabled(true);
        ragPolicy.setGroundedAnswerRequired(true);

        RawAssistantConfigBundle bundle = new RawAssistantConfigBundle(
                "tenant-1", assistant, null, Collections.emptyList(),
                null, ragPolicy, Collections.emptyList(), null, null, Collections.emptyList()
        );

        ResolvedAssistantConfig config = resolver.resolve(bundle, null);

        assertThat(config.getRagConfig().isEnabled()).isTrue();
        assertThat(config.getRagConfig().getDefaultKnowledgeBaseId()).isEqualTo("kb-finance");
        assertThat(config.getRagConfig().getTopK()).isEqualTo(3);
        assertThat(config.getRagConfig().getSimilarityThreshold()).isEqualTo(0.85);
        assertThat(config.getRagConfig().isCitationsEnabled()).isTrue();
        assertThat(config.getRagConfig().isGroundedAnswerRequired()).isTrue();
    }

    @Test
    void resolve_withRuntimeOverride_appliesModelHint() {
        RawAssistantConfigBundle bundle = minimalBundle("test-bot", "tenant-1");
        RuntimeConfigOverride override = new RuntimeConfigOverride();
        override.setModelHint("gpt-4o");

        ResolvedAssistantConfig config = resolver.resolve(bundle, override);

        assertThat(config.getModelRoutingConfig().getDefaultModel()).isEqualTo("gpt-4o");
    }

    @Test
    void resolve_withRuntimeOverride_appliesKnowledgeBaseId() {
        RawAssistantConfigBundle bundle = minimalBundle("test-bot", "tenant-1");
        RuntimeConfigOverride override = new RuntimeConfigOverride();
        override.setKnowledgeBaseId("kb-override-123");

        ResolvedAssistantConfig config = resolver.resolve(bundle, override);

        assertThat(config.getRagConfig().getDefaultKnowledgeBaseId()).isEqualTo("kb-override-123");
        assertThat(config.getRagConfig().isEnabled()).isTrue();
    }

    @Test
    void resolve_withRuntimeOverride_appliesStreamingEnabled() {
        RawAssistantConfigBundle bundle = minimalBundle("test-bot", "tenant-1");
        RuntimeConfigOverride override = new RuntimeConfigOverride();
        override.setStreamingEnabled(true);

        ResolvedAssistantConfig config = resolver.resolve(bundle, override);

        assertThat(config.getResponseConfig().isStreamEnabled()).isTrue();
    }

    @Test
    void resolve_withPromptTemplate_returnsTemplateContent() {
        AssistantEntity assistant = buildAssistant("prompt-bot");
        PromptTemplateEntity pt = new PromptTemplateEntity();
        pt.setSystemPromptTemplate("You are a helpful assistant.");
        pt.setDeveloperPromptTemplate("Be concise.");
        pt.setVersion("v1.0");

        RawAssistantConfigBundle bundle = new RawAssistantConfigBundle(
                "tenant-1", assistant, pt, Collections.emptyList(),
                null, null, Collections.emptyList(), null, null, Collections.emptyList()
        );

        ResolvedAssistantConfig config = resolver.resolve(bundle, null);

        assertThat(config.getPromptConfig().getSystemPromptTemplate()).isEqualTo("You are a helpful assistant.");
        assertThat(config.getPromptConfig().getDeveloperPromptTemplate()).isEqualTo("Be concise.");
        assertThat(config.getPromptConfig().getPromptVersion()).isEqualTo("v1.0");
    }

    @Test
    void resolve_withResponsePolicy_appliesEntityValues() {
        AssistantEntity assistant = buildAssistant("resp-bot");
        ResponsePolicyEntity resp = new ResponsePolicyEntity();
        resp.setDefaultTone("friendly");
        resp.setDefaultFormat("markdown");
        resp.setMarkdownEnabled(true);
        resp.setStreamEnabled(true);
        resp.setMaxOutputTokens(4096);

        RawAssistantConfigBundle bundle = new RawAssistantConfigBundle(
                "tenant-1", assistant, null, Collections.emptyList(),
                null, null, Collections.emptyList(), null, resp, Collections.emptyList()
        );

        ResolvedAssistantConfig config = resolver.resolve(bundle, null);

        assertThat(config.getResponseConfig().getDefaultTone()).isEqualTo("friendly");
        assertThat(config.getResponseConfig().isStreamEnabled()).isTrue();
        assertThat(config.getResponseConfig().getMaxOutputTokens()).isEqualTo(4096);
    }

    @Test
    void resolve_withSafetyPolicy_appliesEntityValues() {
        AssistantEntity assistant = buildAssistant("safe-bot");
        SafetyPolicyEntity safety = new SafetyPolicyEntity();
        safety.setBlockUnknownTools(false);
        safety.setMaskSensitiveDataInLogs(true);
        safety.setDisallowedTopics(List.of("violence", "illegal"));

        RawAssistantConfigBundle bundle = new RawAssistantConfigBundle(
                "tenant-1", assistant, null, Collections.emptyList(),
                null, null, Collections.emptyList(), safety, null, Collections.emptyList()
        );

        ResolvedAssistantConfig config = resolver.resolve(bundle, null);

        assertThat(config.getSafetyConfig().isBlockUnknownTools()).isFalse();
        assertThat(config.getSafetyConfig().isMaskSensitiveDataInLogs()).isTrue();
        assertThat(config.getSafetyConfig().getDisallowedTopics()).containsExactly("violence", "illegal");
    }

    private RawAssistantConfigBundle minimalBundle(String assistantCode, String tenantId) {
        return new RawAssistantConfigBundle(
                tenantId,
                buildAssistant(assistantCode),
                null,
                Collections.emptyList(),
                null, null,
                Collections.emptyList(),
                null, null,
                Collections.emptyList()
        );
    }

    private AssistantEntity buildAssistant(String code) {
        AssistantEntity assistant = new AssistantEntity();
        assistant.setAssistantCode(code);
        assistant.setName("Test Assistant");
        assistant.setActive(true);
        assistant.setConfigVersion("v1");
        return assistant;
    }
}
