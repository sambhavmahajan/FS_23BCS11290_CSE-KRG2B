package com.github.sambhavmahajan.cloudstorageservice.repository;

import com.github.sambhavmahajan.cloudstorageservice.model.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AppUserRepo extends JpaRepository<AppUser, String> {
    Optional<AppUser> findByUsername(String s);

    Optional<AppUser> findByEmail(String email);
}
