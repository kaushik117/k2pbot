package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.ToolExecutionAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ToolExecutionAuditRepository extends JpaRepository<ToolExecutionAuditEntity, Long> {

    List<ToolExecutionAuditEntity> findByRequestId(String requestId);
}
