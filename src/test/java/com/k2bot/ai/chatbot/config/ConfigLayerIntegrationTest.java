package com.k2bot.ai.chatbot.config;

import com.k2bot.ai.chatbot.config.api.AssistantConfigProvider;
import com.k2bot.ai.chatbot.config.exception.AssistantConfigNotFoundException;
import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.RuntimeConfigOverride;
import com.k2bot.ai.chatbot.persistence.entity.AssistantEntity;
import com.k2bot.ai.chatbot.persistence.entity.MemoryPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;
import com.k2bot.ai.chatbot.persistence.entity.PromptTemplateEntity;
import com.k2bot.ai.chatbot.persistence.entity.RagPolicyEntity;
import com.k2bot.ai.chatbot.persistence.entity.ResponsePolicyEntity;
import com.k2bot.ai.chatbot.persistence.repository.AssistantRepository;
import com.k2bot.ai.chatbot.persistence.repository.MemoryPolicyRepository;
import com.k2bot.ai.chatbot.persistence.repository.PromptTemplateRepository;
import com.k2bot.ai.chatbot.persistence.repository.RagPolicyRepository;
import com.k2bot.ai.chatbot.persistence.repository.ResponsePolicyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class ConfigLayerIntegrationTest {

    @Autowired
    private AssistantConfigProvider assistantConfigProvider;

    @Autowired
    private AssistantRepository assistantRepository;

    @Autowired
    private PromptTemplateRepository promptTemplateRepository;

    @Autowired
    private MemoryPolicyRepository memoryPolicyRepository;

    @Autowired
    private RagPolicyRepository ragPolicyRepository;

    @Autowired
    private ResponsePolicyRepository responsePolicyRepository;

    private AssistantEntity seededAssistant;

    @BeforeEach
    void setUp() {
        assistantConfigProvider.evictAll();
        seededAssistant = seedAssistant("integration-bot");
        seedPromptTemplate(seededAssistant, "You are an integration test assistant.", "v1.0");
    }

    @Test
    void getResolvedConfig_withSeededAssistant_returnsResolvedConfig() {
        ResolvedAssistantConfig config = assistantConfigProvider.getResolvedConfig(
                "integration-bot", "tenant-1", null);

        assertThat(config).isNotNull();
        assertThat(config.getAssistantCode()).isEqualTo("integration-bot");
        assertThat(config.isActive()).isTrue();
        assertThat(config.getPromptConfig().getSystemPromptTemplate()).isEqualTo("You are an integration test assistant.");
        assertThat(config.getPromptConfig().getPromptVersion()).isEqualTo("v1.0");
        assertThat(config.getResolvedAt()).isNotNull();
    }

    @Test
    void getResolvedConfig_calledTwice_returnsCachedResult() {
        ResolvedAssistantConfig first = assistantConfigProvider.getResolvedConfig(
                "integration-bot", "tenant-1", null);
        ResolvedAssistantConfig second = assistantConfigProvider.getResolvedConfig(
                "integration-bot", "tenant-1", null);

        assertThat(first).isSameAs(second);
    }

    @Test
    void getResolvedConfig_afterEvict_returnsRefreshedResult() {
        ResolvedAssistantConfig first = assistantConfigProvider.getResolvedConfig(
                "integration-bot", "tenant-1", null);

        assistantConfigProvider.evict("integration-bot", "tenant-1");

        ResolvedAssistantConfig second = assistantConfigProvider.getResolvedConfig(
                "integration-bot", "tenant-1", null);

        assertThat(first).isNotSameAs(second);
        assertThat(second.getAssistantCode()).isEqualTo("integration-bot");
    }

    @Test
    void getResolvedConfig_withUnknownAssistant_throwsNotFoundException() {
        assertThatThrownBy(() ->
                assistantConfigProvider.getResolvedConfig("nonexistent-bot", "tenant-1", null))
                .isInstanceOf(AssistantConfigNotFoundException.class)
                .hasMessageContaining("nonexistent-bot");
    }

    @Test
    void getResolvedConfig_withInactiveAssistant_throwsNotFoundException() {
        AssistantEntity inactive = seedAssistant("inactive-bot");
        inactive.setActive(false);
        assistantRepository.save(inactive);
        seedPromptTemplate(inactive, "System prompt", "v1");

        // The loader queries findByAssistantCodeAndActiveTrue so inactive assistants are simply not found
        assertThatThrownBy(() ->
                assistantConfigProvider.getResolvedConfig("inactive-bot", "tenant-1", null))
                .isInstanceOf(AssistantConfigNotFoundException.class)
                .hasMessageContaining("inactive-bot");
    }

    @Test
    void getResolvedConfig_withMemoryPolicy_returnsResolvedMemoryConfig() {
        MemoryPolicyEntity memPolicy = new MemoryPolicyEntity();
        memPolicy.setAssistant(seededAssistant);
        memPolicy.setMemoryEnabled(true);
        memPolicy.setStoreType(MemoryStoreType.IN_MEMORY);
        memPolicy.setMessageWindowSize(15);
        memPolicy.setTtlMinutes(45);
        memPolicy.setPersistChatHistory(true);
        memPolicy.setSummarizeOldMessages(false);
        memoryPolicyRepository.save(memPolicy);

        ResolvedAssistantConfig config = assistantConfigProvider.getResolvedConfig(
                "integration-bot", "tenant-1", null);

        assertThat(config.getMemoryConfig().isEnabled()).isTrue();
        assertThat(config.getMemoryConfig().getStoreType()).isEqualTo(MemoryStoreType.IN_MEMORY);
        assertThat(config.getMemoryConfig().getMessageWindowSize()).isEqualTo(15);
        assertThat(config.getMemoryConfig().getTtlMinutes()).isEqualTo(45);
    }

    @Test
    void getResolvedConfig_withRagPolicy_returnsResolvedRagConfig() {
        RagPolicyEntity ragPolicy = new RagPolicyEntity();
        ragPolicy.setAssistant(seededAssistant);
        ragPolicy.setRagEnabled(true);
        ragPolicy.setDefaultKnowledgeBaseId("kb-test-001");
        ragPolicy.setTopK(5);
        ragPolicy.setSimilarityThreshold(0.75);
        ragPolicy.setCitationsEnabled(true);
        ragPolicyRepository.save(ragPolicy);

        ResolvedAssistantConfig config = assistantConfigProvider.getResolvedConfig(
                "integration-bot", "tenant-1", null);

        assertThat(config.getRagConfig().isEnabled()).isTrue();
        assertThat(config.getRagConfig().getDefaultKnowledgeBaseId()).isEqualTo("kb-test-001");
        assertThat(config.getRagConfig().isCitationsEnabled()).isTrue();
    }

    @Test
    void getResolvedConfig_withRuntimeOverride_appliesModelHint() {
        RuntimeConfigOverride override = new RuntimeConfigOverride();
        override.setModelHint("claude-3-sonnet");

        ResolvedAssistantConfig config = assistantConfigProvider.getResolvedConfig(
                "integration-bot", "tenant-1", override);

        assertThat(config.getModelRoutingConfig().getDefaultModel()).isEqualTo("claude-3-sonnet");
    }

    @Test
    void getResolvedConfig_withRuntimeOverride_doesNotCacheOverriddenResult() {
        RuntimeConfigOverride override = new RuntimeConfigOverride();
        override.setModelHint("gpt-4o");

        assistantConfigProvider.getResolvedConfig("integration-bot", "tenant-1", override);

        ResolvedAssistantConfig noOverride = assistantConfigProvider.getResolvedConfig(
                "integration-bot", "tenant-1", null);

        assertThat(noOverride.getModelRoutingConfig().getDefaultModel()).isNull();
    }

    @Test
    void getResolvedConfig_withResponsePolicy_returnsResolvedResponseConfig() {
        ResponsePolicyEntity resp = new ResponsePolicyEntity();
        resp.setAssistant(seededAssistant);
        resp.setDefaultTone("formal");
        resp.setMarkdownEnabled(true);
        resp.setMaxOutputTokens(1024);
        responsePolicyRepository.save(resp);

        ResolvedAssistantConfig config = assistantConfigProvider.getResolvedConfig(
                "integration-bot", "tenant-1", null);

        assertThat(config.getResponseConfig().getDefaultTone()).isEqualTo("formal");
        assertThat(config.getResponseConfig().isMarkdownEnabled()).isTrue();
        assertThat(config.getResponseConfig().getMaxOutputTokens()).isEqualTo(1024);
    }

    private AssistantEntity seedAssistant(String code) {
        AssistantEntity assistant = new AssistantEntity();
        assistant.setAssistantCode(code);
        assistant.setName("Test Bot - " + code);
        assistant.setActive(true);
        assistant.setConfigVersion("v1");
        return assistantRepository.save(assistant);
    }

    private void seedPromptTemplate(AssistantEntity assistant, String systemPrompt, String version) {
        PromptTemplateEntity pt = new PromptTemplateEntity();
        pt.setAssistant(assistant);
        pt.setSystemPromptTemplate(systemPrompt);
        pt.setVersion(version);
        pt.setActive(true);
        promptTemplateRepository.save(pt);
    }
}
