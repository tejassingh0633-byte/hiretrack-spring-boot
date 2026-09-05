package com.tejas.hiretrack.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import com.tejas.hiretrack.model.AppUser;
import com.tejas.hiretrack.service.AppUserService;

import jakarta.validation.Valid;

@Controller
public class AuthController {

    private final AppUserService userService;

    public AuthController(AppUserService userService) {
        this.userService = userService;
    }

    @GetMapping("/login")
    public String showLoginPage() {
        return "login";
    }

    @GetMapping("/register")
    public String showRegistrationPage(Model model) {
        model.addAttribute("user", new AppUser());
        return "register";
    }

    @PostMapping("/register")
    public String registerUser(
            @Valid @ModelAttribute("user") AppUser appUser,
            BindingResult result) {

        if (appUser.getEmail() != null
                && !appUser.getEmail().isBlank()
                && userService.emailExists(appUser.getEmail())) {

            result.rejectValue(
                    "email",
                    "duplicate",
                    "An account already exists with this email");
        }

        if (result.hasErrors()) {
            return "register";
        }

        userService.registerUser(appUser);
        return "redirect:/login?registered";
    }
}