package com.k2bot.ai.chatbot.prompt.template;

import com.k2bot.ai.chatbot.prompt.api.PromptTemplateRenderer;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Renders prompt templates using {variable} placeholder substitution,
 * matching Spring AI PromptTemplate syntax.
 */
@Component
public class SpringPromptTemplateRenderer implements PromptTemplateRenderer {

    private static final Pattern PLACEHOLDER_PATTERN = Pattern.compile("\\{(\\w+)}");

    @Override
    public String render(String template, Map<String, Object> variables) {
        if (template == null || template.isBlank()) {
            return template != null ? template : "";
        }
        if (variables == null || variables.isEmpty()) {
            return template;
        }
        StringBuffer result = new StringBuffer();
        Matcher matcher = PLACEHOLDER_PATTERN.matcher(template);
        while (matcher.find()) {
            String key = matcher.group(1);
            Object value = variables.get(key);
            String replacement = value != null
                    ? Matcher.quoteReplacement(value.toString())
                    : matcher.group(0);
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }
}
