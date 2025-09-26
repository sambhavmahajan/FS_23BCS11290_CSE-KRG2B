package com.github.sambhavmahajan.cloudstorageservice.controllers;

import com.github.sambhavmahajan.cloudstorageservice.service.FileService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

@Controller
public class FileController {

    private final FileService fileService;

    public FileController(FileService fileService) {
        this.fileService = fileService;
    }

    @PostMapping("/upload")
    public String uploadFile(@RequestParam("file") MultipartFile file,
                             @RequestParam("visibility") String visibility,
                             @RequestParam(value = "expiresAfterDay", defaultValue = "false") boolean expiresAfterDay,
                             @RequestParam(value = "allowedUsers", required = false) String allowedUsers,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication) {
        try {
            String username = authentication.getName();
            boolean isPublic = "PUBLIC".equals(visibility);

            fileService.storeFile(file, username, isPublic, expiresAfterDay, allowedUsers);

            redirectAttributes.addFlashAttribute("success", "File uploaded successfully: " + file.getOriginalFilename());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to upload file: " + e.getMessage());
        }

        return "redirect:/home";
    }

    @GetMapping("/download/{fileId}")
    @ResponseBody
    public ResponseEntity<Resource> downloadFile(@PathVariable Long fileId, Authentication authentication) {
        try {
            System.out.println("Attempting to download file with ID: " + fileId + " for user: " + authentication.getName());

            com.github.sambhavmahajan.cloudstorageservice.model.File file = fileService.getFile(fileId, authentication.getName());
            System.out.println("File found: " + file.getName() + " at path: " + file.getPath());

            Path filePath = Paths.get(file.getPath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                System.out.println("Resource exists and is readable");

                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .body(resource);
            } else {
                System.err.println("Resource doesn't exist or is not readable");
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Error downloading file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/public/download/{fileId}")
    @ResponseBody
    public ResponseEntity<Resource> downloadPublicFile(@PathVariable Long fileId) {
        try {
            System.out.println("Attempting to download public file with ID: " + fileId);

            com.github.sambhavmahajan.cloudstorageservice.model.File file = fileService.getPublicFile(fileId);
            Path filePath = Paths.get(file.getPath());
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() || resource.isReadable()) {
                return ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_OCTET_STREAM)
                        .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + file.getName() + "\"")
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (MalformedURLException e) {
            System.err.println("Malformed URL: " + e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            System.err.println("Error downloading public file: " + e.getMessage());
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/delete/{fileId}")
    public String deleteFile(@PathVariable Long fileId,
                             RedirectAttributes redirectAttributes,
                             Authentication authentication) {
        try {
            fileService.deleteFile(fileId, authentication.getName());
            redirectAttributes.addFlashAttribute("success", "File deleted successfully");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Failed to delete file: " + e.getMessage());
        }
        return "redirect:/home";
    }

    @GetMapping("/shared")
    public String sharedFiles(Model model, Authentication authentication) {
        try {
            model.addAttribute("files", fileService.getSharedFiles(authentication.getName()));
        } catch (Exception e) {
            model.addAttribute("files", List.of());
        }
        return "shared";
    }
}