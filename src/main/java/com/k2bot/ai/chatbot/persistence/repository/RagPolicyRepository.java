package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.RagPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RagPolicyRepository extends JpaRepository<RagPolicyEntity, Long> {

    Optional<RagPolicyEntity> findByAssistantId(Long assistantId);
}
