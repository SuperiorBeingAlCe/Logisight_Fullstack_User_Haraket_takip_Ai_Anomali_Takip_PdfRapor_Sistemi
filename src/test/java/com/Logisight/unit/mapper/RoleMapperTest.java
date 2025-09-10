package com.Logisight.unit.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import com.Logisight.dto.create.CreateRoleDTO;
import com.Logisight.dto.response.RoleResponseDto;
import com.Logisight.dto.update.UpdateRoleDTO;
import com.Logisight.entity.Role;
import com.Logisight.mapper.RoleMapper;

public class RoleMapperTest {

	private RoleMapper roleMapper;
	
	@BeforeEach
	void setup() {
		roleMapper = Mappers.getMapper(RoleMapper.class);
	}
	
	 @Test
	    void toEntity_shouldMapCreateDtoToEntity() {
	        CreateRoleDTO dto = new CreateRoleDTO();
	        dto.setName("ADMIN");
	      

	        Role entity = roleMapper.toEntity(dto);

	        assertThat(entity.getId()).isNull(); // id DB tarafından setlenecek
	        assertThat(entity.getName()).isEqualTo(dto.getName());
	      
	    }

	    @Test
	    void updateEntity_shouldMergeNonNullFields() {
	        Role existing = new Role();
	        existing.setId(1L);
	        existing.setName("USER");
	      

	        UpdateRoleDTO dto = new UpdateRoleDTO();
	        dto.setName("MODERATOR"); // değişecek
	    

	        roleMapper.updateEntity(dto, existing);

	        assertThat(existing.getId()).isEqualTo(1L); // id değişmedi
	        assertThat(existing.getName()).isEqualTo("MODERATOR"); // değişti
	       
	    }

	    @Test
	    void toResponseDto_shouldMapEntityToResponse() {
	        Role entity = new Role();
	        entity.setId(5L);
	        entity.setName("ADMIN");
	       

	        RoleResponseDto dto = roleMapper.toResponseDto(entity);

	        assertThat(dto.getId()).isEqualTo(entity.getId());
	        assertThat(dto.getName()).isEqualTo(entity.getName());
	      
	    }
}
