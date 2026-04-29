package com.k2bot.ai.chatbot.persistence.entity;

import com.k2bot.ai.chatbot.persistence.converter.JsonMapConverter;
import com.k2bot.ai.chatbot.persistence.converter.StringListJsonConverter;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "ai_assistant_prompt", indexes = {
        @Index(name = "idx_ai_assistant_prompt_assistant", columnList = "assistant_id")
})
public class PromptTemplateEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assistant_id", nullable = false)
    private AssistantEntity assistant;

    @Column(name = "version", length = 50)
    private String version;

    @Column(name = "active", nullable = false)
    private Boolean active = Boolean.TRUE;

    @Lob
    @Column(name = "system_prompt_template")
    private String systemPromptTemplate;

    @Lob
    @Column(name = "developer_prompt_template")
    private String developerPromptTemplate;

    @Convert(converter = JsonMapConverter.class)
    @Column(name = "prompt_variables")
    private Map<String, String> promptVariables = new LinkedHashMap<>();

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "guardrail_instructions")
    private List<String> guardrailInstructions = new ArrayList<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public AssistantEntity getAssistant() {
        return assistant;
    }

    public void setAssistant(AssistantEntity assistant) {
        this.assistant = assistant;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }

    public String getSystemPromptTemplate() {
        return systemPromptTemplate;
    }

    public void setSystemPromptTemplate(String systemPromptTemplate) {
        this.systemPromptTemplate = systemPromptTemplate;
    }

    public String getDeveloperPromptTemplate() {
        return developerPromptTemplate;
    }

    public void setDeveloperPromptTemplate(String developerPromptTemplate) {
        this.developerPromptTemplate = developerPromptTemplate;
    }

    public Map<String, String> getPromptVariables() {
        return promptVariables;
    }

    public void setPromptVariables(Map<String, String> promptVariables) {
        this.promptVariables = promptVariables;
    }

    public List<String> getGuardrailInstructions() {
        return guardrailInstructions;
    }

    public void setGuardrailInstructions(List<String> guardrailInstructions) {
        this.guardrailInstructions = guardrailInstructions;
    }
}
