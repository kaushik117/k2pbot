package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.MemoryPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemoryPolicyRepository extends JpaRepository<MemoryPolicyEntity, Long> {

    Optional<MemoryPolicyEntity> findByAssistantId(Long assistantId);
}
