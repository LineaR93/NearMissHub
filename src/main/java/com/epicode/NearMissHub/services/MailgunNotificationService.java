package com.epicode.NearMissHub.services;

// Service layer: business rules live here so controllers stay thin and readable.

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class MailgunNotificationService {

    private static final Logger log = LoggerFactory.getLogger(MailgunNotificationService.class);

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${mailgun.key}") private String apiKey;
    @Value("${mailgun.domain}") private String domain;
    @Value("${mailgun.from}") private String from;

    public void sendEmail(String to, String subject, String text) {
        try {
            String url = "https://api.mailgun.net/v3/" + domain + "/messages";

            HttpHeaders headers = new HttpHeaders();
            headers.setBasicAuth("api", apiKey);
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("from", from);
            body.add("to", to);
            body.add("subject", subject);
            body.add("text", text);

            HttpEntity<MultiValueMap<String, String>> req = new HttpEntity<>(body, headers);
            restTemplate.postForEntity(url, req, String.class);
            log.info("Mailgun sent email to={} subject={}", to, subject);
        } catch (Exception e) {
            // In the exam demo I prefer a soft-failure (API still works), but I also want
            // a visible hint in the console if Mailgun isn't configured.
            log.warn("Mailgun send failed: {}", e.getMessage());
        }
    }
}
