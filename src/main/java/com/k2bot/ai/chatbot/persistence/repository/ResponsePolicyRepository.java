package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.ResponsePolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResponsePolicyRepository extends JpaRepository<ResponsePolicyEntity, Long> {

    Optional<ResponsePolicyEntity> findByAssistantId(Long assistantId);
}
