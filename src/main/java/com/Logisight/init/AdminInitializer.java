package com.Logisight.init;

import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import com.Logisight.dto.create.UserCreateDto;
import com.Logisight.entity.Role;
import com.Logisight.entity.User;
import com.Logisight.mapper.UserMapper;
import com.Logisight.repository.RoleRepository;
import com.Logisight.repository.UserRepository;

@Component
public class AdminInitializer implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    public AdminInitializer(UserRepository userRepository,
                            RoleRepository roleRepository,
                            UserMapper userMapper,
                            PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
        this.userMapper = userMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        String defaultUsername = "admin";
        String defaultEmail = "admin@example.com";

        if (userRepository.existsByUsername(defaultUsername) || userRepository.existsByEmail(defaultEmail)) {
            System.out.println("Admin user already exists. Skipping creation.");
            return;
        }

        Optional<Role> adminRoleOpt = roleRepository.findByName("ROLE_ADMIN");

        if (adminRoleOpt.isEmpty()) {
            throw new RuntimeException("ADMIN role not found in database. Please create it manually.");
        }

        Role adminRole = adminRoleOpt.get();

        UserCreateDto adminDto = new UserCreateDto();
        adminDto.setUsername(defaultUsername);
        adminDto.setEmail(defaultEmail);
        adminDto.setPassword("Admin123!");

        User adminEntity = userMapper.toEntity(adminDto);

        adminEntity.setPasswordHash(passwordEncoder.encode(adminDto.getPassword()));

        // Many-to-Many: rol set'ine ekleniyor
        adminEntity.getRoles().add(adminRole);

        userRepository.save(adminEntity);
        System.out.println("Admin user created successfully.");
    }
}
