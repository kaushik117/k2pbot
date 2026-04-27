package com.yourcompany.ai.chatbot;

import com.yourcompany.ai.chatbot.web.GlobalExceptionHandler;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class GenericChatbotApplicationTests {

    @Autowired
    private GlobalExceptionHandler globalExceptionHandler;

    @Test
    void contextLoads() {
        assertThat(globalExceptionHandler).isNotNull();
    }
}
