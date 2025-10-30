package com.github.sambhavmahajan.cloudstorageservice.repository;

import com.github.sambhavmahajan.cloudstorageservice.model.File;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface FileRepo extends JpaRepository<File, Long> {
}
