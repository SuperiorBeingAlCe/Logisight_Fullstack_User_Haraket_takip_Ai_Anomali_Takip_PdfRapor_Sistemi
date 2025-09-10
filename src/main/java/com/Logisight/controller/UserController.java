package com.Logisight.controller;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.Logisight.aspect.CapturePhase;
import com.Logisight.aspect.LogUserAction;
import com.Logisight.dto.create.UserCreateDto;
import com.Logisight.dto.response.UserResponseDto;
import com.Logisight.dto.update.UpdateUserDTO;
import com.Logisight.entity.User;
import com.Logisight.service.abstracts.UserService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "https://localhost:3000")
@PreAuthorize("hasRole('ROLE_ADMIN')")
public class UserController {

    private final UserService userService;

    // 🔧 Kullanıcı oluştur (AFTER — ID işlemden sonra oluşur)
    @PostMapping("/save")
    @LogUserAction(
        actionType = "USER_CREATE",
        dynamicDetail = true,
        entityClass = User.class,
        phase = CapturePhase.AFTER,
        lookupField = "id",
        sendNotification = true,
        notificationMessage = "Hoş geldin! Aramıza katıldığın için teşekkürler.",
        notificationLink = "/home"
    )
    public ResponseEntity<UserResponseDto> createUser(@RequestBody @Valid UserCreateDto dto) {
        UserResponseDto response = userService.createUser(dto);
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    // ✏️ Kullanıcı güncelle (BEFORE — ID önceden var) 
    @PutMapping("/{id}")
    @LogUserAction(
        actionType = "USER_UPDATE",
        dynamicDetail = true,
        entityClass = User.class,
        phase = CapturePhase.BEFORE,
        lookupField = "id"
    )
    public ResponseEntity<UserResponseDto> updateUser(
            @PathVariable Long id,
            @RequestBody @Valid UpdateUserDTO dto) {
        UserResponseDto response = userService.updateUser(id, dto);
        return ResponseEntity.ok(response);
    }

    // ❌ Kullanıcı sil (BEFORE — işlemden önce bilgi çekilir)
    @DeleteMapping("/{id}")
    @LogUserAction(
        actionType = "USER_DELETE",
        entityClass = User.class,
        phase = CapturePhase.BEFORE,  
        lookupField = "id"
    )
    public ResponseEntity<Void> deleteUser(@PathVariable Long id) {
        userService.deleteUser(id);
        return ResponseEntity.noContent().build();
    }

    // 🔁 Kullanıcı aktif/pasif yap (BEFORE)
    @PatchMapping("/{id}/enabled")
    @LogUserAction(
        actionType = "USER_ENABLE_CHANGE",
        dynamicDetail = true,
        entityClass = User.class,
        phase = CapturePhase.BEFORE,
        lookupField = "id"
    )
    public ResponseEntity<UserResponseDto> setUserEnabled(
            @PathVariable Long id,
            @RequestParam boolean enabled) {
        UserResponseDto response = userService.setUserEnabled(id, enabled);
        return ResponseEntity.ok(response);
    }

    // GET işlemler — loglama yapılmıyor
    @GetMapping("/{id}")
    public ResponseEntity<UserResponseDto> getById(@PathVariable Long id) {
        return userService.getUserById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-username")
    public ResponseEntity<UserResponseDto> getByUsername(@RequestParam String username) {
        return userService.getUserByUsername(username)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/by-email")
    public ResponseEntity<UserResponseDto> getByEmail(@RequestParam String email) {
        return userService.getUserByEmail(email)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/enabled")
    public ResponseEntity<Page<UserResponseDto>> getEnabledUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getEnabledUsers(page, size));
    }

    @GetMapping
    public ResponseEntity<Page<UserResponseDto>> getAllUsers(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        return ResponseEntity.ok(userService.getAllUsers(page, size));
    }

    @GetMapping("/exists/username/{username}")
    public ResponseEntity<Boolean> existsByUsername(@PathVariable String username) {
        return ResponseEntity.ok(userService.existsByUsername(username));
    }

    @GetMapping("/exists/email/{email}")
    public ResponseEntity<Boolean> existsByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.existsByEmail(email));
    }
}