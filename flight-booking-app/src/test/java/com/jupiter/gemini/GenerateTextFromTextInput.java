package com.jupiter.gemini;

/**
 * @desc: TODO
 * @author: Jupiter.Lin
 * @date: 2025/7/17
 */

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;

class GenerateTextFromTextInput {
    public static void main(String[] args) {
        System.setProperty("http.proxyHost", "27.0.0.1");
        System.setProperty("http.proxyPort", "8086");
        Client client = Client.builder().apiKey("you-key").build();
        System.out.println("Using Gemini API to generate text from text input...");
        GenerateContentResponse response = client.models.generateContent("gemini-2.5-flash", "Introduce Spring AI.", (GenerateContentConfig)null);
        System.out.println(response.text());
    }

    static {
        System.setProperty("http.proxyHost", "127.0.0.1");
        System.setProperty("http.proxyPort", "8086");
    }
}
