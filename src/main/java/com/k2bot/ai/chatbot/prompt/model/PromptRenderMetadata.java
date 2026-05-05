package com.k2bot.ai.chatbot.prompt.model;

public class PromptRenderMetadata {

    private final boolean groundedMode;
    private final boolean toolInstructionsInjected;
    private final boolean responseFormatInstructionsInjected;
    private final boolean memoryInstructionsInjected;
    private final boolean ragInstructionsInjected;

    public PromptRenderMetadata(
            boolean groundedMode,
            boolean toolInstructionsInjected,
            boolean responseFormatInstructionsInjected,
            boolean memoryInstructionsInjected,
            boolean ragInstructionsInjected) {
        this.groundedMode = groundedMode;
        this.toolInstructionsInjected = toolInstructionsInjected;
        this.responseFormatInstructionsInjected = responseFormatInstructionsInjected;
        this.memoryInstructionsInjected = memoryInstructionsInjected;
        this.ragInstructionsInjected = ragInstructionsInjected;
    }

    public boolean isGroundedMode() {
        return groundedMode;
    }

    public boolean isToolInstructionsInjected() {
        return toolInstructionsInjected;
    }

    public boolean isResponseFormatInstructionsInjected() {
        return responseFormatInstructionsInjected;
    }

    public boolean isMemoryInstructionsInjected() {
        return memoryInstructionsInjected;
    }

    public boolean isRagInstructionsInjected() {
        return ragInstructionsInjected;
    }
}
