package com.Logisight.service.abstracts;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

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

    // Kullanıcıya ait tüm aksiyonları zaman aralığında getir
    List<UserActionResponseDto> getUserActionsByUserAndDateRange(Long userId, LocalDateTime start, LocalDateTime end);

    // IP adresine göre filtrelenmiş aksiyonlar
    List<UserActionResponseDto> getUserActionsByIpAddressAndDateRange(String ipAddress, LocalDateTime start, LocalDateTime end);

    // SessionId ile aksiyonları getir
    List<UserActionResponseDto> getUserActionsBySessionId(String sessionId);

    // Anomaly ID’ye göre aksiyonları getir
    List<UserActionResponseDto> getUserActionsByAnomalyId(Long anomalyId);

    // Kullanıcıya ait belli bir tarihten sonraki aksiyon sayısı
    Long countUserActionsSince(Long userId, LocalDateTime since);

    // ActionType bazında gruplanmış sayılar
    List<Object[]> countActionsGroupedByType();

    // ID ile aksiyon sil
    void deleteUserAction(Long id);

}
