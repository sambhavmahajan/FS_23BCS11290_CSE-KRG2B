package com.github.sambhavmahajan.cloudstorageservice.repository;

import com.github.sambhavmahajan.cloudstorageservice.model.FileList;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FileListRepo extends JpaRepository<FileList, Long> {
}
