package com.jupiter.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
//import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.vectorstore.SimpleVectorStore;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;


/**
 * @desc: Configuration class for AI-related beans.
 * @author: Jupiter.Lin
 * @date: 2025/7/16
 */
@Configuration
public class Config {
    private static final Logger log = LoggerFactory.getLogger(Config.class);

    /**
     * Sets the AI enabled type based on the application properties.
     * @param aiEnabledType
     * @return String
     * @author Jupiter.Lin
     * @date 2025/7/16
     */
    @Bean
    public String aiEnabledType(@Value("${spring.ai.enabled-type:openai}") String aiEnabledType) {
        System.setProperty("spring.ai.enabled-type", aiEnabledType);
        log.info("AI Enabled Type: {}", aiEnabledType);
        return aiEnabledType;
    }

    @Bean
    public ChatMemory chatMemory() {
        return MessageWindowChatMemory.builder().maxMessages(10).build();
    }

//    @Bean
//    public VectorStore vectorStore(EmbeddingModel embeddingModel) {
//        SimpleVectorStore.SimpleVectorStoreBuilder builder = SimpleVectorStore.builder(embeddingModel);
//        return builder.build();
//    }
}
