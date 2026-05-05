package com.k2bot.ai.chatbot.prompt.api;

import java.util.Map;

public interface PromptTemplateRenderer {

    String render(String template, Map<String, Object> variables);
}
