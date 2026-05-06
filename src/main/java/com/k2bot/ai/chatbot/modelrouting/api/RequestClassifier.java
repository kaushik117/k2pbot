package com.k2bot.ai.chatbot.modelrouting.api;

import com.k2bot.ai.chatbot.modelrouting.model.RequestClassification;
import com.k2bot.ai.chatbot.modelrouting.model.RoutingInput;

public interface RequestClassifier {

    RequestClassification classify(RoutingInput input);
}
