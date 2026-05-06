package com.k2bot.ai.chatbot.modelrouting.service;

import com.k2bot.ai.chatbot.modelrouting.api.ModelRoutingService;
import com.k2bot.ai.chatbot.modelrouting.api.ModelSelectionValidator;
import com.k2bot.ai.chatbot.modelrouting.api.RequestClassifier;
import com.k2bot.ai.chatbot.modelrouting.api.RoutingPolicyEvaluator;
import com.k2bot.ai.chatbot.modelrouting.exception.ModelRoutingException;
import com.k2bot.ai.chatbot.modelrouting.exception.ModelSelectionValidationException;
import com.k2bot.ai.chatbot.modelrouting.exception.NoEligibleModelException;
import com.k2bot.ai.chatbot.modelrouting.model.ModelSelectionResult;
import com.k2bot.ai.chatbot.modelrouting.model.RequestClassification;
import com.k2bot.ai.chatbot.modelrouting.model.RoutingInput;
import org.springframework.stereotype.Service;

@Service
public class DefaultModelRoutingService implements ModelRoutingService {

    private final RequestClassifier requestClassifier;
    private final RoutingPolicyEvaluator routingPolicyEvaluator;
    private final ModelSelectionValidator modelSelectionValidator;

    public DefaultModelRoutingService(
            RequestClassifier requestClassifier,
            RoutingPolicyEvaluator routingPolicyEvaluator,
            ModelSelectionValidator modelSelectionValidator) {
        this.requestClassifier = requestClassifier;
        this.routingPolicyEvaluator = routingPolicyEvaluator;
        this.modelSelectionValidator = modelSelectionValidator;
    }

    @Override
    public ModelSelectionResult select(RoutingInput input) {
        try {
            RequestClassification classification = requestClassifier.classify(input);
            ModelSelectionResult result = routingPolicyEvaluator.evaluate(input, classification);
            modelSelectionValidator.validate(result, input);
            return result;
        } catch (NoEligibleModelException | ModelSelectionValidationException e) {
            throw e;
        } catch (Exception e) {
            throw new ModelRoutingException(
                    "Model routing failed for assistant: "
                            + input.getResolvedConfig().getAssistantCode(), e);
        }
    }
}
