package com.k2bot.ai.chatbot.memory;

import com.k2bot.ai.chatbot.memory.model.ChatMemoryMessage;
import com.k2bot.ai.chatbot.memory.store.InMemoryChatMemory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class InMemoryChatMemoryTest {

    private InMemoryChatMemory memory;

    @BeforeEach
    void setUp() {
        memory = new InMemoryChatMemory();
    }

    @Test
    void get_emptyStore_returnsEmptyList() {
        assertThat(memory.get("session-1", 10)).isEmpty();
    }

    @Test
    void add_singleMessage_isRetrievable() {
        ChatMemoryMessage msg = new ChatMemoryMessage("USER", "Hello", Instant.now());
        memory.add("session-1", List.of(msg));

        List<ChatMemoryMessage> result = memory.get("session-1", 10);

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getContent()).isEqualTo("Hello");
    }

    @Test
    void get_windowLimitApplied_returnsLastN() {
        for (int i = 1; i <= 5; i++) {
            memory.add("session-1", List.of(new ChatMemoryMessage("USER", "msg-" + i, Instant.now())));
        }

        List<ChatMemoryMessage> result = memory.get("session-1", 3);

        assertThat(result).hasSize(3);
        assertThat(result.get(0).getContent()).isEqualTo("msg-3");
        assertThat(result.get(2).getContent()).isEqualTo("msg-5");
    }

    @Test
    void get_lastNGreaterThanStored_returnsAll() {
        memory.add("session-1", List.of(new ChatMemoryMessage("USER", "only", Instant.now())));

        assertThat(memory.get("session-1", 50)).hasSize(1);
    }

    @Test
    void get_lastNZero_returnsAll() {
        memory.add("session-1", List.of(
                new ChatMemoryMessage("USER", "a", Instant.now()),
                new ChatMemoryMessage("ASSISTANT", "b", Instant.now())));

        assertThat(memory.get("session-1", 0)).hasSize(2);
    }

    @Test
    void clear_removesAllMessagesForSession() {
        memory.add("session-1", List.of(new ChatMemoryMessage("USER", "hi", Instant.now())));
        memory.clear("session-1");

        assertThat(memory.get("session-1", 10)).isEmpty();
    }

    @Test
    void clear_doesNotAffectOtherSessions() {
        memory.add("session-1", List.of(new ChatMemoryMessage("USER", "s1", Instant.now())));
        memory.add("session-2", List.of(new ChatMemoryMessage("USER", "s2", Instant.now())));

        memory.clear("session-1");

        assertThat(memory.get("session-1", 10)).isEmpty();
        assertThat(memory.get("session-2", 10)).hasSize(1);
    }

    @Test
    void add_emptyList_isNoop() {
        memory.add("session-1", List.of());
        assertThat(memory.get("session-1", 10)).isEmpty();
    }
}
