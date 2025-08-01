package com.Logisight.service.abstracts;

import java.util.List;
import java.util.Optional;

import com.Logisight.dto.create.UserCreateDto;
import com.Logisight.dto.response.UserResponseDto;
import com.Logisight.dto.update.UpdateUserDTO;

public interface UserService {
	List<UserResponseDto> getAllEnabledUsers();

    List<UserResponseDto> getAllUsers();

    Optional<UserResponseDto> getUserById(Long id);

    Optional<UserResponseDto> getUserByUsername(String username);

    Optional<UserResponseDto> getUserByEmail(String email);

    UserResponseDto createUser(UserCreateDto userCreateDTO);

    UserResponseDto updateUser(Long id, UpdateUserDTO userUpdateDTO);

    void deleteUser(Long id);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserResponseDto setUserEnabled(Long id, boolean enabled);

   
    // UserResponseDTO updatePassword(Long id, PasswordUpdateDTO passwordUpdateDTO);
}
