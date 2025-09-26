package com.github.sambhavmahajan.cloudstorageservice.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.Set;
import java.util.HashSet;

@Entity
@NoArgsConstructor
@Getter
@Setter
public class File {
    @Id
    @GeneratedValue
    private Long id;

    private String name;
    private String path;
    private boolean isPublic;
    private boolean doesExpireAfterDay;
    private LocalDateTime uploadedAt;
    private String ownerUsername;

    @ElementCollection(fetch = FetchType.EAGER)
    private Set<String> allowedUsers = new HashSet<>();

    public File(String name, String path, boolean isPublic, boolean doesExpireAfterDay) {
        this.name = name;
        this.path = path;
        this.isPublic = isPublic;
        this.doesExpireAfterDay = doesExpireAfterDay;
        this.uploadedAt = LocalDateTime.now();
    }
}