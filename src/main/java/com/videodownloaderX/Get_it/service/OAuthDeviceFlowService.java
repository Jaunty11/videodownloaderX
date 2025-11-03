package com.videodownloaderX.Get_it.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.videodownloaderX.Get_it.model.DeviceCodeResponse;
import com.videodownloaderX.Get_it.model.TokenResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.attribute.PosixFilePermission;
import java.util.*;

@Service
public class OAuthDeviceFlowService {

    private static final String DEVICE_CODE_URL = "https://github.com/login/device/code";
    private static final String TOKEN_URL = "https://github.com/login/oauth/access_token";
    private static final String TOKEN_FILE = System.getProperty("user.home") + "/.videodownloader/token.json";

    @Value("${github.oauth.client-id}")
    private String clientId;

    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public boolean isAuthenticated() {
        File tokenFile = new File(TOKEN_FILE);
        if (!tokenFile.exists()) {
            return false;
        }

        try {
            String tokenJson = Files.readString(tokenFile.toPath());
            TokenResponse token = objectMapper.readValue(tokenJson, TokenResponse.class);
            return validateToken(token.getAccessToken());
        } catch (IOException e) {
            return false;
        }
    }

    public void authenticate() throws IOException, InterruptedException {
        System.out.println("\n━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("🔐 GitHub Authentication Required");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━\n");

        DeviceCodeResponse deviceCode = requestDeviceCode();
        displayUserInstructions(deviceCode);
        TokenResponse token = pollForAuthorization(deviceCode);
        saveToken(token);

        System.out.println("\n✅ Authentication successful!\n");
    }

    public String getAuthenticatedUsername() throws IOException {
        TokenResponse token = loadToken();

        HttpHeaders headers = new HttpHeaders();
        headers.setBearerAuth(token.getAccessToken());

        HttpEntity<String> request = new HttpEntity<>(headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                "https://api.github.com/user",
                HttpMethod.GET,
                request,
                Map.class
        );

        return (String) response.getBody().get("login");
    }

    private DeviceCodeResponse requestDeviceCode() {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("scope", "read:user");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<DeviceCodeResponse> response = restTemplate.postForEntity(
                DEVICE_CODE_URL,
                request,
                DeviceCodeResponse.class
        );

        return response.getBody();
    }

    private void displayUserInstructions(DeviceCodeResponse deviceCode) {
        System.out.println("Please complete authentication:\n");
        System.out.println("  1️⃣  Visit: " + deviceCode.getVerificationUri());
        System.out.println("  2️⃣  Enter code: \033[1;33m" + deviceCode.getUserCode() + "\033[0m\n");
        System.out.println("Waiting for you to authorize...");
        System.out.println("(Press Ctrl+C to cancel)\n");

        try {
            Desktop.getDesktop().browse(new URI(deviceCode.getVerificationUri()));
        } catch (Exception ignored) {
        }
    }

    private TokenResponse pollForAuthorization(DeviceCodeResponse deviceCode) throws InterruptedException {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        headers.setAccept(Collections.singletonList(MediaType.APPLICATION_JSON));

        int interval = deviceCode.getInterval();
        int maxAttempts = 180;

        for (int attempt = 0; attempt < maxAttempts; attempt++) {
            MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
            body.add("client_id", clientId);
            body.add("device_code", deviceCode.getDeviceCode());
            body.add("grant_type", "urn:ietf:params:oauth:grant-type:device_code");

            HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

            try {
                ResponseEntity<TokenResponse> response = restTemplate.postForEntity(
                        TOKEN_URL,
                        request,
                        TokenResponse.class
                );

                TokenResponse token = response.getBody();
                if (token != null && token.getAccessToken() != null) {
                    return token;
                }
            } catch (HttpClientErrorException e) {
                String error = e.getResponseBodyAsString();
                if (error.contains("authorization_pending")) {
                    System.out.print(".");
                } else if (error.contains("slow_down")) {
                    interval += 5;
                } else if (error.contains("access_denied")) {
                    throw new RuntimeException("Authentication denied by user");
                } else if (error.contains("expired_token")) {
                    throw new RuntimeException("Authentication timeout");
                }
            }

            Thread.sleep(interval * 1000L);
        }

        throw new RuntimeException("Authentication timeout");
    }

    private void saveToken(TokenResponse token) throws IOException {
        File tokenFile = new File(TOKEN_FILE);
        tokenFile.getParentFile().mkdirs();

        String tokenJson = objectMapper.writeValueAsString(token);
        Files.writeString(tokenFile.toPath(), tokenJson);

        try {
            Set<PosixFilePermission> perms = new HashSet<>();
            perms.add(PosixFilePermission.OWNER_READ);
            perms.add(PosixFilePermission.OWNER_WRITE);
            Files.setPosixFilePermissions(tokenFile.toPath(), perms);
        } catch (UnsupportedOperationException ignored) {
        }
    }

    private TokenResponse loadToken() throws IOException {
        String tokenJson = Files.readString(new File(TOKEN_FILE).toPath());
        return objectMapper.readValue(tokenJson, TokenResponse.class);
    }

    private boolean validateToken(String accessToken) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setBearerAuth(accessToken);

            HttpEntity<String> request = new HttpEntity<>(headers);
            ResponseEntity<String> response = restTemplate.exchange(
                    "https://api.github.com/user",
                    HttpMethod.GET,
                    request,
                    String.class
            );

            return response.getStatusCode() == HttpStatus.OK;
        } catch (Exception e) {
            return false;
        }
    }
}
