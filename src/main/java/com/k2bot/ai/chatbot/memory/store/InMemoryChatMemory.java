package com.k2bot.ai.chatbot.memory.store;

import com.k2bot.ai.chatbot.memory.api.ChatMemory;
import com.k2bot.ai.chatbot.memory.model.ChatMemoryMessage;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class InMemoryChatMemory implements ChatMemory {

    private static final int MAX_MESSAGES_PER_CONVERSATION = 1000;

    private final ConcurrentHashMap<String, List<ChatMemoryMessage>> store = new ConcurrentHashMap<>();

    @Override
    public void add(String conversationId, List<ChatMemoryMessage> messages) {
        if (messages == null || messages.isEmpty()) {
            return;
        }
        store.compute(conversationId, (k, existing) -> {
            List<ChatMemoryMessage> merged = new ArrayList<>(existing != null ? existing : Collections.emptyList());
            merged.addAll(messages);
            int size = merged.size();
            if (size > MAX_MESSAGES_PER_CONVERSATION) {
                return new ArrayList<>(merged.subList(size - MAX_MESSAGES_PER_CONVERSATION, size));
            }
            return merged;
        });
    }

    @Override
    public List<ChatMemoryMessage> get(String conversationId, int lastN) {
        List<ChatMemoryMessage> messages = store.getOrDefault(conversationId, Collections.emptyList());
        if (lastN <= 0 || messages.size() <= lastN) {
            return Collections.unmodifiableList(messages);
        }
        return Collections.unmodifiableList(
                new ArrayList<>(messages.subList(messages.size() - lastN, messages.size())));
    }

    @Override
    public void clear(String conversationId) {
        store.remove(conversationId);
    }
}
