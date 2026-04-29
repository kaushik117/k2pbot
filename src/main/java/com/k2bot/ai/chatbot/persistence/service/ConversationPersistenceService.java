package com.k2bot.ai.chatbot.persistence.service;

import com.k2bot.ai.chatbot.persistence.entity.ChatMessageEntity;
import com.k2bot.ai.chatbot.persistence.entity.ChatSessionEntity;

import java.util.List;

public interface ConversationPersistenceService {

    ChatSessionEntity createSessionIfAbsent(String sessionId,
                                            String tenantId,
                                            String assistantCode,
                                            String userId,
                                            String locale,
                                            String channel);

    ChatMessageEntity saveMessage(ChatMessageEntity message);

    List<ChatMessageEntity> loadConversation(String sessionId);
}
