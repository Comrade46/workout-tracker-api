package com.workouttracker.security;

import com.workouttracker.model.User;
import com.fasterxml.jackson.annotation.JsonIgnore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.ZoneId;
import java.util.Collection;
import java.util.Collections;

public class UserDetailsImpl implements UserDetails {
    
    private Long id;
    private String username;
    private String email;
    
    @JsonIgnore
    private String password;

    // Tokens issued before this (epoch seconds) are rejected; 0 = none.
    private long tokensValidFromEpochSecond;

    public UserDetailsImpl(Long id, String username, String email, String password) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.password = password;
    }

    public static UserDetailsImpl build(User user) {
        UserDetailsImpl details = new UserDetailsImpl(
                user.getId(),
                user.getUsername(),
                user.getEmail(),
                user.getPassword());

        if (user.getPasswordChangedAt() != null) {
            details.tokensValidFromEpochSecond = user.getPasswordChangedAt()
                    .atZone(ZoneId.systemDefault())
                    .toEpochSecond();
        }

        return details;
    }

    public Long getId() { return id; }
    public String getEmail() { return email; }
    public long getTokensValidFromEpochSecond() { return tokensValidFromEpochSecond; }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Collections.emptyList(); // We aren't using roles (Admin/User) yet
    }

    @Override
    public String getPassword() { return password; }

    @Override
    public String getUsername() { return username; }

    @Override
    public boolean isAccountNonExpired() { return true; }

    @Override
    public boolean isAccountNonLocked() { return true; }

    @Override
    public boolean isCredentialsNonExpired() { return true; }

    @Override
    public boolean isEnabled() { return true; }
}
