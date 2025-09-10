package com.Logisight.service.abstracts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.Logisight.dto.create.CreateUserActionDTO;
import com.Logisight.dto.response.UserActionResponseDto;
import com.Logisight.dto.update.UpdateUserActionDTO;

public interface UserActionService {
	  // Yeni aksiyon yarat
    UserActionResponseDto createUserAction(CreateUserActionDTO createUserActionDTO);

    // Var olan aksiyonu güncelle
    Optional<UserActionResponseDto> updateUserAction(Long id, UpdateUserActionDTO updateUserActionDTO);

    // ID ile aksiyon bul
    Optional<UserActionResponseDto> getUserActionById(Long id);

    // Kullanıcıya ait aksiyonları zaman aralığında sayfalı getir
    Page<UserActionResponseDto> getUserActionsByUserAndDateRange(Long userId, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // IP adresine göre filtrelenmiş aksiyonları sayfalı getir
    Page<UserActionResponseDto> getUserActionsByIpAddressAndDateRange(String ipAddress, LocalDateTime start, LocalDateTime end, Pageable pageable);

    // SessionId ile aksiyonları sayfalı getir
    Page<UserActionResponseDto> getUserActionsBySessionId(String sessionId, Pageable pageable);

    // Anomaly ID’ye göre aksiyonları sayfalı getir
    Page<UserActionResponseDto> getUserActionsByAnomalyId(Long anomalyId, Pageable pageable);

    // Kullanıcıya ait belli bir tarihten sonraki aksiyon sayısı
    Long countUserActionsSince(Long userId, LocalDateTime since);

    // ActionType bazında gruplanmış sayılar
    List<Object[]> countActionsGroupedByType();

    // ID ile aksiyon sil
    void deleteUserAction(Long id);

	Page<UserActionResponseDto> getAllUserActions(Pageable pageable);

}
