package com.k2bot.ai.chatbot.prompt.api;

import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyResult;

public interface PromptAssemblyService {

    PromptAssemblyResult assemble(PromptAssemblyInput input);
}
