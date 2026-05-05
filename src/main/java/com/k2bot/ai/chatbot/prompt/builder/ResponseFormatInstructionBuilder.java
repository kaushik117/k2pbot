package com.k2bot.ai.chatbot.prompt.builder;

import com.k2bot.ai.chatbot.config.model.ResolvedResponseConfig;
import com.k2bot.ai.chatbot.prompt.model.PromptAssemblyInput;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Component
public class ResponseFormatInstructionBuilder {

    public Optional<String> build(PromptAssemblyInput input) {
        ResolvedResponseConfig responseConfig = input.getResolvedConfig().getResponseConfig();
        if (responseConfig == null) {
            return Optional.empty();
        }

        List<String> parts = new ArrayList<>();

        if (responseConfig.getDefaultTone() != null && !responseConfig.getDefaultTone().isBlank()) {
            parts.add("Respond in a " + responseConfig.getDefaultTone() + " tone.");
        }
        if (responseConfig.getDefaultFormat() != null && !responseConfig.getDefaultFormat().isBlank()) {
            parts.add("Format your response as: " + responseConfig.getDefaultFormat() + ".");
        }
        if (responseConfig.isMarkdownEnabled()) {
            parts.add("Use markdown formatting where appropriate.");
        }
        if (responseConfig.getMaxOutputTokens() != null) {
            parts.add("Keep your response concise and within approximately "
                    + responseConfig.getMaxOutputTokens() + " tokens.");
        }

        return parts.isEmpty() ? Optional.empty() : Optional.of(String.join(" ", parts));
    }
}
