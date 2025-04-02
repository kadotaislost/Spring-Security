package com.sachinlama.oauth2final.controller;

import com.sachinlama.oauth2final.dto.ApiResponse;
import com.sachinlama.oauth2final.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/otp")
public class OtpController {

    @Autowired
    private OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<?> generateOtp(@RequestParam String email) {
        try {
            otpService.generateAndSendOtp(email);
            return ResponseEntity.ok(new ApiResponse<>(200, "OTP sent successfully", null));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Error generating OTP: " + e.getMessage(), null));
        }
    }

    @PostMapping("/verify")
    public ResponseEntity<?> verifyOtp(@RequestParam String otp, HttpServletRequest request) {
        HttpSession session = request.getSession();
        String email = (String) session.getAttribute("PENDING_EMAIL");

        if (email == null) {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "No pending authentication", null));
        }

        boolean isValid = otpService.validateOtp(email, otp);

        if(isValid) {
            // Get the pending authentication from session
            Authentication authentication = (Authentication) session.getAttribute("PENDING_AUTHENTICATION");

            if (authentication != null) {
                // Set authentication in security context
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // Store the authenticated security context in the session
                session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

                // Clear pending authentication data
                session.removeAttribute("PENDING_AUTHENTICATION");
                session.removeAttribute("PENDING_EMAIL");

                return ResponseEntity.ok(new ApiResponse<>(200, "OTP verified successfully", null));
            } else {
                return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Authentication data not found", null));
            }
        } else {
            return ResponseEntity.badRequest().body(new ApiResponse<>(400, "Invalid OTP", null));
        }
    }
}