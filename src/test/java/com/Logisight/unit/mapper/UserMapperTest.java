package com.Logisight.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import java.time.LocalDateTime;
import java.util.Set;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.Logisight.dto.create.UserCreateDto;
import com.Logisight.dto.response.UserResponseDto;
import com.Logisight.dto.update.UpdateUserDTO;
import com.Logisight.entity.Role;
import com.Logisight.entity.User;
import com.Logisight.mapper.UserMapper;

public class UserMapperTest {
	
	private UserMapper userMapper;
	
	  @BeforeEach
	    void setUp() {
	        userMapper = Mappers.getMapper(UserMapper.class);
	    }
	  
	  @Test
	    void toEntity_shouldMapCreateDtoToEntity() {
	        UserCreateDto dto = new UserCreateDto();
	        dto.setUsername("example");
	        dto.setPassword("secret123");

	        User entity = userMapper.toEntity(dto);

	        assertThat(entity.getId()).isNull();
	        assertThat(entity.getPasswordHash()).isEqualTo("secret123");
	        assertThat(entity.isEnabled()).isTrue();
	        assertThat(entity.getCreatedAt()).isNotNull();
	        assertThat(entity.getUpdatedAt()).isNotNull();
	        assertThat(entity.getRoles()).isEmpty();
	        assertThat(entity.getActions()).isEmpty();
	    }
	  @Test
	    void updateUserFromDto_shouldUpdateNonNullFields() {
	        User existingUser = new User();
	        existingUser.setUsername("oldName");
	      
	        existingUser.setCreatedAt(LocalDateTime.of(2025,1,1,0,0));

	        UpdateUserDTO dto = new UpdateUserDTO();
	        dto.setUsername("newName");
	      

	        User updated = userMapper.updateUserFromDto(dto, existingUser);

	        assertThat(updated.getUsername()).isEqualTo("newName");
	  
	        assertThat(updated.getCreatedAt()).isEqualTo(LocalDateTime.of(2025,1,1,0,0));
	        assertThat(updated.getUpdatedAt()).isNotNull();
	    }

	    @Test
	    void toResponseDto_shouldMapRolesToSet() {
	        Role admin = new Role();
	        admin.setName("ADMIN");
	        Role userRole = new Role();
	        userRole.setName("USER");

	        User entity = new User();
	        entity.setRoles(Set.of(admin, userRole));

	        UserResponseDto dto = userMapper.toResponseDto(entity);

	        assertThat(dto.getRoles()).containsExactlyInAnyOrder("ADMIN", "USER");
	    }
	}


