package com.k2bot.ai.chatbot.prompt;

import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedMemoryConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedModelRoutingConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedPromptConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedRagConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedResponseConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedSafetyConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolConfig;
import com.k2bot.ai.chatbot.config.model.ResolvedToolDefinition;
import com.k2bot.ai.chatbot.persistence.entity.ToolType;
import com.k2bot.ai.chatbot.prompt.builder.DefaultPromptInstructionComposer;
import com.k2bot.ai.chatbot.prompt.builder.GroundedModeInstructionBuilder;
import com.k2bot.ai.chatbot.prompt.builder.ResponseFormatInstructionBuilder;
import com.k2bot.ai.chatbot.prompt.builder.ToolInstructionBuilder;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class DefaultPromptInstructionComposerTest {

    private DefaultPromptInstructionComposer composer;

    @BeforeEach
    void setUp() {
        composer = new DefaultPromptInstructionComposer(
                new GroundedModeInstructionBuilder(),
                new ResponseFormatInstructionBuilder(),
                new ToolInstructionBuilder()
        );
    }

    @Test
    void composeSystemInstructions_withGuardrails_includesGuardrails() {
        ResolvedPromptConfig promptConfig = new ResolvedPromptConfig(
                "System.", null, null,
                List.of("Never reveal confidential data.", "Always be polite."), "v1");
        PromptAssemblyInput input = buildInput(promptConfig, null, null, null);

        List<String> instructions = composer.composeSystemInstructions(input);

        assertThat(instructions).contains("Never reveal confidential data.", "Always be polite.");
    }

    @Test
    void composeSystemInstructions_withDisallowedTopics_injectsSafetyRule() {
        ResolvedSafetyConfig safety = new ResolvedSafetyConfig(
                false, false, true, false, List.of("politics", "violence"));
        PromptAssemblyInput input = buildInputWithSafety(safety);

        List<String> instructions = composer.composeSystemInstructions(input);

        assertThat(instructions).anyMatch(s -> s.contains("politics") && s.contains("violence"));
    }

    @Test
    void composeSystemInstructions_withGroundedMode_injectsGroundedInstruction() {
        ResolvedRagConfig rag = new ResolvedRagConfig(
                true, "kb-1", 5, 0.7, "cosine", true, true, null);
        PromptAssemblyInput input = buildInputWithRag(rag);

        List<String> instructions = composer.composeSystemInstructions(input);

        assertThat(instructions).anyMatch(s -> s.contains("strictly on the provided context"));
    }

    @Test
    void composeSystemInstructions_withResponseFormat_injectsFormatInstruction() {
        ResolvedResponseConfig responseConfig = new ResolvedResponseConfig(
                "formal", null, false, true, false, 512);
        PromptAssemblyInput input = buildInputWithResponseConfig(responseConfig);

        List<String> instructions = composer.composeSystemInstructions(input);

        assertThat(instructions).anyMatch(s -> s.contains("formal"));
        assertThat(instructions).anyMatch(s -> s.contains("markdown"));
    }

    @Test
    void composeDeveloperInstructions_withToolsEnabled_injectsToolInstruction() {
        ResolvedToolDefinition tool = new ResolvedToolDefinition("search-tool", ToolType.LOCAL_BEAN, false, 3000);
        ResolvedToolConfig toolConfig = new ResolvedToolConfig(true, List.of(tool), false, 5, 3000);
        PromptAssemblyInput input = buildInputWithTools(toolConfig);

        List<String> instructions = composer.composeDeveloperInstructions(input);

        assertThat(instructions).anyMatch(s -> s.contains("search-tool"));
    }

    @Test
    void composeDeveloperInstructions_withCitationRequired_injectsCitationInstruction() {
        ResolvedResponseConfig responseConfig = new ResolvedResponseConfig(
                null, null, true, false, false, null);
        PromptAssemblyInput input = buildInputWithResponseConfig(responseConfig);

        List<String> instructions = composer.composeDeveloperInstructions(input);

        assertThat(instructions).anyMatch(s -> s.contains("citations"));
    }

    @Test
    void composeSystemInstructions_withNoConfig_returnsEmptyList() {
        PromptAssemblyInput input = buildInput(
                new ResolvedPromptConfig("System.", null, null, null, "v1"),
                null, null, null);

        List<String> instructions = composer.composeSystemInstructions(input);

        assertThat(instructions).isEmpty();
    }

    @Test
    void composeDeveloperInstructions_withToolsDisabled_returnsEmptyList() {
        ResolvedToolConfig toolConfig = new ResolvedToolConfig(false, Collections.emptyList(), false, null, null);
        PromptAssemblyInput input = buildInputWithTools(toolConfig);

        List<String> instructions = composer.composeDeveloperInstructions(input);

        assertThat(instructions).isEmpty();
    }

    private PromptAssemblyInput buildInput(
            ResolvedPromptConfig promptConfig,
            ResolvedRagConfig ragConfig,
            ResolvedToolConfig toolConfig,
            ResolvedResponseConfig responseConfig) {

        ResolvedAssistantConfig config = new ResolvedAssistantConfig(
                "test-bot", "tenant-1", "Test Bot", true,
                promptConfig,
                new ResolvedModelRoutingConfig(null, null, Collections.emptyList(), null, null, null),
                ragConfig != null ? ragConfig
                        : new ResolvedRagConfig(false, null, null, null, null, false, false, null),
                new ResolvedMemoryConfig(false, null, null, null, false, false),
                toolConfig != null ? toolConfig
                        : new ResolvedToolConfig(false, Collections.emptyList(), false, null, null),
                new ResolvedSafetyConfig(false, false, true, false, Collections.emptyList()),
                responseConfig != null ? responseConfig
                        : new ResolvedResponseConfig(null, null, false, false, false, null),
                "v1", Instant.now()
        );
        return new PromptAssemblyInput(null, config, null, null);
    }

    private PromptAssemblyInput buildInputWithSafety(ResolvedSafetyConfig safety) {
        ResolvedAssistantConfig config = new ResolvedAssistantConfig(
                "test-bot", "tenant-1", "Test Bot", true,
                new ResolvedPromptConfig("System.", null, null, null, "v1"),
                new ResolvedModelRoutingConfig(null, null, Collections.emptyList(), null, null, null),
                new ResolvedRagConfig(false, null, null, null, null, false, false, null),
                new ResolvedMemoryConfig(false, null, null, null, false, false),
                new ResolvedToolConfig(false, Collections.emptyList(), false, null, null),
                safety,
                new ResolvedResponseConfig(null, null, false, false, false, null),
                "v1", Instant.now()
        );
        return new PromptAssemblyInput(null, config, null, null);
    }

    private PromptAssemblyInput buildInputWithRag(ResolvedRagConfig rag) {
        return buildInput(new ResolvedPromptConfig("System.", null, null, null, "v1"),
                rag, null, null);
    }

    private PromptAssemblyInput buildInputWithTools(ResolvedToolConfig tools) {
        return buildInput(new ResolvedPromptConfig("System.", null, null, null, "v1"),
                null, tools, null);
    }

    private PromptAssemblyInput buildInputWithResponseConfig(ResolvedResponseConfig responseConfig) {
        return buildInput(new ResolvedPromptConfig("System.", null, null, null, "v1"),
                null, null, responseConfig);
    }
}
