package com.k2bot.ai.chatbot.prompt;

import com.k2bot.ai.chatbot.prompt.exception.PromptValidationException;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyResult;
import com.k2bot.ai.chatbot.prompt.model.PromptRenderMetadata;
import com.k2bot.ai.chatbot.prompt.validator.DefaultPromptValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class DefaultPromptValidatorTest {

    private DefaultPromptValidator validator;

    @BeforeEach
    void setUp() {
        validator = new DefaultPromptValidator();
    }

    @Test
    void validate_withValidResult_doesNotThrow() {
        PromptAssemblyResult result = buildResult("You are helpful.", "Hello?",
                new PromptRenderMetadata(false, false, false, false, false));

        assertThatNoException().isThrownBy(() -> validator.validate(result));
    }

    @Test
    void validate_withBlankSystemPrompt_throwsValidationException() {
        PromptAssemblyResult result = buildResult("  ", "Hello?",
                new PromptRenderMetadata(false, false, false, false, false));

        assertThatThrownBy(() -> validator.validate(result))
                .isInstanceOf(PromptValidationException.class)
                .hasMessageContaining("System prompt");
    }

    @Test
    void validate_withNullSystemPrompt_throwsValidationException() {
        PromptAssemblyResult result = buildResult(null, "Hello?",
                new PromptRenderMetadata(false, false, false, false, false));

        assertThatThrownBy(() -> validator.validate(result))
                .isInstanceOf(PromptValidationException.class)
                .hasMessageContaining("System prompt");
    }

    @Test
    void validate_withBlankUserPrompt_throwsValidationException() {
        PromptAssemblyResult result = buildResult("You are helpful.", "",
                new PromptRenderMetadata(false, false, false, false, false));

        assertThatThrownBy(() -> validator.validate(result))
                .isInstanceOf(PromptValidationException.class)
                .hasMessageContaining("User prompt");
    }

    @Test
    void validate_withGroundedModeButNoRagInstructions_throwsValidationException() {
        PromptRenderMetadata metadata = new PromptRenderMetadata(true, false, false, false, false);
        PromptAssemblyResult result = buildResult("You are helpful.", "What is X?", metadata);

        assertThatThrownBy(() -> validator.validate(result))
                .isInstanceOf(PromptValidationException.class)
                .hasMessageContaining("RAG instructions");
    }

    @Test
    void validate_withGroundedModeAndRagInstructions_doesNotThrow() {
        PromptRenderMetadata metadata = new PromptRenderMetadata(true, false, false, false, true);
        PromptAssemblyResult result = buildResult("You are helpful.", "What is X?", metadata);

        assertThatNoException().isThrownBy(() -> validator.validate(result));
    }

    @Test
    void validate_withNullResult_throwsValidationException() {
        assertThatThrownBy(() -> validator.validate(null))
                .isInstanceOf(PromptValidationException.class);
    }

    private PromptAssemblyResult buildResult(String systemPrompt, String userPrompt,
                                              PromptRenderMetadata metadata) {
        return new PromptAssemblyResult(systemPrompt, null, userPrompt,
                Map.of(), "v1", metadata);
    }
}
