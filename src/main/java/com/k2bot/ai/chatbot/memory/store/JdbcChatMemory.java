package com.k2bot.ai.chatbot.memory.store;

import com.k2bot.ai.chatbot.memory.api.ChatMemory;
import com.k2bot.ai.chatbot.memory.model.ChatMemoryMessage;
import com.k2bot.ai.chatbot.persistence.entity.ChatMessageEntity;
import com.k2bot.ai.chatbot.persistence.entity.MessageRole;
import com.k2bot.ai.chatbot.persistence.repository.ChatMessageRepository;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class JdbcChatMemory implements ChatMemory {

    private final ChatMessageRepository chatMessageRepository;

    public JdbcChatMemory(ChatMessageRepository chatMessageRepository) {
        this.chatMessageRepository = chatMessageRepository;
    }

    /**
     * No-op: messages are persisted by ConversationPersistenceService, not the memory layer.
     */
    @Override
    public void add(String conversationId, List<ChatMemoryMessage> messages) {
    }

    @Override
    public List<ChatMemoryMessage> get(String conversationId, int lastN) {
        List<ChatMessageEntity> entities =
                chatMessageRepository.findBySessionSessionIdOrderByCreatedAtAsc(conversationId);

        List<ChatMemoryMessage> messages = entities.stream()
                .filter(e -> e.getMessageRole() == MessageRole.USER
                        || e.getMessageRole() == MessageRole.ASSISTANT)
                .map(e -> new ChatMemoryMessage(
                        e.getMessageRole().name(),
                        e.getContent(),
                        e.getCreatedAt()))
                .collect(Collectors.toCollection(ArrayList::new));

        if (lastN <= 0 || messages.size() <= lastN) {
            return Collections.unmodifiableList(messages);
        }
        return Collections.unmodifiableList(
                new ArrayList<>(messages.subList(messages.size() - lastN, messages.size())));
    }

    /**
     * No-op: physical deletion of chat history is not the memory layer's responsibility.
     */
    @Override
    public void clear(String conversationId) {
    }
}
