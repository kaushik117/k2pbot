package com.k2bot.ai.chatbot.persistence.repository;

import com.k2bot.ai.chatbot.persistence.entity.ModelRouteEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ModelRouteRepository extends JpaRepository<ModelRouteEntity, Long> {

    List<ModelRouteEntity> findByAssistantIdAndActiveTrueOrderByPriorityAsc(Long assistantId);
}
