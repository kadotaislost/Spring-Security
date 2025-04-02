package com.sachinlama.oauth2final.controller;

import com.sachinlama.oauth2final.dto.ApiResponse;
import com.sachinlama.oauth2final.dto.RegistrationDto;
import com.sachinlama.oauth2final.model.User;
import com.sachinlama.oauth2final.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.hibernate.mapping.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;


@RestController
@RequestMapping("/api")
@Tag(name = "Register Endpoint", description = "create new user and add to database")
public class RegisterController {

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> register(@Valid @RequestBody RegistrationDto registrationDto) {
        try{
            User user = userService.registerUser(registrationDto);
            return ResponseEntity.ok(
                    new ApiResponse<>(
                            200,
                            "User registered successfully",
                            user.getId()
                    )
            );
        }catch (RuntimeException e){
            return ResponseEntity.badRequest().body(
                    new ApiResponse<>(
                            400,
                            "User registration failed "+ e.getMessage(),
                            null
                    )
            );
        }
    }

}
