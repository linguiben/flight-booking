
package com.jupiter.ai.controller;

import com.alibaba.cloud.ai.dashscope.chat.DashScopeChatModel;
import com.jupiter.ai.services.BookingTools;
import java.time.LocalDate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.PromptChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.client.advisor.vectorstore.QuestionAnswerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.vectorstore.SearchRequest;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;


/**
 * @desc: Controller for OpenAI chat interactions.
 * @author: Jupiter.Lin
 * @date: 2025/7/16
 */
@CrossOrigin
@RestController
public class OpenAiController {
    private static final Logger log = LoggerFactory.getLogger(OpenAiController.class);
    private final ChatClient chatClient;
    @Autowired
    private VectorStore vectorStore;

    public OpenAiController(String aiEnabledType, ChatMemory chatMemory, BookingTools bookingTools, ToolCallbackProvider mcpTools, DashScopeChatModel dashScopeChatModel, OpenAiChatModel openAiChatModel, @Value("${spring.ai.system-prompt:You are a helpful assistant.}") String systemPrompt) {
        ChatModel chatModel;
        if (aiEnabledType.equals("openai")) {
            this.proxySetup();
            chatModel = openAiChatModel;
        } else {
            if (!aiEnabledType.equals("dashscope")) {
                throw new IllegalArgumentException("Unsupported AI type: " + aiEnabledType);
            }

            chatModel = dashScopeChatModel;
        }

        this.chatClient = ChatClient.builder(chatModel).defaultSystem(systemPrompt).defaultAdvisors(new Advisor[]{PromptChatMemoryAdvisor.builder(chatMemory).build(), new SimpleLoggerAdvisor()}).defaultTools(new Object[]{bookingTools}).defaultToolCallbacks(new ToolCallbackProvider[]{mcpTools}).build();
    }

    @RequestMapping({"/ask"})
    public String chat(@RequestBody String userInput) {
        return this.chatClient.prompt(userInput).advisors(new Advisor[0]).call().content();
    }

    @CrossOrigin
    @GetMapping(
            value = {"/ai/generateAsStream"},
            produces = {"text/event-stream"}
    )
    public Flux<String> generateStreamAsString(@RequestParam(value = "message",defaultValue = "讲个笑话") String message) {
        try {
            Flux<String> content = this.chatClient.prompt().system((s) -> s.param("current_date", LocalDate.now().toString())).user(message).advisors(new Advisor[]{QuestionAnswerAdvisor.builder(this.vectorStore).searchRequest(SearchRequest.builder().similarityThreshold(0.8).topK(6).build()).build()}).stream().content();
            return content.concatWith(Flux.just("[complete]"));
        } catch (Exception e) {
            log.error("Error generating stream: {}", e.getMessage(), e);
            return Flux.just("Error generating response: " + e.getMessage());
        }
    }

    @CrossOrigin
    @GetMapping(
            value = {"/ai/generateAsString"},
            produces = {"text/event-stream"}
    )
    public String generateAsString(@RequestParam(value = "message",defaultValue = "讲个笑话") String message) {
        String content = this.chatClient.prompt().system((s) -> s.param("current_date", LocalDate.now().toString())).user(message + "/no_think").call().content();
        return content.concat("[complete]");
    }

    private void proxySetup() {
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "8086");
        System.setProperty("https.proxyHost", "localhost");
        System.setProperty("https.proxyPort", "8086");
        log.info("System proxy configured for OpenAiChatModel");
    }
}
