package com.github.sambhavmahajan.cloudstorageservice.controllers;

import com.github.sambhavmahajan.cloudstorageservice.service.FileService;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final FileService fileService;

    public HomeController(FileService fileService) {
        this.fileService = fileService;
    }

    @GetMapping({"/", "/home"})
    public String home(Model model, Authentication authentication) {
        try {
            model.addAttribute("files", fileService.getUserFiles(authentication.getName()));
        } catch (Exception e) {
            model.addAttribute("files", List.of());
        }
        return "home";
    }
}