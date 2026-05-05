package com.k2bot.ai.chatbot.prompt.api;

import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyResult;

public interface PromptValidator {

    void validate(PromptAssemblyResult result);
}
