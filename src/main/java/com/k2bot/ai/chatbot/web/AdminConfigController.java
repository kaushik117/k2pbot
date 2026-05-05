package com.k2bot.ai.chatbot.web;

import com.k2bot.ai.chatbot.config.api.AssistantConfigProvider;
import com.k2bot.ai.chatbot.config.model.ResolvedAssistantConfig;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/admin")
public class AdminConfigController {

    private final AssistantConfigProvider assistantConfigProvider;

    public AdminConfigController(AssistantConfigProvider assistantConfigProvider) {
        this.assistantConfigProvider = assistantConfigProvider;
    }

    @GetMapping("/assistants/{assistantCode}/config")
    public ResponseEntity<ResolvedAssistantConfig> getResolvedConfig(
            @PathVariable String assistantCode,
            @RequestParam(defaultValue = "default") String tenantId) {
        ResolvedAssistantConfig config = assistantConfigProvider.getResolvedConfig(assistantCode, tenantId, null);
        return ResponseEntity.ok(config);
    }

    @DeleteMapping("/assistants/{assistantCode}/config/cache")
    public ResponseEntity<Void> evictCache(
            @PathVariable String assistantCode,
            @RequestParam(defaultValue = "default") String tenantId) {
        assistantConfigProvider.evict(assistantCode, tenantId);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/config/cache")
    public ResponseEntity<Void> evictAllCache() {
        assistantConfigProvider.evictAll();
        return ResponseEntity.noContent().build();
    }
}
