package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.AssistantEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AssistantRepository extends JpaRepository<AssistantEntity, Long> {

    Optional<AssistantEntity> findByAssistantCodeAndActiveTrue(String assistantCode);
}
