package com.jupiter.ai.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;


/**
 * @desc: Conditional proxy setter for OpenAiChatModel to set system properties for HTTP and HTTPS proxy.
 * @author: Jupiter.Lin
 * @date: 2025/7/16
 */
public class ConditionalProxySetter implements BeanPostProcessor {
    private static final Logger log = LoggerFactory.getLogger(ConditionalProxySetter.class);
    private boolean proxySet = false;

    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        if (bean instanceof OpenAiChatModel && !this.proxySet) {
            System.setProperty("http.proxyHost", "localhost");
            System.setProperty("http.proxyPort", "8086");
            System.setProperty("https.proxyHost", "localhost");
            System.setProperty("https.proxyPort", "8086");
            log.info("System proxy configured for OpenAiChatModel");
            this.proxySet = true;
        }

        return bean;
    }
}
