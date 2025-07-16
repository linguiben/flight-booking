//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.jupiter.ai.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClientRequest;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;

public class LoggingAdvisor extends SimpleLoggerAdvisor {
    private static final Logger logger = LoggerFactory.getLogger(LoggingAdvisor.class);

    private void logRequest(ChatClientRequest request) {
        logger.info("debug -- request: {}", SimpleLoggerAdvisor.DEFAULT_REQUEST_TO_STRING.apply(request));
    }
}
