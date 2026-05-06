package com.k2bot.ai.chatbot.memory.model;

import java.time.Instant;

public final class ChatMemoryMessage {

    private final String role;
    private final String content;
    private final Instant timestamp;

    public ChatMemoryMessage(String role, String content, Instant timestamp) {
        this.role = role;
        this.content = content;
        this.timestamp = timestamp;
    }

    public String getRole() {
        return role;
    }

    public String getContent() {
        return content;
    }

    public Instant getTimestamp() {
        return timestamp;
    }
}
