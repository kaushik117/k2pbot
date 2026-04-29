package com.k2bot.ai.chatbot.persistence.entity;

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
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "ai_assistant_safety_policy", indexes = {
        @Index(name = "idx_ai_safety_policy_assistant", columnList = "assistant_id", unique = true)
})
public class SafetyPolicyEntity extends BaseAuditEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "assistant_id", nullable = false, unique = true)
    private AssistantEntity assistant;

    @Column(name = "block_unknown_tools")
    private Boolean blockUnknownTools;

    @Column(name = "block_without_rag_when_grounded_mode")
    private Boolean blockWithoutRagWhenGroundedMode;

    @Column(name = "allow_direct_model_answer_without_context")
    private Boolean allowDirectModelAnswerWithoutContext;

    @Column(name = "mask_sensitive_data_in_logs")
    private Boolean maskSensitiveDataInLogs;

    @Convert(converter = StringListJsonConverter.class)
    @Column(name = "disallowed_topics")
    private List<String> disallowedTopics = new ArrayList<>();

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

    public Boolean getBlockUnknownTools() {
        return blockUnknownTools;
    }

    public void setBlockUnknownTools(Boolean blockUnknownTools) {
        this.blockUnknownTools = blockUnknownTools;
    }

    public Boolean getBlockWithoutRagWhenGroundedMode() {
        return blockWithoutRagWhenGroundedMode;
    }

    public void setBlockWithoutRagWhenGroundedMode(Boolean blockWithoutRagWhenGroundedMode) {
        this.blockWithoutRagWhenGroundedMode = blockWithoutRagWhenGroundedMode;
    }

    public Boolean getAllowDirectModelAnswerWithoutContext() {
        return allowDirectModelAnswerWithoutContext;
    }

    public void setAllowDirectModelAnswerWithoutContext(Boolean allowDirectModelAnswerWithoutContext) {
        this.allowDirectModelAnswerWithoutContext = allowDirectModelAnswerWithoutContext;
    }

    public Boolean getMaskSensitiveDataInLogs() {
        return maskSensitiveDataInLogs;
    }

    public void setMaskSensitiveDataInLogs(Boolean maskSensitiveDataInLogs) {
        this.maskSensitiveDataInLogs = maskSensitiveDataInLogs;
    }

    public List<String> getDisallowedTopics() {
        return disallowedTopics;
    }

    public void setDisallowedTopics(List<String> disallowedTopics) {
        this.disallowedTopics = disallowedTopics;
    }
}
