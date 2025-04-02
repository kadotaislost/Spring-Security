package com.sachinlama.oauth2final.service;

import com.sachinlama.oauth2final.dto.RegistrationDto;
import com.sachinlama.oauth2final.model.User;
import com.sachinlama.oauth2final.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BCryptPasswordEncoder passwordEncoder;


    public User registerUser(RegistrationDto registrationDto) {
        if(userRepository.findByEmail(registrationDto.getEmail()) != null){
            throw new RuntimeException("User with that email already exists");
        }

        User user = new User();
        user.setEmail(registrationDto.getEmail());
        user.setPassword(passwordEncoder.encode(registrationDto.getPassword()));
        user.setFullName(registrationDto.getFullName());

        return userRepository.save(user);
    }
}
