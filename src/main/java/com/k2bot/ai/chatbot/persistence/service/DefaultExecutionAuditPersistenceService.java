package com.k2bot.ai.chatbot.persistence.service;

import com.k2bot.ai.chatbot.persistence.entity.ChatExecutionEntity;
import com.k2bot.ai.chatbot.persistence.entity.RagRetrievalAuditEntity;
import com.k2bot.ai.chatbot.persistence.entity.ToolExecutionAuditEntity;
import com.k2bot.ai.chatbot.persistence.repository.ChatExecutionRepository;
import com.k2bot.ai.chatbot.persistence.repository.RagRetrievalAuditRepository;
import com.k2bot.ai.chatbot.persistence.repository.ToolExecutionAuditRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class DefaultExecutionAuditPersistenceService implements ExecutionAuditPersistenceService {

    private final ChatExecutionRepository executionRepository;
    private final ToolExecutionAuditRepository toolAuditRepository;
    private final RagRetrievalAuditRepository ragAuditRepository;

    public DefaultExecutionAuditPersistenceService(ChatExecutionRepository executionRepository,
                                                   ToolExecutionAuditRepository toolAuditRepository,
                                                   RagRetrievalAuditRepository ragAuditRepository) {
        this.executionRepository = executionRepository;
        this.toolAuditRepository = toolAuditRepository;
        this.ragAuditRepository = ragAuditRepository;
    }

    @Override
    @Transactional
    public void saveExecutionStart(ChatExecutionEntity execution) {
        executionRepository.save(execution);
    }

    @Override
    @Transactional
    public void saveExecutionCompletion(ChatExecutionEntity execution) {
        executionRepository.save(execution);
    }

    @Override
    @Transactional
    public void saveToolAudits(List<ToolExecutionAuditEntity> audits) {
        if (audits == null || audits.isEmpty()) {
            return;
        }
        toolAuditRepository.saveAll(audits);
    }

    @Override
    @Transactional
    public void saveRagAudit(RagRetrievalAuditEntity audit) {
        if (audit == null) {
            return;
        }
        ragAuditRepository.save(audit);
    }
}
