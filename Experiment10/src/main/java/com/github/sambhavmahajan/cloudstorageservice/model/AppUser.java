package com.github.sambhavmahajan.cloudstorageservice.model;

import com.github.sambhavmahajan.cloudstorageservice.dto.RegisterDTO;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class AppUser implements UserDetails {

    @Id
    private String username;
    private String email;
    private String password;
    private String role = "USER";

    @OneToOne(cascade = CascadeType.ALL)
    private FileList fileList;

    public AppUser(String username, String email, String password) {
        this.username = username;
        this.email = email;
        this.password = password;
    }
    public AppUser(RegisterDTO registerDTO) {
        this.username = registerDTO.getUsername();
        this.email = registerDTO.getEmail();
        this.password = registerDTO.getPassword();
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of(() -> "ROLE_" + role);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }
}
