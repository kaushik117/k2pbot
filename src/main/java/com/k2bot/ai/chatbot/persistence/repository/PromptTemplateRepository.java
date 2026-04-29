package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.PromptTemplateEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PromptTemplateRepository extends JpaRepository<PromptTemplateEntity, Long> {

    Optional<PromptTemplateEntity> findByAssistantIdAndActiveTrue(Long assistantId);

    List<PromptTemplateEntity> findByAssistantIdOrderByCreatedAtDesc(Long assistantId);
}
