package com.example.demo.config;

import com.example.demo.entity.UserEntity;
import com.example.demo.repository.UserRepo;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepo userRepo;
    private final PasswordEncoder encoder;

    @Override
    public void run(String... args) {
        if (userRepo.findByUsername("admin").isEmpty()) {
            userRepo.save(UserEntity.builder()
                    .username("admin")
                    .email("admin@example.com")   // ✅ thêm email
                    .password(encoder.encode("123"))
                    .role("ROLE_ADMIN")
                    .enabled(true).build());
        }
        if (userRepo.findByUsername("user").isEmpty()) {
            userRepo.save(UserEntity.builder()
                    .username("user")
                    .email("user@example.com")    // ✅ thêm email
                    .password(encoder.encode("123"))
                    .role("ROLE_USER")
                    .enabled(true).build());
        }
    }

}
