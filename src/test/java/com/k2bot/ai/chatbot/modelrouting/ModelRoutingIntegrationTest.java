package com.k2bot.ai.chatbot.modelrouting;

import com.k2bot.ai.chatbot.config.model.FallbackPolicy;
import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedMemoryConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedModelRoute;
import com.k2bot.ai.chatbot.config.model.ResolvedModelRoutingConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedPromptConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedRagConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedResponseConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedSafetyConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolConfig;
import com.k2bot.ai.chatbot.modelrouting.api.ChatOptionsFactory;
import com.k2bot.ai.chatbot.modelrouting.api.ModelRoutingService;
import com.k2bot.ai.chatbot.modelrouting.model.ModelSelectionResult;
import com.k2bot.ai.chatbot.modelrouting.model.RoutingInput;
import com.k2bot.ai.chatbot.modelrouting.support.ModelChatOptions;
import com.k2bot.ai.chatbot.orchestration.model.ChatRequest;
import com.k2bot.ai.chatbot.persistence.entity.RouteType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ModelRoutingIntegrationTest {

    @Autowired
    private ModelRoutingService modelRoutingService;

    @Autowired
    private ChatOptionsFactory chatOptionsFactory;

    @Test
    void select_defaultModel_whenNoRoutesMatch() {
        ResolvedAssistantConfig config = buildConfig(
                "gpt-4o-mini", "openai", Collections.emptyList(), FallbackPolicy.USE_DEFAULT_MODEL);
        RoutingInput input = buildInput("Hello", config);

        ModelSelectionResult result = modelRoutingService.select(input);

        assertThat(result.getModelName()).isEqualTo("gpt-4o-mini");
        assertThat(result.getProvider()).isEqualTo("openai");
    }

    @Test
    void select_longContextRoute_selectedForLongMessage() {
        ResolvedModelRoute longContextRoute = new ResolvedModelRoute(
                "long-ctx", RouteType.LONG_CONTEXT, null, null,
                false, false, false, "anthropic", "claude-3-opus", 8192, 0.5, 1);

        ResolvedAssistantConfig config = buildConfig(
                "gpt-4o-mini", "openai", List.of(longContextRoute), FallbackPolicy.USE_DEFAULT_MODEL);
        RoutingInput input = buildInput("x".repeat(2500), config);

        ModelSelectionResult result = modelRoutingService.select(input);

        assertThat(result.getModelName()).isEqualTo("claude-3-opus");
        assertThat(result.getRouteName()).isEqualTo("long-ctx");
    }

    @Test
    void select_toolHeavyRoute_selectedWhenToolsEnabled() {
        ResolvedModelRoute toolRoute = new ResolvedModelRoute(
                "tool-heavy", RouteType.TOOL_HEAVY, null, null,
                false, true, false, "openai", "gpt-4o", 4096, 0.3, 1);

        ResolvedToolConfig toolConfig = new ResolvedToolConfig(true, Collections.emptyList(), false, 3, 5000);

        ResolvedAssistantConfig config = new ResolvedAssistantConfig(
                "tool-bot", "tenant-1", "Tool Bot", true,
                new ResolvedPromptConfig("System", null, null, null, "v1"),
                new ResolvedModelRoutingConfig("gpt-4o-mini", "openai", List.of(toolRoute), FallbackPolicy.USE_DEFAULT_MODEL, 4096, 0.7),
                new ResolvedRagConfig(false, null, null, null, null, false, false, null),
                new ResolvedMemoryConfig(false, null, null, null, false, false),
                toolConfig,
                new ResolvedSafetyConfig(false, false, true, false, Collections.emptyList()),
                new ResolvedResponseConfig(null, null, false, false, false, 2000),
                "cfg-v1", Instant.now()
        );
        RoutingInput input = buildInput("Order status", config);

        ModelSelectionResult result = modelRoutingService.select(input);

        assertThat(result.getModelName()).isEqualTo("gpt-4o");
        assertThat(result.getRouteName()).isEqualTo("tool-heavy");
    }

    @Test
    void chatOptionsFactory_producesOptionsFromSelectionResult() {
        ResolvedAssistantConfig config = buildConfig(
                "gpt-4o-mini", "openai", Collections.emptyList(), FallbackPolicy.USE_DEFAULT_MODEL);
        RoutingInput input = buildInput("Hello", config);

        ModelSelectionResult result = modelRoutingService.select(input);
        ModelChatOptions options = chatOptionsFactory.create(result, config);

        assertThat(options.getModel()).isEqualTo("gpt-4o-mini");
        assertThat(options.getProvider()).isEqualTo("openai");
        assertThat(options.getTemperature()).isEqualTo(0.7);
        assertThat(options.getMaxOutputTokens()).isEqualTo(2000);
    }

    private RoutingInput buildInput(String message, ResolvedAssistantConfig config) {
        ChatRequest request = new ChatRequest();
        request.setAssistantCode(config.getAssistantCode());
        request.setTenantId(config.getTenantId());
        request.setMessage(message);
        return new RoutingInput(request, config);
    }

    private ResolvedAssistantConfig buildConfig(
            String defaultModel,
            String defaultProvider,
            List<ResolvedModelRoute> routes,
            FallbackPolicy fallbackPolicy) {
        return new ResolvedAssistantConfig(
                "test-bot", "tenant-1", "Test Bot", true,
                new ResolvedPromptConfig("System prompt", null, null, null, "v1"),
                new ResolvedModelRoutingConfig(defaultModel, defaultProvider, routes, fallbackPolicy, 4096, 0.7),
                new ResolvedRagConfig(false, null, null, null, null, false, false, null),
                new ResolvedMemoryConfig(false, null, null, null, false, false),
                new ResolvedToolConfig(false, Collections.emptyList(), false, null, null),
                new ResolvedSafetyConfig(false, false, true, false, Collections.emptyList()),
                new ResolvedResponseConfig(null, null, false, false, false, 2000),
                "cfg-v1", Instant.now()
        );
    }
}
