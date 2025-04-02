package com.sachinlama.oauth2final.controller;

import com.sachinlama.oauth2final.model.User;
import com.sachinlama.oauth2final.model.UserPrincipal;
import com.sachinlama.oauth2final.repository.UserRepository;
import com.sachinlama.oauth2final.service.DogFactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import javax.servlet.http.HttpSession;

@Controller
public class ViewController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private DogFactService dogFactService;

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @GetMapping("/")
    public String home() {
        return "index";
    }

    @GetMapping("/register")
    public String registerForm() {
        return "register";
    }

    @GetMapping("/welcome")
    public String welcome(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        String name = "";
        String email = "";

        if(auth.getPrincipal() instanceof OAuth2User) {
            OAuth2User oauth2User = (OAuth2User) auth.getPrincipal();
            name = oauth2User.getAttribute("name");
            email = oauth2User.getAttribute("email");
        } else if(auth.getPrincipal() instanceof UserPrincipal) {
            UserPrincipal userPrincipal = (UserPrincipal) auth.getPrincipal();
            User user = userRepository.findByEmail(userPrincipal.getUsername());
            name = user.getFullName();
            email = userPrincipal.getUsername();
        }

        model.addAttribute("name", name);
        model.addAttribute("email", email);

        // Add dog fact to the model
        String dogFact = dogFactService.getRandomDogFact();
        model.addAttribute("dogFact", dogFact);
        return "welcome";
    }

    @GetMapping("/verify-otp")
    public String verifyOtp(Model model, HttpSession session) {
        // Get the pending email from session
        String email = (String) session.getAttribute("PENDING_EMAIL");

        if (email == null) {
            // No pending authentication, redirect to login
            return "redirect:/login";
        }

        model.addAttribute("email", email);
        return "verify-otp";
    }
}