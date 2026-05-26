package aiforge_backend.controller;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
@CrossOrigin("*")
public class AiController {

    @Value("${tavily.api.key}")
    private String tavilyApiKey;

    @Value("${openrouter.api.key}")
    private String openrouterApiKey;

    @GetMapping("/generate")
    public Map<String, String> generate(
            @RequestParam String prompt
    ) {

        Map<String, String> result =
                new HashMap<>();

        try {

            RestTemplate restTemplate =
                    new RestTemplate();

            /* =========================
               TAVILY SEARCH
            ========================= */

            String tavilyUrl =
                    "https://api.tavily.com/search";

            HttpHeaders tavilyHeaders =
                    new HttpHeaders();

            tavilyHeaders.setContentType(
                    MediaType.APPLICATION_JSON
            );

            Map<String, Object> tavilyBody =
                    new HashMap<>();

            tavilyBody.put(
                    "api_key",
                    tavilyApiKey
            );

            tavilyBody.put(
                    "query",
                    prompt
            );

            tavilyBody.put(
                    "search_depth",
                    "advanced"
            );

            tavilyBody.put(
                    "max_results",
                    5
            );

            HttpEntity<Map<String, Object>>
                    tavilyRequest =
                    new HttpEntity<>(
                            tavilyBody,
                            tavilyHeaders
                    );

            ResponseEntity<Map> tavilyResponse =
                    restTemplate.exchange(
                            tavilyUrl,
                            HttpMethod.POST,
                            tavilyRequest,
                            Map.class
                    );

            List<Map<String, Object>> results =
                    (List<Map<String, Object>>)
                            tavilyResponse
                                    .getBody()
                                    .get("results");

            StringBuilder liveData =
                    new StringBuilder();

            if (results != null) {

                for (
                        Map<String, Object> item
                                : results
                ) {

                    Object content =
                            item.get("content");

                    if (content != null) {

                        liveData
                                .append(content.toString())
                                .append("\n\n");
                    }
                }
            }

            /* =========================
               OPENROUTER AI
            ========================= */

            String aiUrl =
                    "https://openrouter.ai/api/v1/chat/completions";

            HttpHeaders aiHeaders =
                    new HttpHeaders();

            aiHeaders.setContentType(
                    MediaType.APPLICATION_JSON
            );

            aiHeaders.setBearerAuth(
                    openrouterApiKey
            );

            aiHeaders.set(
                    "HTTP-Referer",
                    "http://localhost:5173"
            );

            aiHeaders.set(
                    "X-Title",
                    "AIForge"
            );

            Map<String, Object> aiBody =
                    new HashMap<>();

            /* =========================
               WORKING MODEL
            ========================= */

            aiBody.put(
                    "model",
                    "openai/gpt-3.5-turbo"
            );

            List<Map<String, String>> messages =
                    new ArrayList<>();

            Map<String, String> systemMessage =
                    new HashMap<>();

            systemMessage.put(
                    "role",
                    "system"
            );

            systemMessage.put(
                    "content",

                    "You are AIForge AI assistant. "
                            +
                            "Answer clearly and professionally. "
                            +
                            "Use live internet data when available.\n\n"
                            +
                            liveData.toString()
            );

            Map<String, String> userMessage =
                    new HashMap<>();

            userMessage.put(
                    "role",
                    "user"
            );

            userMessage.put(
                    "content",
                    prompt
            );

            messages.add(systemMessage);

            messages.add(userMessage);

            aiBody.put(
                    "messages",
                    messages
            );

            HttpEntity<Map<String, Object>>
                    aiRequest =
                    new HttpEntity<>(
                            aiBody,
                            aiHeaders
                    );

            ResponseEntity<Map> aiResponse =
                    restTemplate.exchange(
                            aiUrl,
                            HttpMethod.POST,
                            aiRequest,
                            Map.class
                    );

            List<Map<String, Object>> choices =
                    (List<Map<String, Object>>)
                            aiResponse
                                    .getBody()
                                    .get("choices");

            Map<String, Object> firstChoice =
                    choices.get(0);

            Map<String, String> message =
                    (Map<String, String>)
                            firstChoice.get("message");

            String answer =
                    message.get("content");

            result.put(
                    "answer",
                    answer
            );

        }

        catch (Exception e) {

            result.put(
                    "answer",
                    "AI Error : "
                            + e.getMessage()
            );
        }

        return result;
    }
}