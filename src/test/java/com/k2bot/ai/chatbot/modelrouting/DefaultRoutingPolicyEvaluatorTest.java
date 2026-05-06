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
import com.k2bot.ai.chatbot.config.model.RuntimeConfigOverride;
import com.k2bot.ai.chatbot.modelrouting.evaluator.DefaultRoutingPolicyEvaluator;
import com.k2bot.ai.chatbot.modelrouting.exception.NoEligibleModelException;
import com.k2bot.ai.chatbot.modelrouting.model.ModelSelectionResult;
import com.k2bot.ai.chatbot.modelrouting.model.RequestClassification;
import com.k2bot.ai.chatbot.modelrouting.model.RequestComplexity;
import com.k2bot.ai.chatbot.modelrouting.model.RoutingInput;
import com.k2bot.ai.chatbot.orchestration.model.ChatRequest;
import com.k2bot.ai.chatbot.persistence.entity.RouteType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultRoutingPolicyEvaluatorTest {

    private DefaultRoutingPolicyEvaluator evaluator;

    @BeforeEach
    void setUp() {
        evaluator = new DefaultRoutingPolicyEvaluator();
    }

    @Test
    void evaluate_simpleRequest_usesDefaultModel() {
        ResolvedAssistantConfig config = buildConfig("gpt-4o-mini", "openai", Collections.emptyList(), FallbackPolicy.USE_DEFAULT_MODEL);
        RoutingInput input = buildInput("Hello", config);
        RequestClassification classification = simpleClassification();

        ModelSelectionResult result = evaluator.evaluate(input, classification);

        assertThat(result.getModelName()).isEqualTo("gpt-4o-mini");
        assertThat(result.getProvider()).isEqualTo("openai");
        assertThat(result.getRouteName()).isEqualTo("default");
    }

    @Test
    void evaluate_longContextRequest_matchesLongContextRoute() {
        ResolvedModelRoute longContextRoute = buildRoute("long-ctx", RouteType.LONG_CONTEXT, "anthropic", "claude-3-opus", 1);
        ResolvedAssistantConfig config = buildConfig("gpt-4o-mini", "openai", List.of(longContextRoute), FallbackPolicy.USE_DEFAULT_MODEL);
        RoutingInput input = buildInput("x".repeat(3000), config);
        RequestClassification classification = new RequestClassification(
                RequestComplexity.COMPLEX, false, false, false, true, "long");

        ModelSelectionResult result = evaluator.evaluate(input, classification);

        assertThat(result.getModelName()).isEqualTo("claude-3-opus");
        assertThat(result.getRouteName()).isEqualTo("long-ctx");
    }

    @Test
    void evaluate_toolHeavyRequest_matchesToolHeavyRoute() {
        ResolvedModelRoute toolRoute = buildRoute("tool-route", RouteType.TOOL_HEAVY, "openai", "gpt-4o", 1);
        ResolvedAssistantConfig config = buildConfig("gpt-4o-mini", "openai", List.of(toolRoute), FallbackPolicy.USE_DEFAULT_MODEL);
        RoutingInput input = buildInput("Order lookup", config);
        RequestClassification classification = new RequestClassification(
                RequestComplexity.SIMPLE, false, true, false, false, "tool");

        ModelSelectionResult result = evaluator.evaluate(input, classification);

        assertThat(result.getModelName()).isEqualTo("gpt-4o");
        assertThat(result.getRouteName()).isEqualTo("tool-route");
    }

    @Test
    void evaluate_ragRequest_matchesKnowledgeQaRoute() {
        ResolvedModelRoute ragRoute = buildRoute("rag-route", RouteType.KNOWLEDGE_QA, "openai", "gpt-4o", 1);
        ResolvedAssistantConfig config = buildConfig("gpt-4o-mini", "openai", List.of(ragRoute), FallbackPolicy.USE_DEFAULT_MODEL);
        RoutingInput input = buildInput("What is the policy?", config);
        RequestClassification classification = new RequestClassification(
                RequestComplexity.SIMPLE, true, false, false, false, "rag");

        ModelSelectionResult result = evaluator.evaluate(input, classification);

        assertThat(result.getModelName()).isEqualTo("gpt-4o");
        assertThat(result.getRouteName()).isEqualTo("rag-route");
    }

    @Test
    void evaluate_routePriorityOrder_firstMatchWins() {
        ResolvedModelRoute route1 = buildRoute("route-high", RouteType.SIMPLE, "openai", "gpt-4o", 1);
        ResolvedModelRoute route2 = buildRoute("route-low", RouteType.SIMPLE, "openai", "gpt-4o-mini", 2);
        ResolvedAssistantConfig config = buildConfig("default-model", "openai", List.of(route1, route2), FallbackPolicy.USE_DEFAULT_MODEL);
        RoutingInput input = buildInput("Hello", config);
        RequestClassification classification = simpleClassification();

        ModelSelectionResult result = evaluator.evaluate(input, classification);

        assertThat(result.getRouteName()).isEqualTo("route-high");
        assertThat(result.getModelName()).isEqualTo("gpt-4o");
    }

    @Test
    void evaluate_noMatchAndFailFast_throwsNoEligibleModelException() {
        ResolvedAssistantConfig config = buildConfig(null, null, Collections.emptyList(), FallbackPolicy.FAIL_FAST);
        RoutingInput input = buildInput("Hello", config);
        RequestClassification classification = simpleClassification();

        assertThatThrownBy(() -> evaluator.evaluate(input, classification))
                .isInstanceOf(NoEligibleModelException.class);
    }

    @Test
    void evaluate_withModelHint_overridesRouting() {
        ResolvedAssistantConfig config = buildConfig("gpt-4o-mini", "openai", Collections.emptyList(), FallbackPolicy.USE_DEFAULT_MODEL);
        ChatRequest request = new ChatRequest();
        request.setMessage("Hello");
        RuntimeConfigOverride override = new RuntimeConfigOverride();
        override.setModelHint("anthropic:claude-3-haiku");
        request.setRuntimeOverride(override);
        RoutingInput input = new RoutingInput(request, config);
        RequestClassification classification = simpleClassification();

        ModelSelectionResult result = evaluator.evaluate(input, classification);

        assertThat(result.getProvider()).isEqualTo("anthropic");
        assertThat(result.getModelName()).isEqualTo("claude-3-haiku");
        assertThat(result.getRouteName()).isEqualTo("hint");
    }

    @Test
    void evaluate_withModelHintNoProvider_setsModelOnly() {
        ResolvedAssistantConfig config = buildConfig("gpt-4o-mini", "openai", Collections.emptyList(), FallbackPolicy.USE_DEFAULT_MODEL);
        ChatRequest request = new ChatRequest();
        request.setMessage("Hello");
        RuntimeConfigOverride override = new RuntimeConfigOverride();
        override.setModelHint("gpt-4o");
        request.setRuntimeOverride(override);
        RoutingInput input = new RoutingInput(request, config);

        ModelSelectionResult result = evaluator.evaluate(input, simpleClassification());

        assertThat(result.getModelName()).isEqualTo("gpt-4o");
        assertThat(result.getProvider()).isNull();
    }

    @Test
    void evaluate_fallbackPopulated_whenRouteMatchedAndUseDefaultModelPolicy() {
        ResolvedModelRoute route = buildRoute("r1", RouteType.TOOL_HEAVY, "openai", "gpt-4o", 1);
        ResolvedAssistantConfig config = buildConfig("gpt-4o-mini", "openai", List.of(route), FallbackPolicy.USE_DEFAULT_MODEL);
        RoutingInput input = buildInput("Tool question", config);
        RequestClassification classification = new RequestClassification(
                RequestComplexity.SIMPLE, false, true, false, false, "tool");

        ModelSelectionResult result = evaluator.evaluate(input, classification);

        assertThat(result.getModelName()).isEqualTo("gpt-4o");
        assertThat(result.getFallback()).isNotNull();
        assertThat(result.getFallback().getModelName()).isEqualTo("gpt-4o-mini");
    }

    private RoutingInput buildInput(String message, ResolvedAssistantConfig config) {
        ChatRequest request = new ChatRequest();
        request.setMessage(message);
        return new RoutingInput(request, config);
    }

    private RequestClassification simpleClassification() {
        return new RequestClassification(RequestComplexity.SIMPLE, false, false, false, false, "simple");
    }

    private ResolvedModelRoute buildRoute(String name, RouteType type, String provider, String model, int priority) {
        return new ResolvedModelRoute(name, type, null, null, false, false, false, provider, model, null, null, priority);
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
