package com.sachinlama.oauth2final.service;

import com.sachinlama.oauth2final.model.User;
import com.sachinlama.oauth2final.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.UUID;

@Service
public class CustomOAuth2UserService extends DefaultOAuth2UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2User oAuth2User = super.loadUser(userRequest);

        // Debug the structure of the OAuth2User
        System.out.println("OAuth2User attributes: " + oAuth2User.getAttributes());

        // Extract email attribute correctly (Google specific)
        String email = oAuth2User.getAttribute("email");

        // Extract name attribute correctly (Google specific)
        String name = oAuth2User.getAttribute("name");

        // If the email is null, try to find it in a different location
        if (email == null && oAuth2User.getAttributes().containsKey("sub")) {
            // Google puts the email in the attributes map
            Map<String, Object> attributes = oAuth2User.getAttributes();
            email = (String) attributes.get("email");
            name = (String) attributes.get("name");
        }

        // Check if we successfully got the email
        if (email != null) {
            // Check if user exists in database
            User user = userRepository.findByEmail(email);

            // If user doesn't exist, create a new one
            if (user == null) {
                user = new User();
                user.setEmail(email);
                user.setFullName(name != null ? name : "Google User");
                // Generate a random password for OAuth users
                user.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
                userRepository.save(user);
                System.out.println("New OAuth2 user saved: " + email);
            } else {
                System.out.println("Existing OAuth2 user found: " + email);
            }
        } else {
            System.out.println("Could not extract email from OAuth2User: " + oAuth2User.getAttributes());
        }

        return oAuth2User;
    }
}