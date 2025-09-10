package com.Logisight.unit.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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
import com.Logisight.service.concretes.UserManager;

@ExtendWith(MockitoExtension.class)
public class UserManagerTest {
	@InjectMocks
    private UserManager userManager;

    @Mock
    private UserRepository userRepository;

    @Mock
    private UserMapper userMapper;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private RoleRepository roleRepository;

    private User user;
    private UserResponseDto responseDto;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setId(1L);
        user.setUsername("testuser");
        user.setEmail("test@example.com");

        responseDto = new UserResponseDto();
        responseDto.setId(user.getId());
        responseDto.setUsername(user.getUsername());
        responseDto.setEmail(user.getEmail());
    }

    @Test
    void createUser_success() {
        UserCreateDto dto = new UserCreateDto();
        dto.setUsername("testuser");
        dto.setEmail("test@example.com");
        dto.setPassword("password");

        Role defaultRole = new Role();
        defaultRole.setName("ROLE_USER");

        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(false);
        when(userRepository.existsByEmail(dto.getEmail())).thenReturn(false);
        when(userMapper.toEntity(dto)).thenReturn(user);
        when(passwordEncoder.encode(dto.getPassword())).thenReturn("hashedPassword");
        when(roleRepository.findByName("ROLE_USER")).thenReturn(Optional.of(defaultRole));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userManager.createUser(dto);

        assertNotNull(result);
        assertEquals("testuser", result.getUsername());
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void createUser_usernameExists() {
        UserCreateDto dto = new UserCreateDto();
        dto.setUsername("testuser");

        when(userRepository.existsByUsername(dto.getUsername())).thenReturn(true);

        BusinessException ex = assertThrows(BusinessException.class, () -> userManager.createUser(dto));
        assertEquals(ErrorCode.USERNAME_ALREADY_EXISTS, ex.getErrorCode());
    }

    @Test
    void updateUser_success() {
        UpdateUserDTO dto = new UpdateUserDTO();
        dto.setEmail("new@example.com");
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.updateUserFromDto(any(UpdateUserDTO.class), any(User.class)))
            .thenAnswer(invocation -> invocation.getArgument(1));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userManager.updateUser(1L, dto);

        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    
    }

    @Test
    void setUserEnabled_success() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userRepository.save(user)).thenReturn(user);
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        UserResponseDto result = userManager.setUserEnabled(1L, true);

        assertNotNull(result);
        verify(userRepository, times(1)).save(user);
    }

    @Test
    void getUserById_found() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(userMapper.toResponseDto(user)).thenReturn(responseDto);

        Optional<UserResponseDto> result = userManager.getUserById(1L);

        assertTrue(result.isPresent());
        assertEquals(user.getId(), result.get().getId());
    }

    @Test
    void deleteUser_success() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userManager.deleteUser(1L);

        verify(userRepository, times(1)).deleteById(1L);
    }

    @Test
    void deleteUser_notFound() {
        when(userRepository.existsById(2L)).thenReturn(false);

        BusinessException ex = assertThrows(BusinessException.class, () -> userManager.deleteUser(2L));
        assertEquals(ErrorCode.USER_NOT_FOUND, ex.getErrorCode());
    }

    @Test
    void existsByUsername_returnsTrue() {
        when(userRepository.existsByUsername("testuser")).thenReturn(true);
        assertTrue(userManager.existsByUsername("testuser"));
    }

    @Test
    void existsByEmail_returnsFalse() {
        when(userRepository.existsByEmail("other@example.com")).thenReturn(false);
        assertFalse(userManager.existsByEmail("other@example.com"));
    }
}
