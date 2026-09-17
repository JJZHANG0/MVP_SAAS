package com.toolfix.service;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.codec.digest.HmacAlgorithms;
import org.apache.commons.codec.digest.HmacUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.UUID;

@Service
@Slf4j
public class SecurityService {
    
    @Value("${toolfix.security.hmac-secret}")
    private String hmacSecret;
    
    @Value("${toolfix.security.link-expiry-days}")
    private int linkExpiryDays;
    
    public String generateSessionUuid() {
        return UUID.randomUUID().toString();
    }
    
    public LocalDateTime calculateExpiryTime() {
        return LocalDateTime.now().plusDays(linkExpiryDays);
    }
    
    public String generateHmacToken(String sessionUuid, LocalDateTime expiryTime) {
        long expiryTimestamp = expiryTime.toEpochSecond(ZoneOffset.UTC);
        String payload = sessionUuid + ":" + expiryTimestamp;
        
        HmacUtils hmacUtils = new HmacUtils(HmacAlgorithms.HMAC_SHA_256, hmacSecret.getBytes(StandardCharsets.UTF_8));
        return hmacUtils.hmacHex(payload);
    }
    
    public boolean validateHmacToken(String sessionUuid, LocalDateTime expiryTime, String token) {
        String expectedToken = generateHmacToken(sessionUuid, expiryTime);
        return expectedToken.equals(token);
    }
    
    public boolean isExpired(LocalDateTime expiryTime) {
        return LocalDateTime.now().isAfter(expiryTime);
    }
    
    public String buildSecureLink(String baseUrl, String sessionUuid, String token) {
        return String.format("%s/diagnosis/%s?token=%s", baseUrl, sessionUuid, token);
    }
}
