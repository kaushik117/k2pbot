package com.k2bot.ai.chatbot.memory;

import com.k2bot.ai.chatbot.config.model.ResolvedMemoryConfig;
import com.k2bot.ai.chatbot.memory.advisor.JdbcChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.advisor.NoOpChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.advisor.WindowedChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.api.ChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.factory.DefaultMemoryAdvisorFactory;
import com.k2bot.ai.chatbot.memory.model.ChatMemoryContext;
import com.k2bot.ai.chatbot.memory.store.InMemoryChatMemory;
import com.k2bot.ai.chatbot.memory.store.JdbcChatMemory;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

class DefaultMemoryAdvisorFactoryTest {

    private DefaultMemoryAdvisorFactory factory;

    @BeforeEach
    void setUp() {
        factory = new DefaultMemoryAdvisorFactory(
                new InMemoryChatMemory(),
                mock(JdbcChatMemory.class));
    }

    @Test
    void create_memoryDisabled_returnsNoOpAdvisor() {
        ChatMemoryContext context = new ChatMemoryContext(
                "session-1",
                new ResolvedMemoryConfig(false, MemoryStoreType.IN_MEMORY, 10, 60, false, false));

        List<ChatMemoryAdvisor> advisors = factory.create(context);

        assertThat(advisors).hasSize(1);
        assertThat(advisors.get(0)).isInstanceOf(NoOpChatMemoryAdvisor.class);
        assertThat(advisors.get(0).isEnabled()).isFalse();
    }

    @Test
    void create_storeTypeNone_returnsNoOpAdvisor() {
        ChatMemoryContext context = new ChatMemoryContext(
                "session-1",
                new ResolvedMemoryConfig(true, MemoryStoreType.NONE, 10, 60, false, false));

        List<ChatMemoryAdvisor> advisors = factory.create(context);

        assertThat(advisors).hasSize(1);
        assertThat(advisors.get(0)).isInstanceOf(NoOpChatMemoryAdvisor.class);
    }

    @Test
    void create_inMemoryMode_returnsWindowedAdvisor() {
        ChatMemoryContext context = new ChatMemoryContext(
                "session-1",
                new ResolvedMemoryConfig(true, MemoryStoreType.IN_MEMORY, 20, 60, false, false));

        List<ChatMemoryAdvisor> advisors = factory.create(context);

        assertThat(advisors).hasSize(1);
        assertThat(advisors.get(0)).isInstanceOf(WindowedChatMemoryAdvisor.class);
        assertThat(advisors.get(0).isEnabled()).isTrue();
        assertThat(advisors.get(0).getStoreType()).isEqualTo(MemoryStoreType.IN_MEMORY);
        assertThat(((WindowedChatMemoryAdvisor) advisors.get(0)).getWindowSize()).isEqualTo(20);
    }

    @Test
    void create_jdbcMode_returnsJdbcAdvisor() {
        ChatMemoryContext context = new ChatMemoryContext(
                "session-1",
                new ResolvedMemoryConfig(true, MemoryStoreType.JDBC, 15, 60, true, false));

        List<ChatMemoryAdvisor> advisors = factory.create(context);

        assertThat(advisors).hasSize(1);
        assertThat(advisors.get(0)).isInstanceOf(JdbcChatMemoryAdvisor.class);
        assertThat(advisors.get(0).isEnabled()).isTrue();
        assertThat(advisors.get(0).getStoreType()).isEqualTo(MemoryStoreType.JDBC);
        assertThat(((JdbcChatMemoryAdvisor) advisors.get(0)).getWindowSize()).isEqualTo(15);
    }

    @Test
    void create_nullWindowSize_usesDefaultWindowSize() {
        ChatMemoryContext context = new ChatMemoryContext(
                "session-1",
                new ResolvedMemoryConfig(true, MemoryStoreType.IN_MEMORY, null, 60, false, false));

        List<ChatMemoryAdvisor> advisors = factory.create(context);

        assertThat(advisors.get(0)).isInstanceOf(WindowedChatMemoryAdvisor.class);
        assertThat(((WindowedChatMemoryAdvisor) advisors.get(0)).getWindowSize()).isEqualTo(10);
    }

    @Test
    void create_noOpAdvisor_loadHistoryReturnsEmpty() {
        ChatMemoryContext context = new ChatMemoryContext(
                "session-1",
                new ResolvedMemoryConfig(false, null, null, null, false, false));

        ChatMemoryAdvisor advisor = factory.create(context).get(0);

        assertThat(advisor.loadHistory("session-1", 10)).isEmpty();
    }
}
