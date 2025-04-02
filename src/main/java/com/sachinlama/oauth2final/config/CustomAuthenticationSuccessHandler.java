package com.sachinlama.oauth2final.config;

import com.sachinlama.oauth2final.service.OtpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@Component
public class CustomAuthenticationSuccessHandler extends SavedRequestAwareAuthenticationSuccessHandler {

    @Autowired
    private OtpService otpService;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        // Get the authenticated user's email
        String email = authentication.getName();

        System.out.println("Authentication successful for user: " + email);

        // Generate and send OTP
        try {
            otpService.generateAndSendOtp(email);
            System.out.println("OTP sent to: " + email);
        } catch (Exception e) {
            System.err.println("Failed to send OTP: " + e.getMessage());
        }

        // Store authentication in session for later use after OTP verification
        HttpSession session = request.getSession();
        session.setAttribute("PENDING_AUTHENTICATION", authentication);
        session.setAttribute("PENDING_EMAIL", email);

        // Clear security context - user is not fully authenticated yet
        session.setAttribute("SPRING_SECURITY_CONTEXT", null);

        // Redirect to OTP verification page
        getRedirectStrategy().sendRedirect(request, response, "/verify-otp");
    }
}