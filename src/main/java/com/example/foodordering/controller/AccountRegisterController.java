package com.example.foodordering.controller;

import com.example.foodordering.dto.request.AccountRegistrationRequest;
import com.example.foodordering.dto.response.AccountResponse;
import com.example.foodordering.service.AccountRegistrationService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
@RequiredArgsConstructor
public class AccountRegisterController {
    @Autowired
    AccountRegistrationService accountService;

    @GetMapping("/registers")
    public String showRegistrationForm(Model model) {
        model.addAttribute("accountRegistrationRequest", new AccountRegistrationRequest());
        return "register";
    }

    @PostMapping("/registers")
    public String createAccount(@Valid @ModelAttribute AccountRegistrationRequest request, BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            return "register"; // Return to registration form with validation errors
        }

        AccountResponse accountResponse = accountService.createAccount(request);

        if (accountResponse != null) {
            // Successful registration logic here
            return "redirect:/login"; // Example redirect to login page after successful registration
        } else {
            // Failed registration logic here
            model.addAttribute("error", "Registration failed");
            return "register"; // Return to registration form with error message
        }
    }
}
