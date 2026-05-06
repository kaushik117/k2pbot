package com.k2bot.ai.chatbot.modelrouting;

import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedMemoryConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedModelRoutingConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedPromptConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedRagConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedResponseConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedSafetyConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolConfig;
import com.k2bot.ai.chatbot.modelrouting.classifier.HeuristicRequestClassifier;
import com.k2bot.ai.chatbot.modelrouting.model.RequestClassification;
import com.k2bot.ai.chatbot.modelrouting.model.RequestComplexity;
import com.k2bot.ai.chatbot.modelrouting.model.RoutingInput;
import com.k2bot.ai.chatbot.orchestration.model.ChatRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;

import static org.assertj.core.api.Assertions.assertThat;

class HeuristicRequestClassifierTest {

    private HeuristicRequestClassifier classifier;

    @BeforeEach
    void setUp() {
        classifier = new HeuristicRequestClassifier();
    }

    @Test
    void classify_shortMessage_returnsSIMPLE() {
        RoutingInput input = buildInput("Hello", false, false);

        RequestClassification result = classifier.classify(input);

        assertThat(result.getComplexity()).isEqualTo(RequestComplexity.SIMPLE);
        assertThat(result.isLongContextExpected()).isFalse();
    }

    @Test
    void classify_mediumMessage_returnsMODERATE() {
        String message = "x".repeat(600);
        RoutingInput input = buildInput(message, false, false);

        RequestClassification result = classifier.classify(input);

        assertThat(result.getComplexity()).isEqualTo(RequestComplexity.MODERATE);
        assertThat(result.isLongContextExpected()).isFalse();
    }

    @Test
    void classify_longMessage_returnsCOMPLEX_and_longContextTrue() {
        String message = "x".repeat(2500);
        RoutingInput input = buildInput(message, false, false);

        RequestClassification result = classifier.classify(input);

        assertThat(result.getComplexity()).isEqualTo(RequestComplexity.COMPLEX);
        assertThat(result.isLongContextExpected()).isTrue();
    }

    @Test
    void classify_ragEnabledConfig_setsRagExpected() {
        RoutingInput input = buildInput("What is the refund policy?", true, false);

        RequestClassification result = classifier.classify(input);

        assertThat(result.isRagExpected()).isTrue();
    }

    @Test
    void classify_ragDisabledConfig_ragExpectedFalse() {
        RoutingInput input = buildInput("Hello", false, false);

        RequestClassification result = classifier.classify(input);

        assertThat(result.isRagExpected()).isFalse();
    }

    @Test
    void classify_toolsEnabledConfig_setsToolExpected() {
        RoutingInput input = buildInput("Where is my order?", false, true);

        RequestClassification result = classifier.classify(input);

        assertThat(result.isToolExpected()).isTrue();
    }

    @Test
    void classify_nullMessage_treatedAsEmpty() {
        ChatRequest request = new ChatRequest();
        request.setMessage(null);
        RoutingInput input = new RoutingInput(request, buildConfig(false, false));

        RequestClassification result = classifier.classify(input);

        assertThat(result.getComplexity()).isEqualTo(RequestComplexity.SIMPLE);
    }

    @Test
    void classify_classificationReasonIsPopulated() {
        RoutingInput input = buildInput("Test message", true, true);

        RequestClassification result = classifier.classify(input);

        assertThat(result.getClassificationReason()).isNotBlank();
        assertThat(result.getClassificationReason()).contains("complexity=");
    }

    private RoutingInput buildInput(String message, boolean ragEnabled, boolean toolsEnabled) {
        ChatRequest request = new ChatRequest();
        request.setMessage(message);
        return new RoutingInput(request, buildConfig(ragEnabled, toolsEnabled));
    }

    private ResolvedAssistantConfig buildConfig(boolean ragEnabled, boolean toolsEnabled) {
        return new ResolvedAssistantConfig(
                "test-bot", "tenant-1", "Test Bot", true,
                new ResolvedPromptConfig("System prompt", null, null, null, "v1"),
                new ResolvedModelRoutingConfig("gpt-4o", "openai", Collections.emptyList(), null, 4096, 0.7),
                new ResolvedRagConfig(ragEnabled, "kb-1", 5, 0.75, "cosine", true, false, null),
                new ResolvedMemoryConfig(false, null, null, null, false, false),
                new ResolvedToolConfig(toolsEnabled, Collections.emptyList(), false, null, null),
                new ResolvedSafetyConfig(false, false, true, false, Collections.emptyList()),
                new ResolvedResponseConfig(null, null, false, false, false, 2000),
                "cfg-v1", Instant.now()
        );
    }
}
