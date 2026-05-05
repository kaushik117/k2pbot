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
import com.k2bot.ai.chatbot.orchestration.model.ChatRequest;
import com.k2bot.ai.chatbot.persistence.entity.ToolType;
import com.k2bot.ai.chatbot.prompt.api.PromptAssemblyService;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyResult;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class PromptAssemblyIntegrationTest {

    @Autowired
    private PromptAssemblyService promptAssemblyService;

    @Test
    void assemble_withBasicConfig_producesExpectedPrompts() {
        ResolvedAssistantConfig config = buildConfig(
                "You are a helpful assistant for {companyName}.",
                Map.of("companyName", "TestCorp"),
                null, null, null, null
        );

        ChatRequest request = buildRequest("What can you help me with?");
        PromptAssemblyInput input = new PromptAssemblyInput(request, config, null, null);

        PromptAssemblyResult result = promptAssemblyService.assemble(input);

        assertThat(result.getSystemPrompt()).contains("TestCorp");
        assertThat(result.getUserPrompt()).isEqualTo("What can you help me with?");
        assertThat(result.getPromptVersion()).isEqualTo("v1");
        assertThat(result.getMetadata()).isNotNull();
    }

    @Test
    void assemble_withGroundedMode_injectsRagInstructions() {
        ResolvedRagConfig rag = new ResolvedRagConfig(
                true, "kb-001", 5, 0.75, "cosine", true, true, null);

        ResolvedAssistantConfig config = buildConfig(
                "You are a knowledge assistant.",
                null, rag, null, null, null
        );

        ChatRequest request = buildRequest("Explain our refund policy.");
        PromptAssemblyInput input = new PromptAssemblyInput(request, config, null, null);

        PromptAssemblyResult result = promptAssemblyService.assemble(input);

        assertThat(result.getSystemPrompt()).contains("strictly on the provided context");
        assertThat(result.getMetadata().isGroundedMode()).isTrue();
        assertThat(result.getMetadata().isRagInstructionsInjected()).isTrue();
    }

    @Test
    void assemble_withToolsEnabled_injectsToolInstructions() {
        ResolvedToolDefinition tool = new ResolvedToolDefinition(
                "order-lookup", ToolType.LOCAL_BEAN, false, 5000);
        ResolvedToolConfig toolConfig = new ResolvedToolConfig(
                true, List.of(tool), false, 3, 5000);

        ResolvedAssistantConfig config = buildConfig(
                "You are an order support assistant.", null, null, toolConfig, null, null);

        ChatRequest request = buildRequest("Where is my order?");
        PromptAssemblyInput input = new PromptAssemblyInput(request, config, null, null);

        PromptAssemblyResult result = promptAssemblyService.assemble(input);

        assertThat(result.getDeveloperPrompt()).contains("order-lookup");
        assertThat(result.getMetadata().isToolInstructionsInjected()).isTrue();
    }

    @Test
    void assemble_propagatesPromptVersionToResult() {
        ResolvedPromptConfig promptConfig = new ResolvedPromptConfig(
                "You are version 2 assistant.", null, null, null, "v2.1");

        ResolvedAssistantConfig config = new ResolvedAssistantConfig(
                "versioned-bot", "tenant-1", "Versioned Bot", true,
                promptConfig,
                new ResolvedModelRoutingConfig(null, null, Collections.emptyList(), null, null, null),
                new ResolvedRagConfig(false, null, null, null, null, false, false, null),
                new ResolvedMemoryConfig(false, null, null, null, false, false),
                new ResolvedToolConfig(false, Collections.emptyList(), false, null, null),
                new ResolvedSafetyConfig(false, false, true, false, Collections.emptyList()),
                new ResolvedResponseConfig(null, null, false, false, false, null),
                "cfg-v1", Instant.now()
        );

        ChatRequest request = buildRequest("Hello.");
        PromptAssemblyInput input = new PromptAssemblyInput(request, config, null, null);

        PromptAssemblyResult result = promptAssemblyService.assemble(input);

        assertThat(result.getPromptVersion()).isEqualTo("v2.1");
    }

    @Test
    void assemble_withResponsePolicy_injectsFormatInstructions() {
        ResolvedResponseConfig responseConfig = new ResolvedResponseConfig(
                "professional", null, false, true, false, null);

        ResolvedAssistantConfig config = buildConfig(
                "You are a professional assistant.", null, null, null, responseConfig, null);

        ChatRequest request = buildRequest("Summarize the report.");
        PromptAssemblyInput input = new PromptAssemblyInput(request, config, null, null);

        PromptAssemblyResult result = promptAssemblyService.assemble(input);

        assertThat(result.getSystemPrompt()).contains("professional");
        assertThat(result.getSystemPrompt()).contains("markdown");
    }

    @Test
    void assemble_withRuntimeVariables_overridesDefaults() {
        ResolvedPromptConfig promptConfig = new ResolvedPromptConfig(
                "You serve {region} customers.", null,
                Map.of("region", "global"), null, "v1");

        ResolvedAssistantConfig config = new ResolvedAssistantConfig(
                "region-bot", "tenant-1", "Region Bot", true,
                promptConfig,
                new ResolvedModelRoutingConfig(null, null, Collections.emptyList(), null, null, null),
                new ResolvedRagConfig(false, null, null, null, null, false, false, null),
                new ResolvedMemoryConfig(false, null, null, null, false, false),
                new ResolvedToolConfig(false, Collections.emptyList(), false, null, null),
                new ResolvedSafetyConfig(false, false, true, false, Collections.emptyList()),
                new ResolvedResponseConfig(null, null, false, false, false, null),
                "cfg-v1", Instant.now()
        );

        ChatRequest request = buildRequest("Help me.");
        PromptAssemblyInput input = new PromptAssemblyInput(
                request, config, null, Map.of("region", "APAC"));

        PromptAssemblyResult result = promptAssemblyService.assemble(input);

        assertThat(result.getSystemPrompt()).contains("APAC");
    }

    private ChatRequest buildRequest(String message) {
        ChatRequest request = new ChatRequest();
        request.setAssistantCode("test-bot");
        request.setTenantId("tenant-1");
        request.setSessionId("sess-001");
        request.setUserId("user-1");
        request.setMessage(message);
        return request;
    }

    private ResolvedAssistantConfig buildConfig(
            String systemPrompt,
            Map<String, String> defaultVars,
            ResolvedRagConfig ragConfig,
            ResolvedToolConfig toolConfig,
            ResolvedResponseConfig responseConfig,
            List<String> guardrails) {

        ResolvedPromptConfig promptConfig = new ResolvedPromptConfig(
                systemPrompt, null, defaultVars, guardrails, "v1");

        return new ResolvedAssistantConfig(
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
                "cfg-v1", Instant.now()
        );
    }
}
