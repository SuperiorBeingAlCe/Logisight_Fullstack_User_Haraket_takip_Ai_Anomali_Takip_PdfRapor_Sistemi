package com.Logisight.controller;

import java.time.LocalDateTime;

import org.springframework.data.domain.Page;
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
import com.Logisight.dto.create.CreateSessionDTO;
import com.Logisight.dto.response.SessionResponseDTO;
import com.Logisight.dto.update.UpdateSessionDTO;
import com.Logisight.entity.Session;
import com.Logisight.service.abstracts.SessionService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/sessions")
@RequiredArgsConstructor
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class SessionController {
	 private final SessionService sessionService;

	    @PostMapping
	    @LogUserAction(
	        actionType = "SESSION_CREATE",
	        dynamicDetail = true,
	        entityClass = Session.class,
	        phase = CapturePhase.AFTER,
	        lookupField = "id"
	    )
	    public ResponseEntity<SessionResponseDTO> createSession(@RequestBody @Valid CreateSessionDTO createDTO) {
	        SessionResponseDTO response = sessionService.createSession(createDTO);
	        return new ResponseEntity<>(response, HttpStatus.CREATED);
	    }

	    @PutMapping("/{id}")
	    @LogUserAction(
	        actionType = "SESSION_UPDATE",
	        dynamicDetail = true,
	        entityClass = Session.class,
	        phase = CapturePhase.BEFORE,
	        lookupField = "id"
	    )
	    public ResponseEntity<SessionResponseDTO> updateSession(
	            @PathVariable Long id,
	            @RequestBody @Valid UpdateSessionDTO updateDTO) {
	        return sessionService.updateSession(id, updateDTO)
	                .map(ResponseEntity::ok)
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }

	    @PutMapping("/by-session-id/{sessionId}")
	    @LogUserAction(
	        actionType = "SESSION_UPDATE_BY_SESSION_ID",
	        dynamicDetail = true,
	        entityClass = Session.class,
	        phase = CapturePhase.BEFORE,
	        lookupField = "sessionId"
	    )
	    public ResponseEntity<SessionResponseDTO> updateSessionBySessionId(
	            @PathVariable String sessionId,
	            @RequestBody @Valid UpdateSessionDTO updateDTO) {
	        return sessionService.updateSession(sessionId, updateDTO)
	                .map(ResponseEntity::ok)
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }

	    @GetMapping("/{id}")
	    public ResponseEntity<SessionResponseDTO> getSessionById(@PathVariable Long id) {
	        return sessionService.getSessionById(id)
	                .map(ResponseEntity::ok)
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }

	    @GetMapping("/by-session-id/{sessionId}")
	    public ResponseEntity<SessionResponseDTO> getSessionBySessionId(@PathVariable String sessionId) {
	        return sessionService.getSessionBySessionId(sessionId)
	                .map(ResponseEntity::ok)
	                .orElseGet(() -> ResponseEntity.notFound().build());
	    }

	    @GetMapping("/active/by-user/{userId}")
	    public ResponseEntity<Page<SessionResponseDTO>> getActiveSessionsByUserId(
	            @PathVariable Long userId,
	            Pageable pageable) {
	        Page<SessionResponseDTO> page = sessionService.getActiveSessionsByUserId(userId, pageable);
	        return ResponseEntity.ok(page);
	    }

	    @GetMapping("/by-user/{userId}")
	    public ResponseEntity<Page<SessionResponseDTO>> getSessionsByUserId(
	            @PathVariable Long userId,
	            Pageable pageable) {
	        Page<SessionResponseDTO> page = sessionService.getSessionsByUserId(userId, pageable);
	        return ResponseEntity.ok(page);
	    }

	    @GetMapping("/expired-active")
	    public ResponseEntity<Page<SessionResponseDTO>> getExpiredActiveSessions(
	            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime now,
	            Pageable pageable) {
	        Page<SessionResponseDTO> page = sessionService.getExpiredActiveSessions(now, pageable);
	        return ResponseEntity.ok(page);
	    }

	    @DeleteMapping("/{id}")
	    @LogUserAction(
	        actionType = "SESSION_DELETE",
	        entityClass = Session.class,
	        phase = CapturePhase.BEFORE,
	        lookupField = "id"
	    )
	    public ResponseEntity<Void> deleteSession(@PathVariable Long id) {
	        sessionService.deleteSession(id);
	        return ResponseEntity.noContent().build();
	    }

	    @GetMapping("/is-active/{sessionId}")
	    public ResponseEntity<Boolean> isSessionActive(@PathVariable String sessionId) {
	        boolean active = sessionService.isSessionActive(sessionId);
	        return ResponseEntity.ok(active);
	    }
	}