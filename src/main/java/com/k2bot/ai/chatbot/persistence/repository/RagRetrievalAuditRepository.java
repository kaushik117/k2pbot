package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.RagRetrievalAuditEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface RagRetrievalAuditRepository extends JpaRepository<RagRetrievalAuditEntity, Long> {

    List<RagRetrievalAuditEntity> findByRequestId(String requestId);
}
