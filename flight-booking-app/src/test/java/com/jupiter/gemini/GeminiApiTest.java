package com.jupiter.gemini;

/**
 * @desc: TODO
 * @author: Jupiter.Lin
 * @date: 2025/7/17
 */

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;

public class GeminiApiTest {
    public static void main(String[] args) {
        System.setProperty("http.proxyHost", "localhost");
        System.setProperty("http.proxyPort", "8086");
        System.setProperty("java.net.useSystemProxies", "true");
        Client client = new Client();
        GenerateContentResponse response = client.models.generateContent("gemini-2.0-flash", "Introduce Spring AI.", (GenerateContentConfig)null);
        System.out.println(response.text());
    }
}
