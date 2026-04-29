package com.k2bot.ai.chatbot.persistence.support;

import com.k2bot.ai.chatbot.persistence.exception.PersistenceLayerException;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Component
public class SchemaStartupValidator {

    private static final Logger log = LoggerFactory.getLogger(SchemaStartupValidator.class);

    private static final List<String> REQUIRED_TABLES = List.of(
            "ai_assistant",
            "ai_assistant_prompt",
            "ai_assistant_model_route",
            "ai_assistant_memory_policy",
            "ai_assistant_rag_policy",
            "ai_assistant_tool_policy",
            "ai_assistant_safety_policy",
            "ai_assistant_response_policy",
            "ai_knowledge_base",
            "ai_tenant_assistant_override",
            "ai_chat_session",
            "ai_chat_message",
            "ai_chat_execution",
            "ai_tool_execution_audit",
            "ai_rag_retrieval_audit"
    );

    @PersistenceContext
    private EntityManager entityManager;

    @EventListener(ApplicationReadyEvent.class)
    @Transactional(readOnly = true)
    public void validateSchema() {
        for (String table : REQUIRED_TABLES) {
            try {
                entityManager.createNativeQuery("SELECT 1 FROM " + table + " WHERE 1 = 0").getResultList();
            } catch (RuntimeException ex) {
                throw new PersistenceLayerException(
                        "Required table '" + table + "' is not present or not queryable", ex);
            }
        }
        log.info("Schema validation passed. {} chatbot tables present.", REQUIRED_TABLES.size());
    }
}
