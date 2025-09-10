package com.Logisight.service.abstracts;

import java.util.Optional;

import org.springframework.data.domain.Page;

import com.Logisight.dto.create.UserCreateDto;
import com.Logisight.dto.response.UserResponseDto;
import com.Logisight.dto.update.UpdateUserDTO;

public interface UserService {
	
	UserResponseDto setUserEnabled(Long id, boolean enabled);
	
	Page<UserResponseDto> getEnabledUsers(int page, int size);

    // Sayfalı tüm kullanıcı listesi
    Page<UserResponseDto> getAllUsers(int page, int size);

    Optional<UserResponseDto> getUserById(Long id);

    Optional<UserResponseDto> getUserByUsername(String username);

    Optional<UserResponseDto> getUserByEmail(String email);

    UserResponseDto createUser(UserCreateDto userCreateDTO);

    UserResponseDto updateUser(Long id, UpdateUserDTO userUpdateDTO);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);
}
