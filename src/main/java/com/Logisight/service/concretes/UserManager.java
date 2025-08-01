package com.Logisight.service.concretes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.Logisight.dto.create.UserCreateDto;
import com.Logisight.dto.response.UserResponseDto;
import com.Logisight.dto.update.UpdateUserDTO;
import com.Logisight.entity.User;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.mapper.UserMapper;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.abstracts.UserService;

import lombok.RequiredArgsConstructor;
@Service
@RequiredArgsConstructor
public class UserManager implements UserService {
    
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    
    @Override
    public UserResponseDto createUser(UserCreateDto createUserDTO) {
        if (userRepository.existsByUsername(createUserDTO.getUsername())) {
            throw new BusinessException(ErrorCode.USERNAME_ALREADY_EXISTS);
        }
        if (userRepository.existsByEmail(createUserDTO.getEmail())) {
            throw new BusinessException(ErrorCode.EMAIL_ALREADY_EXISTS);
        }

        User user = userMapper.toEntity(createUserDTO);
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
    public List<UserResponseDto> getAllEnabledUsers() {
        return userRepository.findAllByEnabledTrue()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public List<UserResponseDto> getAllUsers() {
        return userRepository.findAll()
                .stream()
                .map(userMapper::toResponseDto)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<UserResponseDto> getUserById(Long id) {
        return userRepository.findById(id)
                .map(userMapper::toResponseDto)
                .or(() -> { throw new BusinessException(ErrorCode.USER_NOT_FOUND); });
    }

    @Override
    public Optional<UserResponseDto> getUserByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toResponseDto)
                .map(Optional::of) 
                .orElseThrow(() -> new BusinessException(ErrorCode.USER_NOT_FOUND));
    }

    @Override
    public Optional<UserResponseDto> getUserByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toResponseDto)
                .or(() -> { throw new BusinessException(ErrorCode.USER_NOT_FOUND); });
    }

    @Override
    public void deleteUser(Long id) {
        if (!userRepository.existsById(id)) {
            throw new BusinessException(ErrorCode.USER_NOT_FOUND);
        }
        try {
            userRepository.deleteById(id);
        } catch (Exception ex) {
            throw new BusinessException(ErrorCode.USER_DELETION_FAILED, ex.getMessage());
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
