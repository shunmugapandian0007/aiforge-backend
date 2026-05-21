package aiforge_backend.controller;

import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

@RestController
@CrossOrigin("*")

public class AiController {

    @Value("${openrouter.api.key}")
    private String apiKey;

    private final WebClient webClient =
            WebClient.builder().build();

    @PostMapping("/generate")

    public Object generateContent(
            @RequestBody Map<String, String> body
    ) {

        try {

            String userPrompt =
                    body.get("prompt");

            String lowerPrompt =
                    userPrompt.toLowerCase();

            // =========================
            // LIVE CURRENT DATA
            // =========================

            String liveData = "";

            if(

                    lowerPrompt.contains("today")
                    ||

                    lowerPrompt.contains("current")
                    ||

                    lowerPrompt.contains("latest")
                    ||

                    lowerPrompt.contains("cm")
                    ||

                    lowerPrompt.contains("news")
            ){

                try{

                    Document doc = Jsoup.connect(
                            "https://en.wikipedia.org/wiki/Chief_Minister_of_Tamil_Nadu"
                    ).get();

                    String text =
                            doc.text();

                    liveData =
                            "LIVE INTERNET DATA: " + text;

                }

                catch(Exception ex){

                    liveData =
                            "No live data available";
                }
            }

            // =========================
            // SYSTEM PROMPT
            // =========================

     String systemPrompt = """

You are AIForge.

You are a modern conversational AI assistant exactly like ChatGPT.

RULES:

- Reply naturally like a real human assistant.
- Be smart, modern, friendly, and conversational.
- Give clean formatted answers.
- Use markdown formatting.
- Use proper code blocks for programming.
- Explain clearly.
- Use headings and bullet points when needed.
- If user asks coding questions:
  - give clean professional code
  - explain step by step
  - format code beautifully
- Never give robotic replies.
- Never repeat the question.
- Never say "As an AI language model".
- Never mention knowledge cutoff.
- Sound exactly like ChatGPT.
- Keep answers visually clean.
- For short questions give short answers.
- For complex questions give detailed answers.
- Match user's language style.
- If user speaks Tamil-English mix, reply naturally in Tamil-English mix.
- Act premium and intelligent.

""";

            // =========================
            // USER MESSAGE
            // =========================

            String finalPrompt =

                    liveData +

                    "\n\nUser Question:\n"

                    + userPrompt;

            // =========================
            // OPENROUTER API
            // =========================

            String url =
                    "https://openrouter.ai/api/v1/chat/completions";

            Map<String, Object> requestBody =
                    Map.of(

                            "model",
                            "openai/gpt-4o-mini",

                            "messages",

                            List.of(

                                    Map.of(
                                            "role",
                                            "system",

                                            "content",
                                            systemPrompt
                                    ),

                                    Map.of(
                                            "role",
                                            "user",

                                            "content",
                                            finalPrompt
                                    )
                            )
                    );

            Object response =
                    webClient.post()

                            .uri(url)

                            .header(
                                    HttpHeaders.AUTHORIZATION,

                                    "Bearer " + apiKey
                            )

                            .header(
                                    "HTTP-Referer",

                                    "http://localhost:5173"
                            )

                            .header(
                                    "X-Title",

                                    "AIForge"
                            )

                            .contentType(
                                    MediaType.APPLICATION_JSON
                            )

                            .bodyValue(requestBody)

                            .retrieve()

                            .bodyToMono(Object.class)

                            .block();

            return response;
        }

        catch (Exception e) {

            e.printStackTrace();

            return Map.of(
                    "error",
                    e.getMessage()
            );
        }
    }
}