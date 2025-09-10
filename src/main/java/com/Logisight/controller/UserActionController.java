package com.Logisight.controller;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.format.annotation.DateTimeFormat;
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
import com.Logisight.dto.create.CreateUserActionDTO;
import com.Logisight.dto.response.UserActionResponseDto;
import com.Logisight.dto.update.UpdateUserActionDTO;
import com.Logisight.entity.UserAction;
import com.Logisight.service.abstracts.UserActionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/user-actions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_SUPERIOR')")
public class UserActionController {
	
	private final UserActionService userActionService;

	 @PostMapping
	    @LogUserAction(
	        actionType = "USER_ACTION_CREATE",
	        dynamicDetail = true,
	        entityClass = UserAction.class,
	        phase = CapturePhase.AFTER,
	        lookupField = "id"
	    )
	    public ResponseEntity<UserActionResponseDto> createUserAction(
	            @RequestBody @Valid CreateUserActionDTO dto) {
	        UserActionResponseDto response = userActionService.createUserAction(dto);
	        return new ResponseEntity<>(response, HttpStatus.CREATED);
	    }

	    @PutMapping("/{id}")
	    @LogUserAction(
	        actionType = "USER_ACTION_UPDATE",
	        dynamicDetail = true,
	        entityClass = UserAction.class,
	        phase = CapturePhase.BEFORE,
	        lookupField = "id"
	    )
	    public ResponseEntity<UserActionResponseDto> updateUserAction(
	            @PathVariable Long id,
	            @RequestBody @Valid UpdateUserActionDTO dto) {
	        return userActionService.updateUserAction(id, dto)
	                .map(ResponseEntity::ok)
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }

	    @GetMapping("/{id}")
	    public ResponseEntity<UserActionResponseDto> getUserActionById(@PathVariable Long id) {
	        return userActionService.getUserActionById(id)
	                .map(ResponseEntity::ok)
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }

	    @GetMapping("/by-user/{userId}")
	    public ResponseEntity<Page<UserActionResponseDto>> getUserActionsByUserAndDateRange(
	            @PathVariable Long userId,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
	            Pageable pageable) {
	        Page<UserActionResponseDto> page = userActionService.getUserActionsByUserAndDateRange(userId, start, end, pageable);
	        return ResponseEntity.ok(page);
	    }

	    @GetMapping("/by-ip")
	    public ResponseEntity<Page<UserActionResponseDto>> getUserActionsByIpAddressAndDateRange(
	            @RequestParam String ipAddress,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime start,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime end,
	            Pageable pageable) {
	        Page<UserActionResponseDto> page = userActionService.getUserActionsByIpAddressAndDateRange(ipAddress, start, end, pageable);
	        return ResponseEntity.ok(page);
	    }

	    @GetMapping("/by-session/{sessionId}")
	    public ResponseEntity<Page<UserActionResponseDto>> getUserActionsBySessionId(
	            @PathVariable String sessionId,
	            Pageable pageable) {
	        Page<UserActionResponseDto> page = userActionService.getUserActionsBySessionId(sessionId, pageable);
	        return ResponseEntity.ok(page);
	    }

	    @GetMapping("/by-anomaly/{anomalyId}")
	    public ResponseEntity<Page<UserActionResponseDto>> getUserActionsByAnomalyId(
	            @PathVariable Long anomalyId,
	            Pageable pageable) {
	        Page<UserActionResponseDto> page = userActionService.getUserActionsByAnomalyId(anomalyId, pageable);
	        return ResponseEntity.ok(page);
	    }

	    @GetMapping("/count-by-user-since/{userId}")
	    public ResponseEntity<Long> countUserActionsSince(
	            @PathVariable Long userId,
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime since) {
	        Long count = userActionService.countUserActionsSince(userId, since);
	        return ResponseEntity.ok(count);
	    }

	    @GetMapping("/count-grouped-by-type")
	    public ResponseEntity<List<Object[]>> countActionsGroupedByType() {
	        List<Object[]> result = userActionService.countActionsGroupedByType();
	        return ResponseEntity.ok(result);
	    }

	    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERIOR')")
	    @GetMapping
	    public ResponseEntity<Page<UserActionResponseDto>> getAllUserActions(
	            @RequestParam(defaultValue = "0") int page,
	            @RequestParam(defaultValue = "10") int size) {
	        Pageable pageable = PageRequest.of(page, size);
	        Page<UserActionResponseDto> actions = userActionService.getAllUserActions(pageable);
	        return ResponseEntity.ok(actions);
	    }
	    
	    @DeleteMapping("/{id}")
	    @LogUserAction(
	        actionType = "USER_ACTION_DELETE",
	        entityClass = UserAction.class,
	        phase = CapturePhase.BEFORE,
	        lookupField = "id"
	    )
	    public ResponseEntity<Void> deleteUserAction(@PathVariable Long id) {
	        userActionService.deleteUserAction(id);
	        return ResponseEntity.noContent().build();
	    }
	}