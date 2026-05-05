package com.k2bot.ai.chatbot.web;

import com.k2bot.ai.chatbot.common.ChatbotException;
import com.k2bot.ai.chatbot.config.exception.AssistantConfigNotFoundException;
import com.k2bot.ai.chatbot.config.exception.AssistantConfigResolutionException;
import com.k2bot.ai.chatbot.config.exception.AssistantConfigValidationException;
import com.k2bot.ai.chatbot.config.exception.KnowledgeBaseNotFoundException;
import com.k2bot.ai.chatbot.config.exception.UnauthorizedRuntimeOverrideException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(AssistantConfigNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleConfigNotFound(AssistantConfigNotFoundException ex) {
        log.warn("Assistant config not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "CONFIG_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(AssistantConfigValidationException.class)
    public ResponseEntity<Map<String, Object>> handleConfigValidation(AssistantConfigValidationException ex) {
        log.warn("Config validation error: {}", ex.getMessage());
        return build(HttpStatus.UNPROCESSABLE_ENTITY, "CONFIG_INVALID", ex.getMessage());
    }

    @ExceptionHandler(AssistantConfigResolutionException.class)
    public ResponseEntity<Map<String, Object>> handleConfigResolution(AssistantConfigResolutionException ex) {
        log.error("Config resolution error for assistantCode={}", ex.getAssistantCode(), ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "CONFIG_RESOLUTION_ERROR", ex.getMessage());
    }

    @ExceptionHandler(KnowledgeBaseNotFoundException.class)
    public ResponseEntity<Map<String, Object>> handleKnowledgeBaseNotFound(KnowledgeBaseNotFoundException ex) {
        log.warn("Knowledge base not found: {}", ex.getMessage());
        return build(HttpStatus.NOT_FOUND, "CONFIG_NOT_FOUND", ex.getMessage());
    }

    @ExceptionHandler(UnauthorizedRuntimeOverrideException.class)
    public ResponseEntity<Map<String, Object>> handleUnauthorizedOverride(UnauthorizedRuntimeOverrideException ex) {
        log.warn("Unauthorized runtime override: {}", ex.getMessage());
        return build(HttpStatus.FORBIDDEN, "INVALID_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(ChatbotException.class)
    public ResponseEntity<Map<String, Object>> handleChatbotException(ChatbotException ex) {
        log.warn("Chatbot exception: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "CHATBOT_ERROR", ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Map<String, Object>> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument: {}", ex.getMessage());
        return build(HttpStatus.BAD_REQUEST, "INVALID_REQUEST", ex.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>> handleUnexpected(Exception ex) {
        log.error("Unexpected error", ex);
        return build(HttpStatus.INTERNAL_SERVER_ERROR, "INTERNAL_ERROR", "Unexpected server error");
    }

    private ResponseEntity<Map<String, Object>> build(HttpStatus status, String code, String message) {
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("timestamp", Instant.now().toString());
        body.put("status", status.value());
        body.put("error", code);
        body.put("message", message);
        return ResponseEntity.status(status).body(body);
    }
}
