package com.github.sambhavmahajan.cloudstorageservice.controllers;

import com.github.sambhavmahajan.cloudstorageservice.dto.RegisterDTO;
import com.github.sambhavmahajan.cloudstorageservice.service.AppUserService;
import org.apache.catalina.User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class UserController {
    private final AppUserService appUserService;
    public UserController(AppUserService appUserService) {
        this.appUserService = appUserService;
    }
    @GetMapping("/register")
    public String getRegister(Model model) {
        RegisterDTO  registerDTO = new RegisterDTO();
        model.addAttribute("registerDTO", registerDTO);
        return "register";
    }
    @PostMapping("/register")
    public String postRegister(Model model, @ModelAttribute RegisterDTO registerDTO) {
        boolean registered = false;
        try {
            registered = appUserService.registerUser(registerDTO);
        } catch (Exception ex) {
            model.addAttribute("error", ex.getMessage());
        }
        if(registered) {
            model.addAttribute("success", registerDTO.getUsername() + " registered successfully");
        }
        return "register";
    }
    @GetMapping("/login")
    public String login() {
        return "login";
    }
}
