package com.k2bot.ai.chatbot.persistence.service;

import com.k2bot.ai.chatbot.persistence.entity.ChatExecutionEntity;
import com.k2bot.ai.chatbot.persistence.entity.RagRetrievalAuditEntity;
import com.k2bot.ai.chatbot.persistence.entity.ToolExecutionAuditEntity;

import java.util.List;

public interface ExecutionAuditPersistenceService {

    void saveExecutionStart(ChatExecutionEntity execution);

    void saveExecutionCompletion(ChatExecutionEntity execution);

    void saveToolAudits(List<ToolExecutionAuditEntity> audits);

    void saveRagAudit(RagRetrievalAuditEntity audit);
}
