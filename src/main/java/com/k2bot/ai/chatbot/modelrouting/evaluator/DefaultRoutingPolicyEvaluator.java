package com.k2bot.ai.chatbot.modelrouting.evaluator;

import com.k2bot.ai.chatbot.config.model.FallbackPolicy;
import com.k2bot.ai.chatbot.config.model.ResolvedModelRoute;
import com.k2bot.ai.chatbot.config.model.ResolvedModelRoutingConfig;
import com.k2bot.ai.chatbot.config.model.RuntimeConfigOverride;
import com.k2bot.ai.chatbot.modelrouting.api.RoutingPolicyEvaluator;
import com.k2bot.ai.chatbot.modelrouting.exception.NoEligibleModelException;
import com.k2bot.ai.chatbot.modelrouting.model.FallbackSelection;
import com.k2bot.ai.chatbot.modelrouting.model.ModelSelectionResult;
import com.k2bot.ai.chatbot.modelrouting.model.RequestClassification;
import com.k2bot.ai.chatbot.modelrouting.model.RoutingInput;
import com.k2bot.ai.chatbot.orchestration.model.ChatRequest;
import com.k2bot.ai.chatbot.persistence.entity.RouteType;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class DefaultRoutingPolicyEvaluator implements RoutingPolicyEvaluator {

    @Override
    public ModelSelectionResult evaluate(RoutingInput input, RequestClassification classification) {
        ResolvedModelRoutingConfig routingConfig = input.getResolvedConfig().getModelRoutingConfig();

        String modelHint = resolveModelHint(input.getChatRequest());
        if (modelHint != null && !modelHint.isBlank()) {
            return buildFromModelHint(modelHint, routingConfig);
        }

        List<ResolvedModelRoute> routes = routingConfig != null ? routingConfig.getRoutes() : List.of();
        int messageLength = resolveMessageLength(input.getChatRequest());

        for (ResolvedModelRoute route : routes) {
            if (routeMatches(route, classification, messageLength)) {
                return buildFromRoute(route, routingConfig);
            }
        }

        return buildDefault(routingConfig);
    }

    private String resolveModelHint(ChatRequest request) {
        if (request == null || request.getRuntimeOverride() == null) {
            return null;
        }
        return request.getRuntimeOverride().getModelHint();
    }

    private int resolveMessageLength(ChatRequest request) {
        if (request == null || request.getMessage() == null) {
            return 0;
        }
        return request.getMessage().length();
    }

    private boolean routeMatches(ResolvedModelRoute route, RequestClassification classification, int messageLength) {
        RouteType routeType = route.getRouteType();

        if (routeType == RouteType.LONG_CONTEXT && !classification.isLongContextExpected()) {
            return false;
        }
        if (routeType == RouteType.TOOL_HEAVY && !classification.isToolExpected()) {
            return false;
        }
        if (routeType == RouteType.KNOWLEDGE_QA && !classification.isRagExpected()) {
            return false;
        }
        if (routeType == RouteType.STRUCTURED_OUTPUT && !classification.isStructuredOutputExpected()) {
            return false;
        }

        if (route.isRagEnabledOnly() && !classification.isRagExpected()) {
            return false;
        }
        if (route.isToolsRequiredOnly() && !classification.isToolExpected()) {
            return false;
        }
        if (route.isStructuredOutputOnly() && !classification.isStructuredOutputExpected()) {
            return false;
        }

        if (route.getMinPromptLength() != null && messageLength < route.getMinPromptLength()) {
            return false;
        }
        if (route.getMaxPromptLength() != null && messageLength > route.getMaxPromptLength()) {
            return false;
        }

        return true;
    }

    private ModelSelectionResult buildFromRoute(ResolvedModelRoute route, ResolvedModelRoutingConfig routingConfig) {
        ModelSelectionResult result = new ModelSelectionResult();
        result.setProvider(route.getTargetProvider());
        result.setModelName(route.getTargetModel());
        result.setTemperature(coalesce(route.getTemperature(), defaultTemperature(routingConfig)));
        result.setMaxInputTokens(coalesce(route.getMaxInputTokens(), defaultMaxInputTokens(routingConfig)));
        result.setRouteName(route.getRouteName());
        result.setSelectionReason("Matched route: " + route.getRouteName());
        result.setFallback(buildFallback(routingConfig));
        return result;
    }

    private ModelSelectionResult buildDefault(ResolvedModelRoutingConfig routingConfig) {
        if (routingConfig == null || isBlank(routingConfig.getDefaultModel())) {
            throw new NoEligibleModelException(
                    "No matching route found and no default model configured");
        }
        if (routingConfig.getFallbackPolicy() == FallbackPolicy.FAIL_FAST) {
            throw new NoEligibleModelException(
                    "No matching route found and fallback policy is FAIL_FAST");
        }
        ModelSelectionResult result = new ModelSelectionResult();
        result.setProvider(routingConfig.getDefaultProvider());
        result.setModelName(routingConfig.getDefaultModel());
        result.setTemperature(routingConfig.getDefaultTemperature());
        result.setMaxInputTokens(routingConfig.getDefaultMaxInputTokens());
        result.setRouteName("default");
        result.setSelectionReason("No matching route; using default model");
        return result;
    }

    private ModelSelectionResult buildFromModelHint(String hint, ResolvedModelRoutingConfig routingConfig) {
        ModelSelectionResult result = new ModelSelectionResult();
        if (hint.contains(":")) {
            String[] parts = hint.split(":", 2);
            result.setProvider(parts[0]);
            result.setModelName(parts[1]);
        } else {
            result.setModelName(hint);
        }
        result.setTemperature(defaultTemperature(routingConfig));
        result.setMaxInputTokens(defaultMaxInputTokens(routingConfig));
        result.setRouteName("hint");
        result.setSelectionReason("Model selected via runtime hint: " + hint);
        result.setFallback(buildFallback(routingConfig));
        return result;
    }

    private FallbackSelection buildFallback(ResolvedModelRoutingConfig routingConfig) {
        if (routingConfig == null) {
            return null;
        }
        if (routingConfig.getFallbackPolicy() != FallbackPolicy.USE_DEFAULT_MODEL) {
            return null;
        }
        if (isBlank(routingConfig.getDefaultModel())) {
            return null;
        }
        return new FallbackSelection(
                routingConfig.getDefaultProvider(),
                routingConfig.getDefaultModel(),
                "Fallback to default model per USE_DEFAULT_MODEL policy"
        );
    }

    private Double defaultTemperature(ResolvedModelRoutingConfig config) {
        return config != null ? config.getDefaultTemperature() : null;
    }

    private Integer defaultMaxInputTokens(ResolvedModelRoutingConfig config) {
        return config != null ? config.getDefaultMaxInputTokens() : null;
    }

    private <T> T coalesce(T first, T second) {
        return first != null ? first : second;
    }

    private boolean isBlank(String s) {
        return s == null || s.isBlank();
    }
}
