package com.Logisight.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Logisight.aspect.CapturePhase;
import com.Logisight.aspect.LogUserAction;
import com.Logisight.dto.create.CreateRoleDTO;
import com.Logisight.dto.response.RoleResponseDto;
import com.Logisight.dto.update.UpdateRoleDTO;
import com.Logisight.entity.Role;
import com.Logisight.service.abstracts.RoleService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/roles")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class RoleController {
	  private final RoleService roleService;

	    @PostMapping 
	    @LogUserAction(
	        actionType = "ROLE_CREATE",
	        dynamicDetail = true,
	        entityClass = Role.class,
	        phase = CapturePhase.AFTER,
	        lookupField = "id"
	    )
	    public ResponseEntity<RoleResponseDto> createRole(@RequestBody @Valid CreateRoleDTO createDTO) {
	        RoleResponseDto response = roleService.createRole(createDTO);
	        return new ResponseEntity<>(response, HttpStatus.CREATED);
	    }

	    @PutMapping("/{id}")
	    @LogUserAction(
	        actionType = "ROLE_UPDATE",
	        dynamicDetail = true,
	        entityClass = Role.class,
	        phase = CapturePhase.BEFORE,
	        lookupField = "id"
	    )
	    public ResponseEntity<RoleResponseDto> updateRole(
	            @PathVariable Long id,
	            @RequestBody @Valid UpdateRoleDTO updateDTO) {
	        return roleService.updateRole(id, updateDTO)
	                .map(ResponseEntity::ok)
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }

	    @GetMapping("/{id}")
	    public ResponseEntity<RoleResponseDto> getRoleById(@PathVariable Long id) {
	        return roleService.getRoleById(id)
	                .map(ResponseEntity::ok)
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }

	    @GetMapping("/by-name")
	    public ResponseEntity<RoleResponseDto> getRoleByName(@RequestParam String name) {
	        return roleService.getRoleByName(name)
	                .map(ResponseEntity::ok)
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }

	    @GetMapping
	    public ResponseEntity<List<RoleResponseDto>> getAllRoles() {
	        List<RoleResponseDto> roles = roleService.getAllRoles();
	        return ResponseEntity.ok(roles);
	    }

	    @GetMapping("/exists")
	    public ResponseEntity<Boolean> existsByName(@RequestParam String name) {
	        boolean exists = roleService.existsByName(name);
	        return ResponseEntity.ok(exists);
	    }

	    @DeleteMapping("/{id}")
	    @LogUserAction(
	        actionType = "ROLE_DELETE",
	        entityClass = Role.class,
	        phase = CapturePhase.BEFORE,
	        lookupField = "id"
	    )
	    public ResponseEntity<Void> deleteRole(@PathVariable Long id) {
	        roleService.deleteRole(id);
	        return ResponseEntity.noContent().build();
	    }
	}