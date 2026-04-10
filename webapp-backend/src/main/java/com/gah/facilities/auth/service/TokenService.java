package com.gah.facilities.auth.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gah.facilities.common.domain.user.UserAccount;
import com.gah.facilities.common.domain.user.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Base64;
import java.util.Optional;

@Service
public class TokenService {
    private final String secret;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public TokenService(@Value("${security.jwt.secret}") String secret) {
        this.secret = secret;
    }

    public String issueToken(UserAccount account) {
        String header = base64Url("{\"alg\":\"HS256\",\"typ\":\"JWT\"}");
        long exp = Instant.now().plusSeconds(3600).getEpochSecond();
        String payload = base64Url("{\"sub\":" + account.getId() + ",\"role\":\"" + account.getRole().name() + "\",\"exp\":" + exp + "}");
        String signature = sign(header + "." + payload);
        return header + "." + payload + "." + signature;
    }

    public Optional<TokenClaims> parseAndValidate(String token) {
        try {
            String[] parts = token.split("\\.");
            if (parts.length != 3) {
                return Optional.empty();
            }

            String content = parts[0] + "." + parts[1];
            String expectedSignature = sign(content);
            if (!MessageDigest.isEqual(
                    expectedSignature.getBytes(StandardCharsets.UTF_8),
                    parts[2].getBytes(StandardCharsets.UTF_8))) {
                return Optional.empty();
            }

            byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[1]);
            JsonNode payload = objectMapper.readTree(payloadBytes);

            long userId = payload.get("sub").asLong();
            String roleName = payload.get("role").asText();
            long exp = payload.get("exp").asLong();

            if (Instant.now().getEpochSecond() >= exp) {
                return Optional.empty();
            }

            return Optional.of(new TokenClaims(userId, UserRole.valueOf(roleName), exp));
        } catch (Exception ex) {
            return Optional.empty();
        }
    }

    private String sign(String content) {
        try {
            Mac hmac = Mac.getInstance("HmacSHA256");
            hmac.init(new SecretKeySpec(secret.getBytes(StandardCharsets.UTF_8), "HmacSHA256"));
            return Base64.getUrlEncoder().withoutPadding().encodeToString(hmac.doFinal(content.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            throw new IllegalStateException("Unable to create token", e);
        }
    }

    private String base64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    public record TokenClaims(long userId, UserRole role, long exp) {
    }
}
