package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.ChatExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ChatExecutionRepository extends JpaRepository<ChatExecutionEntity, String> {

    List<ChatExecutionEntity> findBySessionIdOrderByStartedAtDesc(String sessionId);
}
