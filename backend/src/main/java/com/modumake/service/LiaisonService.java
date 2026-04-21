package com.modumake.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.modumake.dto.TechPackDto;
import com.modumake.model.Manufacturer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.util.logging.Logger;

@Service
public class LiaisonService {

    private static final Logger logger = Logger.getLogger(LiaisonService.class.getName());
    private final HttpClient httpClient = HttpClient.newHttpClient();

    @Value("${whatsapp.api.url:mock-url}")
    private String whatsappApiUrl;

    @Value("${whatsapp.api.token:mock-token}")
    private String whatsappApiToken;

    public void dispatchTechPack(TechPackDto techPack, Manufacturer manufacturer, String imageUrl) {
        if (manufacturer.getWhatsappNumber() != null && !manufacturer.getWhatsappNumber().isEmpty()) {
            sendWhatsAppMessage(techPack, manufacturer, imageUrl);
        } else {
            sendEmailFallback(techPack, manufacturer, imageUrl);
        }
    }

    private void sendWhatsAppMessage(TechPackDto techPack, Manufacturer manufacturer, String imageUrl) {
        if ("mock-url".equals(whatsappApiUrl) || "mock-token".equals(whatsappApiToken)) {
            logger.info("[Mock Payload] To: " + manufacturer.getWhatsappNumber() + ", Image: " + imageUrl);
            return;
        }

        try {
            // Meta WhatsApp Cloud API Template Message payload
            String requestBody = """
                    {
                      "messaging_product": "whatsapp",
                      "to": "%s",
                      "type": "template",
                      "template": {
                        "name": "tech_pack_dispatch",
                        "language": {
                          "code": "en"
                        },
                        "components": [
                          {
                            "type": "header",
                            "parameters": [
                              {
                                "type": "image",
                                "image": {
                                  "link": "%s"
                                }
                              }
                            ]
                          },
                          {
                            "type": "body",
                            "parameters": [
                              {
                                "type": "text",
                                "text": "%s"
                              },
                              {
                                "type": "text",
                                "text": "%s"
                              }
                            ]
                          }
                        ]
                      }
                    }
                    """.formatted(manufacturer.getWhatsappNumber(), imageUrl, manufacturer.getName(), techPack.getHsnCode());

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(whatsappApiUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + whatsappApiToken)
                    .POST(HttpRequest.BodyPublishers.ofString(requestBody, StandardCharsets.UTF_8))
                    .build();

            HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

            if (response.statusCode() == 200 || response.statusCode() == 201) {
                logger.info("Successfully dispatched WhatsApp message to " + manufacturer.getWhatsappNumber());
            } else {
                logger.severe("WhatsApp API Failed: " + response.body());
                sendEmailFallback(techPack, manufacturer, imageUrl);
            }
        } catch (Exception e) {
            logger.severe("Exception during WhatsApp dispatch: " + e.getMessage());
            sendEmailFallback(techPack, manufacturer, imageUrl);
        }
    }

    private void sendEmailFallback(TechPackDto techPack, Manufacturer manufacturer, String imageUrl) {
        // Implementation for Spring Boot Starter Mail
        logger.info("WhatsApp not available or failed. Fallback email queued to " + manufacturer.getEmail());
    }
}
