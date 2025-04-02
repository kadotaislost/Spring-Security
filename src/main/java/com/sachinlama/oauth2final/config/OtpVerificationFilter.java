package com.sachinlama.oauth2final.config;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class OtpVerificationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {

        // Skip filter for public URLs
        String requestURI = request.getRequestURI();
        if (isPublicUrl(requestURI)) {
            chain.doFilter(request, response);
            return;
        }

        HttpSession session = request.getSession(false);
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();

        // Check if user is authenticated but OTP verification is pending
        if (session != null && session.getAttribute("PENDING_AUTHENTICATION") != null) {
            // Redirect to OTP verification page
            response.sendRedirect("/verify-otp");
            return;
        }

        chain.doFilter(request, response);
    }

    private boolean isPublicUrl(String url) {
        List<String> publicUrls = Arrays.asList("/", "/login", "/register", "/api/register",
                "/api/otp/verify", "/api/otp/generate", "/verify-otp");
        return publicUrls.stream().anyMatch(url::equals)
                || url.startsWith("/css/")
                || url.startsWith("/js/")
                || url.startsWith("/docs")
                || url.startsWith("/swagger-ui/")
                || url.startsWith("/v3/api-docs/");
    }
}