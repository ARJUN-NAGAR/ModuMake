package com.modumake.auth.service;

import com.modumake.auth.dto.AuthResponse;
import com.modumake.auth.dto.LoginRequest;
import com.modumake.auth.dto.SignupRequest;
import com.modumake.model.User;
import com.modumake.repository.UserRepository;
import com.modumake.security.JwtUtil;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final OtpService otpService;
    private final JwtUtil jwtUtil;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, OtpService otpService, JwtUtil jwtUtil) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.otpService = otpService;
        this.jwtUtil = jwtUtil;
    }

    public AuthResponse signup(SignupRequest request) {
        if (userRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new RuntimeException("Email already explicitly registered");
        }

        User user = User.builder()
                .name(request.getName())
                .email(request.getEmail())
                .phoneNumber(request.getPhoneNumber())
                .passwordHash(passwordEncoder.encode(request.getPassword()))
                .role(User.Role.CUSTOMER)
                // We default to true in MVP if we are bypassing actual email dispatch
                // but conceptually should be false until OTP verified.
                .build();
        
        userRepository.save(user);
        
        // Generate OTP
        String otp = otpService.generateAndStoreOtp(user.getEmail());
        System.out.println("====== DEV ONLY: OTP for " + user.getEmail() + " is: " + otp + " ======");

        return AuthResponse.builder()
                .message("Signup successful! Please check your email for the OTP.")
                .verified(false)
                .build();
    }

    public AuthResponse verifyOtp(String email, String otp) {
        if (otpService.verifyOtp(email, otp)) {
            Optional<User> optUser = userRepository.findByEmail(email);
            if (optUser.isPresent()) {
                // Issue JWT upon verify
                String token = jwtUtil.generateToken(email);
                return AuthResponse.builder()
                        .accessToken(token)
                        .message("Verification successful")
                        .verified(true)
                        .build();
            }
        }
        throw new RuntimeException("Invalid or expired OTP");
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (!passwordEncoder.matches(request.getPassword(), user.getPasswordHash())) {
            throw new RuntimeException("Invalid credentials");
        }

        String token = jwtUtil.generateToken(user.getEmail());
        return AuthResponse.builder()
                .accessToken(token)
                .message("Login successful")
                .verified(true)
                .build();
    }
}
