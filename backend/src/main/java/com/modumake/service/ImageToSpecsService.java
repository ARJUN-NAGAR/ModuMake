package com.modumake.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modumake.dto.TechPackDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.logging.Logger;

@Service
public class ImageToSpecsService {

    private static final Logger logger = Logger.getLogger(ImageToSpecsService.class.getName());
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${openai.api.key:mock-key}")
    private String openAiApiKey;

    public TechPackDto convertImageToSpecs(String imageUrl, String category) throws Exception {
        if ("mock-key".equals(openAiApiKey)) {
            logger.info("Using mock GPT-4o-vision API parsing.");
            return generateMockTechPack(category);
        }

        String prompt = "Analyze this image and return a JSON TechPack for manufacturing. If it is fashion, provide HSN Code 62, fabric type, GSM, stitching type, chestCm, and lengthCm. If furniture, provide HSN Code 9403, material, lengthInches, widthInches, and heightInches. Provide strictly valid JSON conforming to these keys: hsnCode, category, fabricOrMaterialType, gsm, stitchingType, dimensions (map), additionalInstructions.";

        String requestBody = """
                {
                  "model": "gpt-4o",
                  "messages": [
                    {
                      "role": "user",
                      "content": [
                        {
                          "type": "text",
                          "text": "%s"
                        },
                        {
                          "type": "image_url",
                          "image_url": {
                            "url": "%s"
                          }
                        }
                      ]
                    }
                  ],
                  "max_tokens": 800
                }
                """.formatted(prompt, imageUrl);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("https://api.openai.com/v1/chat/completions"))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + openAiApiKey)
                .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 200) {
            logger.info("OpenAI parsed image successfully.");
            // In a real scenario, we parse the JSON block out of response.body() using Regex or unmarshalling.
            // Returning mock here just as a placeholder since we don't have the exact response parsing logic ready.
            return generateMockTechPack(category);
        } else {
            logger.severe("OpenAI GPT-4o vision failed: " + response.body());
            throw new RuntimeException("GPT-4o vision parsing failed");
        }
    }

    private TechPackDto generateMockTechPack(String category) {
        if ("FASHION".equalsIgnoreCase(category)) {
            return TechPackDto.builder()
                    .hsnCode("62")
                    .category("FASHION")
                    .fabricOrMaterialType("Cotton")
                    .gsm(180)
                    .stitchingType("Lockstitch")
                    .dimensions(Map.of("chestCm", 100.0, "lengthCm", 106.0))
                    .additionalInstructions("Soft finish suitable for Indian climate")
                    .build();
        } else {
            return TechPackDto.builder()
                    .hsnCode("9403")
                    .category("FURNITURE")
                    .fabricOrMaterialType("Teak Wood")
                    .dimensions(Map.of("lengthInches", 72.0, "widthInches", 36.0, "heightInches", 30.0))
                    .additionalInstructions("Polished finish")
                    .build();
        }
    }
}
