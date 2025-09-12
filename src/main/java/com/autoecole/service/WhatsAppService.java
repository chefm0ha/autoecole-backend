package com.autoecole.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.http.*;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
public class WhatsAppService {

    @Value("${whatsapp.api.url:https://api.whatsapp.business}")
    private String whatsappApiUrl;

    @Value("${whatsapp.api.token:}")
    private String whatsappApiToken;

    @Value("${whatsapp.enabled:false}")
    private boolean whatsappEnabled;

    private final RestTemplate restTemplate;

    public WhatsAppService() {
        this.restTemplate = new RestTemplate();
    }

    /**
     * Send WhatsApp message
     *
     * @param phoneNumber The recipient's phone number (format: +212XXXXXXXXX)
     * @param message The message to send
     * @return true if sent successfully, false otherwise
     */
    public boolean sendMessage(String phoneNumber, String message) {
        if (!whatsappEnabled) {
            log.info("WhatsApp is disabled. Would send message to {}: {}", phoneNumber, message);
            return true; // Return true for mock/testing purposes
        }

        try {
            // Validate phone number format
            if (!isValidPhoneNumber(phoneNumber)) {
                log.error("Invalid phone number format: {}", phoneNumber);
                return false;
            }

            // Prepare the WhatsApp API request
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("messaging_product", "whatsapp");
            requestBody.put("to", phoneNumber);
            requestBody.put("type", "text");

            Map<String, String> textContent = new HashMap<>();
            textContent.put("body", message);
            requestBody.put("text", textContent);

            // Set headers
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.setBearerAuth(whatsappApiToken);

            HttpEntity<Map<String, Object>> request = new HttpEntity<>(requestBody, headers);

            // Send the request
            ResponseEntity<String> response = restTemplate.exchange(
                    whatsappApiUrl + "/messages",
                    HttpMethod.POST,
                    request,
                    String.class
            );

            if (response.getStatusCode().is2xxSuccessful()) {
                log.info("WhatsApp message sent successfully to: {}", phoneNumber);
                return true;
            } else {
                log.error("Failed to send WhatsApp message. Status: {}, Response: {}",
                        response.getStatusCode(), response.getBody());
                return false;
            }

        } catch (Exception e) {
            log.error("Error sending WhatsApp message to {}: {}", phoneNumber, e.getMessage(), e);
            return false;
        }
    }

    private boolean isValidPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return false;
        }
        // Moroccan phone number validation: +212XXXXXXXXX
        return phoneNumber.matches("^\\+212[5-7]\\d{8}$");
    }
}