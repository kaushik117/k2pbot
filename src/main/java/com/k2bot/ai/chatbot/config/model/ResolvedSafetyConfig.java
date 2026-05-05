package com.k2bot.ai.chatbot.config.model;

import java.util.List;

public final class ResolvedSafetyConfig {

    private final boolean blockUnknownTools;
    private final boolean blockWithoutRagWhenGroundedMode;
    private final boolean allowDirectModelAnswerWithoutContext;
    private final boolean maskSensitiveDataInLogs;
    private final List<String> disallowedTopics;

    public ResolvedSafetyConfig(
            boolean blockUnknownTools,
            boolean blockWithoutRagWhenGroundedMode,
            boolean allowDirectModelAnswerWithoutContext,
            boolean maskSensitiveDataInLogs,
            List<String> disallowedTopics) {
        this.blockUnknownTools = blockUnknownTools;
        this.blockWithoutRagWhenGroundedMode = blockWithoutRagWhenGroundedMode;
        this.allowDirectModelAnswerWithoutContext = allowDirectModelAnswerWithoutContext;
        this.maskSensitiveDataInLogs = maskSensitiveDataInLogs;
        this.disallowedTopics = disallowedTopics;
    }

    public boolean isBlockUnknownTools() {
        return blockUnknownTools;
    }

    public boolean isBlockWithoutRagWhenGroundedMode() {
        return blockWithoutRagWhenGroundedMode;
    }

    public boolean isAllowDirectModelAnswerWithoutContext() {
        return allowDirectModelAnswerWithoutContext;
    }

    public boolean isMaskSensitiveDataInLogs() {
        return maskSensitiveDataInLogs;
    }

    public List<String> getDisallowedTopics() {
        return disallowedTopics;
    }
}
