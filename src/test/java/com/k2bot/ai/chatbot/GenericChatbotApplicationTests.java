package com.k2bot.ai.chatbot;

import com.k2bot.ai.chatbot.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class GenericChatbotApplicationTests {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void contextLoads() {
        assertThat(globalExceptionHandler).isNotNull();
    }
}
