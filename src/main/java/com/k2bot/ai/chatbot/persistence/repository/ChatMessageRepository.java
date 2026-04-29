package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.ChatMessageEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatMessageRepository extends JpaRepository<ChatMessageEntity, String> {

    List<ChatMessageEntity> findBySessionSessionIdOrderByCreatedAtAsc(String sessionId);
}
