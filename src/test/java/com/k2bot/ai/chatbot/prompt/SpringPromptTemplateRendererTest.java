package com.k2bot.ai.chatbot.prompt;

import com.k2bot.ai.chatbot.prompt.template.SpringPromptTemplateRenderer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class SpringPromptTemplateRendererTest {

    private SpringPromptTemplateRenderer renderer;

    @BeforeEach
    void setUp() {
        renderer = new SpringPromptTemplateRenderer();
    }

    @Test
    void render_substitutesPlaceholders() {
        String result = renderer.render(
                "Hello {name}, welcome to {company}.",
                Map.of("name", "Alice", "company", "Acme"));

        assertThat(result).isEqualTo("Hello Alice, welcome to Acme.");
    }

    @Test
    void render_withNullTemplate_returnsEmpty() {
        assertThat(renderer.render(null, Map.of("x", "y"))).isEmpty();
    }

    @Test
    void render_withBlankTemplate_returnsBlank() {
        assertThat(renderer.render("   ", Map.of("x", "y"))).isEqualTo("   ");
    }

    @Test
    void render_withNullVariables_returnsTemplateUnchanged() {
        String template = "Hello {name}.";
        assertThat(renderer.render(template, null)).isEqualTo(template);
    }

    @Test
    void render_withEmptyVariables_returnsTemplateUnchanged() {
        String template = "Hello {name}.";
        assertThat(renderer.render(template, Map.of())).isEqualTo(template);
    }

    @Test
    void render_withUnknownPlaceholder_leavesItUnchanged() {
        String result = renderer.render(
                "Hello {name} from {unknown}.",
                Map.of("name", "Bob"));

        assertThat(result).isEqualTo("Hello Bob from {unknown}.");
    }

    @Test
    void render_withNoPlaceholders_returnsTemplateUnchanged() {
        String template = "You are a helpful assistant.";
        assertThat(renderer.render(template, Map.of("x", "y"))).isEqualTo(template);
    }

    @Test
    void render_withNullVariableValue_leavesPlaceholderUnchanged() {
        Map<String, Object> vars = new java.util.HashMap<>();
        vars.put("name", null);
        String result = renderer.render("Hello {name}.", vars);
        assertThat(result).isEqualTo("Hello {name}.");
    }

    @Test
    void render_withNumericValue_substitutesCorrectly() {
        String result = renderer.render("Max tokens: {maxTokens}.", Map.of("maxTokens", 1024));
        assertThat(result).isEqualTo("Max tokens: 1024.");
    }
}
