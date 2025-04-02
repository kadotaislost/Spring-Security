package com.sachinlama.oauth2final.repository;

import com.sachinlama.oauth2final.model.OtpCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OtpRepository extends JpaRepository<OtpCode, Long> {
    // Find the most recent OTP for a given email that hasn't been used yet
    Optional<OtpCode> findTopByEmailOrderByCreatedAtDesc(String email);

    // Find a specific OTP code for validation
    Optional<OtpCode> findByEmailAndCodeAndUsedFalse(String email, String code);
}
