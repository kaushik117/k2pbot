package com.k2bot.ai.chatbot.prompt;

import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedMemoryConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedModelRoutingConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedPromptConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedRagConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedResponseConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedSafetyConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolConfig;
import com.k2bot.ai.chatbot.modelrouting.model.ModelSelectionResult;
import com.k2bot.ai.chatbot.orchestration.model.ChatRequest;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;
import com.k2bot.ai.chatbot.prompt.resolver.DefaultPromptVariableResolver;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPromptVariableResolverTest {

    private DefaultPromptVariableResolver resolver;

    @BeforeEach
    void setUp() {
        resolver = new DefaultPromptVariableResolver();
    }

    @Test
    void resolve_withRequestFields_mapsToVariables() {
        ChatRequest request = new ChatRequest();
        request.setAssistantCode("bot-1");
        request.setTenantId("tenant-a");
        request.setSessionId("sess-001");
        request.setUserId("user-42");
        request.setLocale("en");
        request.setChannel("web");

        PromptAssemblyInput input = new PromptAssemblyInput(request, buildMinimalConfig(null), null, null);

        Map<String, Object> vars = resolver.resolve(input);

        assertThat(vars).containsEntry("assistantCode", "bot-1");
        assertThat(vars).containsEntry("tenantId", "tenant-a");
        assertThat(vars).containsEntry("sessionId", "sess-001");
        assertThat(vars).containsEntry("userId", "user-42");
        assertThat(vars).containsEntry("locale", "en");
        assertThat(vars).containsEntry("channel", "web");
    }

    @Test
    void resolve_withDefaultVariables_includesConfigDefaults() {
        Map<String, String> defaults = Map.of("companyName", "Acme Corp", "language", "English");
        ResolvedPromptConfig promptConfig = new ResolvedPromptConfig(
                "You are a helpful assistant for {companyName}.", null, defaults, null, "v1");
        ResolvedAssistantConfig config = buildConfigWithPrompt(promptConfig);

        PromptAssemblyInput input = new PromptAssemblyInput(null, config, null, null);

        Map<String, Object> vars = resolver.resolve(input);

        assertThat(vars).containsEntry("companyName", "Acme Corp");
        assertThat(vars).containsEntry("language", "English");
    }

    @Test
    void resolve_withRuntimeVariables_overridesDefaults() {
        Map<String, String> defaults = Map.of("tone", "casual");
        ResolvedPromptConfig promptConfig = new ResolvedPromptConfig(
                "System prompt.", null, defaults, null, "v1");
        ResolvedAssistantConfig config = buildConfigWithPrompt(promptConfig);

        Map<String, Object> runtimeVars = Map.of("tone", "formal");
        PromptAssemblyInput input = new PromptAssemblyInput(null, config, null, runtimeVars);

        Map<String, Object> vars = resolver.resolve(input);

        assertThat(vars).containsEntry("tone", "formal");
    }

    @Test
    void resolve_withModelSelection_mapsModelFields() {
        ModelSelectionResult model = new ModelSelectionResult();
        model.setProvider("openai");
        model.setModelName("gpt-4o");

        PromptAssemblyInput input = new PromptAssemblyInput(null, buildMinimalConfig(null), model, null);

        Map<String, Object> vars = resolver.resolve(input);

        assertThat(vars).containsEntry("selectedModel", "gpt-4o");
        assertThat(vars).containsEntry("selectedProvider", "openai");
    }

    @Test
    void resolve_withNullRequest_doesNotThrow() {
        PromptAssemblyInput input = new PromptAssemblyInput(null, buildMinimalConfig(null), null, null);

        Map<String, Object> vars = resolver.resolve(input);

        assertThat(vars).isNotNull();
    }

    @Test
    void resolve_withRequestContext_mergesContext() {
        ChatRequest request = new ChatRequest();
        request.setContext(Map.of("productId", "PRD-42", "plan", "premium"));

        PromptAssemblyInput input = new PromptAssemblyInput(request, buildMinimalConfig(null), null, null);

        Map<String, Object> vars = resolver.resolve(input);

        assertThat(vars).containsEntry("productId", "PRD-42");
        assertThat(vars).containsEntry("plan", "premium");
    }

    private ResolvedAssistantConfig buildMinimalConfig(ResolvedPromptConfig promptConfig) {
        return buildConfigWithPrompt(promptConfig != null ? promptConfig
                : new ResolvedPromptConfig("System.", null, null, null, "v1"));
    }

    private ResolvedAssistantConfig buildConfigWithPrompt(ResolvedPromptConfig promptConfig) {
        return new ResolvedAssistantConfig(
                "test-bot", "tenant-1", "Test Bot", true,
                promptConfig,
                new ResolvedModelRoutingConfig(null, null, Collections.emptyList(), null, null, null),
                new ResolvedRagConfig(false, null, null, null, null, false, false, null),
                new ResolvedMemoryConfig(false, null, null, null, false, false),
                new ResolvedToolConfig(false, Collections.emptyList(), false, null, null),
                new ResolvedSafetyConfig(false, false, true, false, Collections.emptyList()),
                new ResolvedResponseConfig(null, null, false, false, false, null),
                "v1",
                Instant.now()
        );
    }
}
