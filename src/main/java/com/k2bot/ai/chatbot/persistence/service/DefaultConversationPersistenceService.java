package com.k2bot.ai.chatbot.persistence.service;

import com.k2bot.ai.chatbot.persistence.entity.ChatMessageEntity;
import com.k2bot.ai.chatbot.persistence.entity.ChatSessionEntity;
import com.k2bot.ai.chatbot.persistence.entity.SessionStatus;
import com.k2bot.ai.chatbot.persistence.repository.ChatMessageRepository;
import com.k2bot.ai.chatbot.persistence.repository.ChatSessionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

@Service
public class DefaultConversationPersistenceService implements ConversationPersistenceService {

    private final ChatSessionRepository sessionRepository;
    private final ChatMessageRepository messageRepository;

    public DefaultConversationPersistenceService(ChatSessionRepository sessionRepository,
                                                 ChatMessageRepository messageRepository) {
        this.sessionRepository = sessionRepository;
        this.messageRepository = messageRepository;
    }

    @Override
    @Transactional
    public ChatSessionEntity createSessionIfAbsent(String sessionId,
                                                   String tenantId,
                                                   String assistantCode,
                                                   String userId,
                                                   String locale,
                                                   String channel) {
        return sessionRepository.findById(sessionId).orElseGet(() -> {
            ChatSessionEntity session = new ChatSessionEntity();
            session.setSessionId(sessionId);
            session.setTenantId(tenantId);
            session.setAssistantCode(assistantCode);
            session.setUserId(userId);
            session.setLocale(locale);
            session.setChannel(channel);
            session.setStatus(SessionStatus.ACTIVE);
            session.setLastMessageAt(Instant.now());
            return sessionRepository.save(session);
        });
    }

    @Override
    @Transactional
    public ChatMessageEntity saveMessage(ChatMessageEntity message) {
        return messageRepository.save(message);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ChatMessageEntity> loadConversation(String sessionId) {
        return messageRepository.findBySessionSessionIdOrderByCreatedAtAsc(sessionId);
    }
}
