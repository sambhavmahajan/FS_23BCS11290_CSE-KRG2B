package com.github.sambhavmahajan.cloudstorageservice.service;

import com.github.sambhavmahajan.cloudstorageservice.dto.RegisterDTO;
import com.github.sambhavmahajan.cloudstorageservice.exceptions.EmailAlreadyExistsException;
import com.github.sambhavmahajan.cloudstorageservice.exceptions.UserNotFoundException;
import com.github.sambhavmahajan.cloudstorageservice.exceptions.UsernameAlreadyExistsException;
import com.github.sambhavmahajan.cloudstorageservice.model.AppUser;
import com.github.sambhavmahajan.cloudstorageservice.model.FileList;
import com.github.sambhavmahajan.cloudstorageservice.repository.AppUserRepo;
import com.github.sambhavmahajan.cloudstorageservice.repository.FileListRepo;
import jakarta.transaction.Transactional;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AppUserService implements UserDetailsService {
    private final AppUserRepo appUserRepo;
    private final PasswordEncoder passwordEncoder;
    private final FileListRepo fileListRepo;

    public AppUserService(AppUserRepo appUserRepo, PasswordEncoder passwordEncoder, FileListRepo fileListRepo) {
        this.appUserRepo = appUserRepo;
        this.passwordEncoder = passwordEncoder;
        this.fileListRepo = fileListRepo;
    }

    public Optional<AppUser> findByUsername(String username) throws UserNotFoundException {
        Optional<AppUser> usr = appUserRepo.findByUsername(username);
        if(!usr.isPresent()) throw new UserNotFoundException(username);
        return usr;
    }

    @Transactional
    public boolean registerUser(RegisterDTO registerDTO) throws UsernameAlreadyExistsException, EmailAlreadyExistsException {
        Optional<AppUser> usr = appUserRepo.findByUsername(registerDTO.getUsername());
        if(usr.isPresent()) throw new UsernameAlreadyExistsException(registerDTO.getUsername());
        usr = appUserRepo.findByEmail(registerDTO.getEmail());
        if(usr.isPresent()) throw new EmailAlreadyExistsException(registerDTO.getEmail());
        registerDTO.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        AppUser user = new AppUser(registerDTO);
        FileList fileList = new FileList();
        fileListRepo.save(fileList);
        user.setFileList(fileList);
        appUserRepo.save(user);
        return true;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        AppUser user = appUserRepo.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));
        return user;
    }
}