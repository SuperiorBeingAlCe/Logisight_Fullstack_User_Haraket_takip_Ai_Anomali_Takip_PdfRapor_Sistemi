package com.Logisight.service.concretes;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Optional;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.Logisight.dto.create.UserCreateDto;
import com.Logisight.dto.response.UserResponseDto;
import com.Logisight.dto.update.UpdateUserDTO;
import com.Logisight.entity.Role;
import com.Logisight.entity.User;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.UserMapper;
import com.Logisight.repository.RoleRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.abstracts.UserService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class UserManager implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    
    @Override
    public UserResponseDto createUser(UserCreateDto createUserDTO) {
        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = userMapper.toEntity(createUserDTO);
        
        String hashedPassword = passwordEncoder.encode(createUserDTO.getPassword());
        user.setPasswordHash(hashedPassword);
        
        Role defaultRole = roleRepository.findByName("ROLE_USER")
                .orElseThrow(() -> new RuntimeException("Default role ROLE_USER bulunamadı"));
            user.setRoles(Collections.singleton(defaultRole));
        User saved;
        try {
            saved = userRepository.save(user);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.USER_CREATION_FAILED, ex.getMessage());
        }
        return userMapper.toResponseDto(saved);
    }
    
    @Override
    public UserResponseDto updateUser(Long id, UpdateUserDTO updateUserDTO) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        userMapper.updateUserFromDto(updateUserDTO, user);
        user.setUpdatedAt(LocalDateTime.now());
        
        User updated;
        try {
            updated = userRepository.save(user);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.USER_UPDATE_FAILED, ex.getMessage());
        }
        return userMapper.toResponseDto(updated);
    }
    
    @Override
    public UserResponseDto setUserEnabled(Long id, boolean enabled) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
        
        user.setEnabled(enabled);
        user.setUpdatedAt(LocalDateTime.now());
        
        User updated;
        try {
            updated = userRepository.save(user);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.USER_UPDATE_FAILED, ex.getMessage());
        }
        return userMapper.toResponseDto(updated);
    }

    @Override
    @Cacheable(value = "enabledUsersPage", key = "#page + '-' + #size")
    public Page<UserResponseDto> getEnabledUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findAllByEnabledTrue(pageable);
        return usersPage.map(userMapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "allUsersPage", key = "#page + '-' + #size")
    public Page<UserResponseDto> getAllUsers(int page, int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<User> usersPage = userRepository.findAll(pageable);
        return usersPage.map(userMapper::toResponseDto);
    }

    @Override
    @Cacheable(value = "users", key = "#id")
    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id)
            .map(userMapper::toResponseDto)
            .or(() -> { throw new BusinessException(ErrorCode.USER_NOT_FOUND); });
    }

    @Override
    @Cacheable(value = "usersByUsername", key = "#username")
    public Optional<UserResponseDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
            .map(userMapper::toResponseDto)
            .map(Optional::of)
            .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    @Cacheable(value = "usersByEmail", key = "#email")
    public Optional<UserResponseDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
            .map(userMapper::toResponseDto)
            .or(() -> { throw new BusinessException(ErrorCode.USER_NOT_FOUND); });
    }

    @Override
    @CacheEvict(value = {
        "users", "usersByUsername", "usersByEmail", "enabledUsersPage", "allUsersPage"
    }, key = "#id", allEntries = false) // Sadece ilgili id bazlı cache temizlenmeli, tümünü değil
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        try {
            userRepository.deleteById(id);
        } catch (DataAccessException ex) {
            throw new BusinessException(ErrorCode.USER_DELETION_FAILED);
        }
    }

    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }
}
