package com.k2bot.ai.chatbot.prompt.api;

import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;

import java.util.Map;

public interface PromptVariableResolver {

    Map<String, Object> resolve(PromptAssemblyInput input);
}
