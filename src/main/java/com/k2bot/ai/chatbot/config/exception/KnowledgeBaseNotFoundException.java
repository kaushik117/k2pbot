package com.k2bot.ai.chatbot.config.exception;

public class KnowledgeBaseNotFoundException extends RuntimeException {

    private final String knowledgeBaseId;

    public KnowledgeBaseNotFoundException(String knowledgeBaseId) {
        super("No active knowledge base found for knowledgeBaseId='" + knowledgeBaseId + "'");
        this.knowledgeBaseId = knowledgeBaseId;
    }

    public String getKnowledgeBaseId() {
        return knowledgeBaseId;
    }
}
