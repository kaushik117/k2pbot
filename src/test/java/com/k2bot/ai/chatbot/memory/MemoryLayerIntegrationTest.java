package com.k2bot.ai.chatbot.memory;

import com.k2bot.ai.chatbot.config.model.ResolvedMemoryConfig;
import com.k2bot.ai.chatbot.memory.advisor.NoOpChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.advisor.WindowedChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.api.ChatMemoryAdvisor;
import com.k2bot.ai.chatbot.memory.api.MemoryAdvisorFactory;
import com.k2bot.ai.chatbot.memory.model.ChatMemoryContext;
import com.k2bot.ai.chatbot.memory.model.ChatMemoryMessage;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class MemoryLayerIntegrationTest {

    @Autowired
    private MemoryAdvisorFactory memoryAdvisorFactory;

    @Test
    void applicationContext_loadsMemoryBeans() {
        assertThat(memoryAdvisorFactory).isNotNull();
    }

    @Test
    void create_disabledMemory_returnsNoOpAdvisor() {
        ChatMemoryContext context = new ChatMemoryContext(
                "session-it-1",
                new ResolvedMemoryConfig(false, null, null, null, false, false));

        List<ChatMemoryAdvisor> advisors = memoryAdvisorFactory.create(context);

        assertThat(advisors).hasSize(1);
        assertThat(advisors.get(0)).isInstanceOf(NoOpChatMemoryAdvisor.class);
        assertThat(advisors.get(0).loadHistory("session-it-1", 10)).isEmpty();
    }

    @Test
    void create_inMemoryMode_recordAndLoadHistory() {
        String sessionId = "session-it-2";
        ChatMemoryContext context = new ChatMemoryContext(
                sessionId,
                new ResolvedMemoryConfig(true, MemoryStoreType.IN_MEMORY, 5, 60, false, false));

        List<ChatMemoryAdvisor> advisors = memoryAdvisorFactory.create(context);
        assertThat(advisors.get(0)).isInstanceOf(WindowedChatMemoryAdvisor.class);

        ChatMemoryAdvisor advisor = advisors.get(0);
        advisor.recordMessage(sessionId, new ChatMemoryMessage("USER", "Hello", Instant.now()));
        advisor.recordMessage(sessionId, new ChatMemoryMessage("ASSISTANT", "Hi there", Instant.now()));

        List<ChatMemoryMessage> history = advisor.loadHistory(sessionId, 10);

        assertThat(history).hasSize(2);
        assertThat(history.get(0).getRole()).isEqualTo("USER");
        assertThat(history.get(1).getRole()).isEqualTo("ASSISTANT");
    }

    @Test
    void create_inMemoryMode_windowLimitHonoured() {
        String sessionId = "session-it-3";
        ChatMemoryContext context = new ChatMemoryContext(
                sessionId,
                new ResolvedMemoryConfig(true, MemoryStoreType.IN_MEMORY, 2, 60, false, false));

        ChatMemoryAdvisor advisor = memoryAdvisorFactory.create(context).get(0);
        advisor.recordMessage(sessionId, new ChatMemoryMessage("USER", "msg1", Instant.now()));
        advisor.recordMessage(sessionId, new ChatMemoryMessage("ASSISTANT", "msg2", Instant.now()));
        advisor.recordMessage(sessionId, new ChatMemoryMessage("USER", "msg3", Instant.now()));

        List<ChatMemoryMessage> history = advisor.loadHistory(sessionId, 10);

        assertThat(history).hasSize(2);
        assertThat(history.get(0).getContent()).isEqualTo("msg2");
        assertThat(history.get(1).getContent()).isEqualTo("msg3");
    }

    @Test
    void create_jdbcMode_loadsWithoutError() {
        ChatMemoryContext context = new ChatMemoryContext(
                "session-it-nonexistent",
                new ResolvedMemoryConfig(true, MemoryStoreType.JDBC, 10, 60, true, false));

        List<ChatMemoryAdvisor> advisors = memoryAdvisorFactory.create(context);
        assertThat(advisors.get(0).isEnabled()).isTrue();

        List<ChatMemoryMessage> history = advisors.get(0).loadHistory("session-it-nonexistent", 10);
        assertThat(history).isEmpty();
    }
}
