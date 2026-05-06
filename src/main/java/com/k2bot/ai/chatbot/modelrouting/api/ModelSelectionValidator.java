package com.k2bot.ai.chatbot.modelrouting.api;

import com.k2bot.ai.chatbot.modelrouting.model.ModelSelectionResult;
import com.k2bot.ai.chatbot.modelrouting.model.RoutingInput;

public interface ModelSelectionValidator {

    void validate(ModelSelectionResult result, RoutingInput input);
}
