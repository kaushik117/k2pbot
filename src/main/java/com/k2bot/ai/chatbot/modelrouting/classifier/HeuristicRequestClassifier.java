package com.k2bot.ai.chatbot.modelrouting.classifier;

import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedRagConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolConfig;
import com.k2bot.ai.chatbot.modelrouting.api.RequestClassifier;
import com.k2bot.ai.chatbot.modelrouting.model.RequestClassification;
import com.k2bot.ai.chatbot.modelrouting.model.RequestComplexity;
import com.k2bot.ai.chatbot.modelrouting.model.RoutingInput;
import org.springframework.stereotype.Component;

@Component
public class HeuristicRequestClassifier implements RequestClassifier {

    private static final int LONG_CONTEXT_THRESHOLD = 2000;
    private static final int MODERATE_THRESHOLD = 500;

    @Override
    public RequestClassification classify(RoutingInput input) {
        String message = resolveMessage(input);
        int messageLength = message.length();

        boolean longContextExpected = messageLength > LONG_CONTEXT_THRESHOLD;
        RequestComplexity complexity = classifyComplexity(messageLength);

        ResolvedAssistantConfig config = input.getResolvedConfig();
        boolean ragExpected = isRagExpected(config);
        boolean toolExpected = isToolExpected(config);

        String reason = buildReason(complexity, ragExpected, toolExpected, longContextExpected, messageLength);

        return new RequestClassification(
                complexity,
                ragExpected,
                toolExpected,
                false,
                longContextExpected,
                reason
        );
    }

    private String resolveMessage(RoutingInput input) {
        if (input.getChatRequest() == null || input.getChatRequest().getMessage() == null) {
            return "";
        }
        return input.getChatRequest().getMessage();
    }

    private RequestComplexity classifyComplexity(int messageLength) {
        if (messageLength > LONG_CONTEXT_THRESHOLD) {
            return RequestComplexity.COMPLEX;
        }
        if (messageLength > MODERATE_THRESHOLD) {
            return RequestComplexity.MODERATE;
        }
        return RequestComplexity.SIMPLE;
    }

    private boolean isRagExpected(ResolvedAssistantConfig config) {
        ResolvedRagConfig ragConfig = config.getRagConfig();
        return ragConfig != null && ragConfig.isEnabled();
    }

    private boolean isToolExpected(ResolvedAssistantConfig config) {
        ResolvedToolConfig toolConfig = config.getToolConfig();
        return toolConfig != null && toolConfig.isEnabled();
    }

    private String buildReason(
            RequestComplexity complexity,
            boolean ragExpected,
            boolean toolExpected,
            boolean longContextExpected,
            int messageLength) {
        StringBuilder sb = new StringBuilder();
        sb.append("complexity=").append(complexity.name());
        sb.append(", messageLength=").append(messageLength);
        if (longContextExpected) {
            sb.append(", longContext=true");
        }
        if (ragExpected) {
            sb.append(", rag=true");
        }
        if (toolExpected) {
            sb.append(", tools=true");
        }
        return sb.toString();
    }
}
