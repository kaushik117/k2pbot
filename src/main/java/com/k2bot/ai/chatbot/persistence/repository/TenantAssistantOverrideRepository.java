package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.TenantAssistantOverrideEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TenantAssistantOverrideRepository extends JpaRepository<TenantAssistantOverrideEntity, Long> {

    List<TenantAssistantOverrideEntity> findByTenantIdAndAssistantIdAndActiveTrue(String tenantId, Long assistantId);
}
