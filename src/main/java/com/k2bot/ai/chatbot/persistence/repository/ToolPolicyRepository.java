package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.ToolPolicyEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToolPolicyRepository extends JpaRepository<ToolPolicyEntity, Long> {

    List<ToolPolicyEntity> findByAssistantIdAndEnabledTrue(Long assistantId);
}
