package com.k2bot.ai.chatbot.memory.factory;

import com.k2bot.ai.chatbot.config.model.ResolvedMemoryConfig;
import com.k2bot.ai.chatbot.memory.advisor.JdbcChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.advisor.NoOpChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.advisor.WindowedChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.api.ChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.api.MemoryAdvisorFactory;
import com.k2bot.ai.chatbot.memory.exception.MemoryStrategyException;
import com.k2bot.ai.chatbot.memory.model.ChatMemoryContext;
import com.k2bot.ai.chatbot.memory.store.InMemoryChatMemory;
import com.k2bot.ai.chatbot.memory.store.JdbcChatMemory;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;

@Service
public class DefaultMemoryAdvisorFactory implements MemoryAdvisorFactory {

    private static final int DEFAULT_WINDOW_SIZE = 10;

    private final InMemoryChatMemory inMemoryChatMemory;
    private final JdbcChatMemory jdbcChatMemory;

    public DefaultMemoryAdvisorFactory(
            InMemoryChatMemory inMemoryChatMemory,
            JdbcChatMemory jdbcChatMemory) {
        this.inMemoryChatMemory = inMemoryChatMemory;
        this.jdbcChatMemory = jdbcChatMemory;
    }

    @Override
    public List<ChatMemoryAdvisor> create(ChatMemoryContext context) {
        ResolvedMemoryConfig config = context.getMemoryConfig();

        if (!config.isEnabled() || config.getStoreType() == null
                || config.getStoreType() == MemoryStoreType.NONE) {
            return Collections.singletonList(new NoOpChatMemoryAdvisor());
        }

        int windowSize = config.getMessageWindowSize() != null && config.getMessageWindowSize() > 0
                ? config.getMessageWindowSize()
                : DEFAULT_WINDOW_SIZE;

        return switch (config.getStoreType()) {
            case IN_MEMORY -> Collections.singletonList(
                    new WindowedChatMemoryAdvisor(inMemoryChatMemory, windowSize));
            case JDBC -> Collections.singletonList(
                    new JdbcChatMemoryAdvisor(jdbcChatMemory, windowSize));
            default -> throw new MemoryStrategyException(
                    "Unsupported memory store type: " + config.getStoreType());
        };
    }
}
