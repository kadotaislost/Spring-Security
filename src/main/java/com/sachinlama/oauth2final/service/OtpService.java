package com.sachinlama.oauth2final.service;

import com.sachinlama.oauth2final.model.OtpCode;
import com.sachinlama.oauth2final.repository.OtpRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Optional;

@Service
public class OtpService {

    @Autowired
    private OtpRepository otpRepository;

    @Autowired
    private JavaMailSender emailSender;

    private static final Logger logger = LoggerFactory.getLogger(OtpService.class);
    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRATION_MINUTES = 5;

    private String generateOtp() {
       // Generate a random OTP
        SecureRandom random = new SecureRandom();
        StringBuilder otp = new StringBuilder(OTP_LENGTH);
        for (int i = 0; i < OTP_LENGTH; i++) {
            otp.append(random.nextInt(10)); // Append a random digit (0-9)
        }
        return otp.toString();
    }

    private void saveOtp(String email, String otp) {
        OtpCode otpCode = new OtpCode(otp, email, OTP_EXPIRATION_MINUTES);
        otpRepository.save(otpCode);
    }

    private void sendOtpEmail(String email, String otp) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(email);
        message.setSubject("Your OTP Code SACHIN TEST APP");
        message.setText("Your OTP code is: " + otp+
                "\nThis code is valid for " + OTP_EXPIRATION_MINUTES + " minutes.");
        try{
            emailSender.send(message);
        }catch (Exception e){
            logger.error("Failed to send  otp to {}: {}", email, e.getMessage() );
            throw new RuntimeException(e);
        }

    }
    public void generateAndSendOtp(String email) {
        String otp = generateOtp();
        saveOtp(email, otp);
        sendOtpEmail(email, otp);
        logger.info("OTP sent to {} successfully", email);
    }


    public boolean validateOtp(String email, String code) {
        Optional<OtpCode> otpOptional = otpRepository.findByEmailAndCodeAndUsedFalse(email, code);

        if (otpOptional.isPresent()) {
            OtpCode otp = otpOptional.get();

            if (otp.isExpired()) {
                logger.warn("OTP expired for email: {}", email);
                return false;
            }

            // Mark the OTP as used
            otp.setUsed(true);
            otpRepository.save(otp);
            logger.info("OTP validated successfully for: {}", email);
            return true;
        }

        logger.warn("Invalid OTP attempt for email: {}", email);
        return false;
    }


}
