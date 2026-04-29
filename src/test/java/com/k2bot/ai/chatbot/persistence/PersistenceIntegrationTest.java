package com.k2bot.ai.chatbot.persistence;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.Instant;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import com.k2bot.ai.chatbot.persistence.entity.AssistantEntity;
import com.k2bot.ai.chatbot.persistence.entity.ChatExecutionEntity;
import com.k2bot.ai.chatbot.persistence.entity.ChatMessageEntity;
import com.k2bot.ai.chatbot.persistence.entity.ChatSessionEntity;
import com.k2bot.ai.chatbot.persistence.entity.MemoryStoreType;
import com.k2bot.ai.chatbot.persistence.entity.MessageRole;
import com.k2bot.ai.chatbot.persistence.entity.SessionStatus;
import com.k2bot.ai.chatbot.persistence.entity.ToolExecutionAuditEntity;
import com.k2bot.ai.chatbot.persistence.entity.ToolType;
import com.k2bot.ai.chatbot.persistence.repository.AssistantRepository;
import com.k2bot.ai.chatbot.persistence.repository.ChatExecutionRepository;
import com.k2bot.ai.chatbot.persistence.repository.ChatMessageRepository;
import com.k2bot.ai.chatbot.persistence.repository.ChatSessionRepository;
import com.k2bot.ai.chatbot.persistence.repository.ToolExecutionAuditRepository;
import com.k2bot.ai.chatbot.persistence.service.ConversationPersistenceService;
import com.k2bot.ai.chatbot.persistence.service.ExecutionAuditPersistenceService;

@SpringBootTest
@ActiveProfiles("test")
class PersistenceIntegrationTest {

    @Autowired
    private AssistantRepository assistantRepository;

    @Autowired
    private ChatSessionRepository chatSessionRepository;

    @Autowired
    private ChatMessageRepository chatMessageRepository;

    @Autowired
    private ChatExecutionRepository chatExecutionRepository;

    @Autowired
    private ToolExecutionAuditRepository toolExecutionAuditRepository;

    @Autowired
    private ConversationPersistenceService conversationPersistenceService;

    @Autowired
    private ExecutionAuditPersistenceService executionAuditPersistenceService;

    @Test
    void insertAndFetchAssistant() {
        AssistantEntity assistant = new AssistantEntity();
        assistant.setAssistantCode("it-assistant-" + System.nanoTime());
        assistant.setName("Integration Test Assistant");
        assistant.setActive(true);

        AssistantEntity saved = assistantRepository.save(assistant);

        assertThat(saved.getId()).isNotNull();
        assertThat(saved.getCreatedAt()).isNotNull();
        assertThat(saved.getUpdatedAt()).isNotNull();
        assertThat(saved.getRowVersion()).isNotNull();

        AssistantEntity loaded = assistantRepository
                .findByAssistantCodeAndActiveTrue(saved.getAssistantCode())
                .orElseThrow();
        assertThat(loaded.getId()).isEqualTo(saved.getId());
    }

    @Test
    void insertAndFetchChatSessionAndMessagesOrdered() {
        String sessionId = "sess-" + System.nanoTime();

        ChatSessionEntity session = conversationPersistenceService.createSessionIfAbsent(
                sessionId, "tenant-a", "assistant-a", "user-a", "en-US", "web");

        assertThat(session.getStatus()).isEqualTo(SessionStatus.ACTIVE);

        ChatMessageEntity first = new ChatMessageEntity();
        first.setMessageId("msg-1-" + System.nanoTime());
        first.setSession(session);
        first.setRequestId("req-1");
        first.setMessageRole(MessageRole.USER);
        first.setContent("hello");
        conversationPersistenceService.saveMessage(first);

        ChatMessageEntity second = new ChatMessageEntity();
        second.setMessageId("msg-2-" + System.nanoTime());
        second.setSession(session);
        second.setRequestId("req-1");
        second.setMessageRole(MessageRole.ASSISTANT);
        second.setContent("hi there");
        conversationPersistenceService.saveMessage(second);

        List<ChatMessageEntity> conversation = conversationPersistenceService.loadConversation(sessionId);

        assertThat(conversation).hasSize(2);
        assertThat(conversation.get(0).getMessageRole()).isEqualTo(MessageRole.USER);
        assertThat(conversation.get(1).getMessageRole()).isEqualTo(MessageRole.ASSISTANT);
        assertThat(conversation.get(0).getCreatedAt())
                .isBeforeOrEqualTo(conversation.get(1).getCreatedAt());

        assertThat(chatSessionRepository.findById(sessionId)).isPresent();
        assertThat(chatMessageRepository.findBySessionSessionIdOrderByCreatedAtAsc(sessionId)).hasSize(2);
    }

    @Test
    void insertAndFetchExecutionAuditWithToolAudits() {
        String requestId = "req-" + System.nanoTime();

        ChatExecutionEntity execution = new ChatExecutionEntity();
        execution.setRequestId(requestId);
        execution.setSessionId("sess-x");
        execution.setTenantId("tenant-a");
        execution.setAssistantCode("assistant-a");
        execution.setUserId("user-a");
        execution.setSelectedProvider("openai");
        execution.setSelectedModel("gpt-4o-mini");
        execution.setMemoryStoreType(MemoryStoreType.IN_MEMORY);
        execution.setSuccess(true);
        execution.setInputTokens(120);
        execution.setOutputTokens(80);
        execution.setLatencyMs(1500L);
        execution.setStartedAt(Instant.now());
        execution.setCompletedAt(Instant.now());

        executionAuditPersistenceService.saveExecutionStart(execution);

        ToolExecutionAuditEntity toolAudit = new ToolExecutionAuditEntity();
        toolAudit.setRequestId(requestId);
        toolAudit.setSessionId("sess-x");
        toolAudit.setToolName("calculator");
        toolAudit.setToolType(ToolType.LOCAL_BEAN);
        toolAudit.setSuccess(true);
        toolAudit.setLatencyMs(50L);

        executionAuditPersistenceService.saveToolAudits(List.of(toolAudit));

        ChatExecutionEntity loaded = chatExecutionRepository.findById(requestId).orElseThrow();
        assertThat(loaded.getSelectedModel()).isEqualTo("gpt-4o-mini");
        assertThat(loaded.getMemoryStoreType()).isEqualTo(MemoryStoreType.IN_MEMORY);

        List<ToolExecutionAuditEntity> toolAudits = toolExecutionAuditRepository.findByRequestId(requestId);
        assertThat(toolAudits).hasSize(1);
        assertThat(toolAudits.get(0).getToolType()).isEqualTo(ToolType.LOCAL_BEAN);
    }
}
