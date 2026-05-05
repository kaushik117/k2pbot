package com.k2bot.ai.chatbot.prompt.api;

import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;

import java.util.List;

public interface PromptInstructionComposer {

    List<String> composeSystemInstructions(PromptAssemblyInput input);

    List<String> composeDeveloperInstructions(PromptAssemblyInput input);
}
