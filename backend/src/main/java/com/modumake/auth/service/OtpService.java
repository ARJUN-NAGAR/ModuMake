package com.modumake.auth.service;

import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.security.SecureRandom;

@Service
public class OtpService {

    private final StringRedisTemplate redisTemplate;
    private final SecureRandom secureRandom = new SecureRandom();

    public OtpService(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public String generateAndStoreOtp(String email) {
        // Generate 6-digit OTP
        int otpNumeric = 100000 + secureRandom.nextInt(900000);
        String otp = String.valueOf(otpNumeric);
        
        // Store in Redis with 5 minute expiry
        redisTemplate.opsForValue().set("otp:" + email, otp, Duration.ofMinutes(5));
        
        return otp; // In a real app we would email/SMS this, returning it for local logging
    }

    public boolean verifyOtp(String email, String inputOtp) {
        String storedOtp = redisTemplate.opsForValue().get("otp:" + email);
        if (storedOtp != null && storedOtp.equals(inputOtp)) {
            // Delete upon successful verification to prevent reuse
            redisTemplate.delete("otp:" + email);
            return true;
        }
        return false;
    }
}
