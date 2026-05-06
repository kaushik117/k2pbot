package com.k2bot.ai.chatbot.modelrouting.model;

public class RequestClassification {

    private final RequestComplexity complexity;
    private final boolean ragExpected;
    private final boolean toolExpected;
    private final boolean structuredOutputExpected;
    private final boolean longContextExpected;
    private final String classificationReason;

    public RequestClassification(
            RequestComplexity complexity,
            boolean ragExpected,
            boolean toolExpected,
            boolean structuredOutputExpected,
            boolean longContextExpected,
            String classificationReason) {
        this.complexity = complexity;
        this.ragExpected = ragExpected;
        this.toolExpected = toolExpected;
        this.structuredOutputExpected = structuredOutputExpected;
        this.longContextExpected = longContextExpected;
        this.classificationReason = classificationReason;
    }

    public RequestComplexity getComplexity() {
        return complexity;
    }

    public boolean isRagExpected() {
        return ragExpected;
    }

    public boolean isToolExpected() {
        return toolExpected;
    }

    public boolean isStructuredOutputExpected() {
        return structuredOutputExpected;
    }

    public boolean isLongContextExpected() {
        return longContextExpected;
    }

    public String getClassificationReason() {
        return classificationReason;
    }
}
