package com.github.sambhavmahajan.cloudstorageservice.service;

import com.github.sambhavmahajan.cloudstorageservice.exceptions.UserNotFoundException;
import com.github.sambhavmahajan.cloudstorageservice.model.AppUser;
import com.github.sambhavmahajan.cloudstorageservice.model.File;
import com.github.sambhavmahajan.cloudstorageservice.model.FileList;
import com.github.sambhavmahajan.cloudstorageservice.repository.AppUserRepo;
import com.github.sambhavmahajan.cloudstorageservice.repository.FileListRepo;
import com.github.sambhavmahajan.cloudstorageservice.repository.FileRepo;
import jakarta.annotation.PostConstruct;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@EnableScheduling
public class FileService {

    private final FileRepo fileRepo;
    private final AppUserRepo appUserRepo;
    private final FileListRepo fileListRepo;

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Value("${file.max-size-mb:10}")
    private long maxFileSizeMB;

    public FileService(FileRepo fileRepo, AppUserRepo appUserRepo, FileListRepo fileListRepo) {
        this.fileRepo = fileRepo;
        this.appUserRepo = appUserRepo;
        this.fileListRepo = fileListRepo;
    }

    @PostConstruct
    public void init() {
        try {
            Files.createDirectories(Paths.get(uploadDir));
        } catch (IOException e) {
            throw new RuntimeException("Could not create upload directory", e);
        }
    }

    @Transactional
    public File storeFile(MultipartFile file, String username, boolean isPublic,
                          boolean expiresAfterDay, String allowedUsers) throws IOException, UserNotFoundException {
        long fileSizeInMB = file.getSize() / (1024 * 1024);
        if (fileSizeInMB > maxFileSizeMB) {
            throw new RuntimeException("File size exceeds maximum allowed size of " + maxFileSizeMB + " MB");
        }

        AppUser user = appUserRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        String filename = UUID.randomUUID().toString() + "_" + file.getOriginalFilename();
        Path targetLocation = Paths.get(uploadDir).resolve(filename);

        Files.copy(file.getInputStream(), targetLocation);

        File fileEntity = new File(file.getOriginalFilename(), targetLocation.toString(), isPublic, expiresAfterDay);
        fileEntity.setOwnerUsername(username);
        if (allowedUsers != null && !allowedUsers.trim().isEmpty()) {
            Set<String> allowedUserSet = Arrays.stream(allowedUsers.split(","))
                    .map(String::trim)
                    .filter(s -> !s.isEmpty())
                    .collect(Collectors.toSet());
            fileEntity.setAllowedUsers(allowedUserSet);
        }

        if (user.getFileList() == null) {
            FileList fileList = new FileList();
            user.setFileList(fileList);
        }
        fileEntity = fileRepo.save(fileEntity);
        user.getFileList().getFiles().add(fileEntity);
        appUserRepo.save(user);

        return fileEntity;
    }

    public File getFile(Long fileId, String username) throws UserNotFoundException {
        File file = fileRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        if (!hasAccess(file, username)) {
            throw new RuntimeException("Access denied");
        }
        if (isExpired(file)) {
            throw new RuntimeException("File has expired");
        }

        return file;
    }

    public File getPublicFile(Long fileId) {
        File file = fileRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));

        if (!file.isPublic()) {
            throw new RuntimeException("File is not public");
        }

        if (isExpired(file)) {
            throw new RuntimeException("File has expired");
        }

        return file;
    }

    private boolean hasAccess(File file, String username) {
        if (file.isPublic()) return true;
        if (username.equals(file.getOwnerUsername())) return true;
        return file.getAllowedUsers().contains(username);
    }

    private boolean isExpired(File file) {
        if (!file.isDoesExpireAfterDay()) return false;

        LocalDateTime expiryTime = file.getUploadedAt().plusDays(1);
        return LocalDateTime.now().isAfter(expiryTime);
    }

    @Transactional
    public void deleteFile(Long fileId, String username) throws UserNotFoundException, IOException {
        File file = fileRepo.findById(fileId)
                .orElseThrow(() -> new RuntimeException("File not found"));
        if (!username.equals(file.getOwnerUsername())) {
            throw new RuntimeException("Access denied - only the owner can delete this file");
        }
        Files.deleteIfExists(Paths.get(file.getPath()));
        AppUser user = appUserRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (user.getFileList() != null) {
            user.getFileList().getFiles().remove(file);
        }
        fileRepo.delete(file);
        appUserRepo.save(user);
    }

    public List<File> getUserFiles(String username) throws UserNotFoundException {
        AppUser user = appUserRepo.findByUsername(username)
                .orElseThrow(() -> new UserNotFoundException(username));

        if (user.getFileList() == null) {
            return List.of();
        }
        return user.getFileList().getFiles().stream()
                .filter(file -> !isExpired(file))
                .collect(Collectors.toList());
    }

    public List<File> getSharedFiles(String username) {
        return fileRepo.findAll().stream()
                .filter(file -> file.getAllowedUsers().contains(username))
                .filter(file -> !isExpired(file))
                .collect(Collectors.toList());
    }

    @Scheduled(fixedRate = 3600000)
    @Transactional
    public void deleteExpiredFiles() {
        List<File> allFiles = fileRepo.findAll();
        for (File file : allFiles) {
            if (isExpired(file)) {
                try {
                    Files.deleteIfExists(Paths.get(file.getPath()));
                    List<AppUser> users = appUserRepo.findAll();
                    for (AppUser user : users) {
                        if (user.getFileList() != null) {
                            user.getFileList().getFiles().remove(file);
                            appUserRepo.save(user);
                        }
                    }
                    fileRepo.delete(file);
                } catch (IOException ex) {
                    System.out.println(ex.getMessage());
                }
            }
        }
    }
}