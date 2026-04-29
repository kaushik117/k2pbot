package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.SafetyPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SafetyPolicyRepository extends JpaRepository<SafetyPolicyEntity, Long> {

    Optional<SafetyPolicyEntity> findByAssistantId(Long assistantId);
}
