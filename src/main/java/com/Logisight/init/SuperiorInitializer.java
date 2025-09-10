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
public class SuperiorInitializer implements ApplicationRunner{
	 private final UserRepository userRepository;
	    private final RoleRepository roleRepository;
	    private final UserMapper userMapper;
	    private final PasswordEncoder passwordEncoder;

	    public SuperiorInitializer(UserRepository userRepository,
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
	        String defaultUsername = "superior";
	        String defaultEmail = "superior@example.com";

	        if (userRepository.existsByUsername(defaultUsername) || userRepository.existsByEmail(defaultEmail)) {
	            System.out.println("Superior user already exists. Skipping creation.");
	            return;
	        }

	        Optional<Role> superiorRoleOpt = roleRepository.findByName("ROLE_SUPERIOR");

	        if (superiorRoleOpt.isEmpty()) {
	            throw new RuntimeException("SUPERIOR role not found in database. Please create it manually.");
	        }

	        Role superiorRole = superiorRoleOpt.get();

	        UserCreateDto superiorDto = new UserCreateDto();
	        superiorDto.setUsername(defaultUsername);
	        superiorDto.setEmail(defaultEmail);
	        superiorDto.setPassword("Superior123!");

	        User superiorEntity = userMapper.toEntity(superiorDto);

	        superiorEntity.setPasswordHash(passwordEncoder.encode(superiorDto.getPassword()));

	        // Many-to-Many: rol set'ine ekleniyor
	        superiorEntity.getRoles().add(superiorRole);

	        userRepository.save(superiorEntity);
	        System.out.println("Superior user created successfully.");
	    }
	
}
