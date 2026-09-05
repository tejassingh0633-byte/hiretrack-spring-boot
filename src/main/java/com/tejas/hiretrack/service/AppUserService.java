package com.tejas.hiretrack.service;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.tejas.hiretrack.model.AppUser;
import com.tejas.hiretrack.repository.AppUserRepository;

@Service
public class AppUserService implements UserDetailsService {

    private final AppUserRepository repository;
    private final PasswordEncoder passwordEncoder;

    public AppUserService(
            AppUserRepository repository,
            PasswordEncoder passwordEncoder) {

        this.repository = repository;
        this.passwordEncoder = passwordEncoder;
    }

    public boolean emailExists(String email) {
        return repository.existsByEmailIgnoreCase(email);
    }

    public AppUser registerUser(AppUser appUser) {
        appUser.setName(appUser.getName().trim());
        appUser.setEmail(appUser.getEmail().trim().toLowerCase());
        appUser.setPassword(
                passwordEncoder.encode(appUser.getPassword()));
        appUser.setRole("ROLE_USER");

        return repository.save(appUser);
    }

    @Override
    public UserDetails loadUserByUsername(String email)
            throws UsernameNotFoundException {

        AppUser appUser = repository
                .findByEmailIgnoreCase(email)
                .orElseThrow(() ->
                        new UsernameNotFoundException(
                                "User not found"));

        return User.withUsername(appUser.getEmail())
                .password(appUser.getPassword())
                .authorities(appUser.getRole())
                .build();
    }
}