package com.Logisight.aspect;

import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Optional;
import java.util.UUID;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.context.ApplicationContext;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.Logisight.dto.create.CreateNotificationDTO;
import com.Logisight.dto.create.CreateUserActionDTO;
import com.Logisight.entity.UserAction;
import com.Logisight.mapper.UserActionMapper;
import com.Logisight.repository.UserActionRepository;
import com.Logisight.repository.UserRepository;
import com.Logisight.service.abstracts.NotificationService;
import com.Logisight.service.concretes.UserDetailsImpl;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * Güçlü, güvenli ve kapsamlı bir Entity action logging aspect'i.
 */
@Aspect
@Component
@RequiredArgsConstructor
@Slf4j
public class EntityActionLoggingAspect {

    private final ApplicationContext applicationContext;
    private final HttpServletRequest request;
    private final UserActionMapper mapper;
    private final UserActionRepository actionRepo;
    private final UserRepository userRepository;

    @Around("@annotation(logUserAction)")
    public Object logAction(ProceedingJoinPoint joinPoint, LogUserAction logUserAction) throws Throwable {
        Object entityBefore = null;

        if (logUserAction.phase() == CapturePhase.BEFORE || logUserAction.phase() == CapturePhase.BOTH) {
            entityBefore = safeFetchEntity(joinPoint, logUserAction);
        }

        Object result = joinPoint.proceed();

        Object entityAfter = null;
        if (logUserAction.phase() == CapturePhase.AFTER || logUserAction.phase() == CapturePhase.BOTH) {
            entityAfter = extractEntityFromReturn(result, logUserAction);
            if (entityAfter == null) {
                entityAfter = safeFetchEntity(joinPoint, logUserAction);
            }
        }

        Object entityToLog = null;
        if (logUserAction.dynamicDetail()) {
            entityToLog = entityAfter != null ? entityAfter : entityBefore;
        } else {
            // dynamicDetail false ise yine entity bilgisi eklensin istiyorsan:
            entityToLog = entityBefore != null ? entityBefore : entityAfter;
        }

        saveActionLog(logUserAction, entityToLog);

        return result;
    }

    private Object extractEntityFromReturn(Object returnValue, LogUserAction logUserAction) {
        if (returnValue == null) return null;

        // Eğer ResponseEntity sarılıysa gövdeyi al
        if (returnValue instanceof ResponseEntity) {
            Object body = ((ResponseEntity<?>) returnValue).getBody();
            if (body == null) return null;
            return extractEntityFromBodyOrDto(body, logUserAction);
        }

        // Direkt dönen değer (DTO ya da entity)
        return extractEntityFromBodyOrDto(returnValue, logUserAction);
    }

    private Object extractEntityFromBodyOrDto(Object body, LogUserAction logUserAction) {
        Class<?> entityClass = logUserAction.entityClass();

        // Eğer body zaten hedef entity ise direkt dön
        if (entityClass.isInstance(body)) {
            return body;
        }

        // DTO ise id getter'ını dene
        try {
            Method getIdMethod = null;
            try {
                getIdMethod = body.getClass().getMethod("getId");
            } catch (NoSuchMethodException e) {
                // bazen "getId" yoktur; dene "getUserId" veya "getId()" yoksa atla
                // burada özel durumlar ekleyebilirsin
            }

            if (getIdMethod != null) {
                Object id = getIdMethod.invoke(body);
                if (id != null) {
                    String repoBeanName = decap(entityClass.getSimpleName()) + "Repository";
                    @SuppressWarnings("unchecked")
                    JpaRepository<Object, Long> repository = (JpaRepository<Object, Long>) applicationContext.getBean(repoBeanName);
                    Long idLong = convertToLong(id);
                    Optional<?> entityOpt = repository.findById(idLong);
                    return entityOpt.orElse(null);
                }
            }
        } catch (Exception e) {
            log.warn("extractEntityFromReturn: dto->entity dönüşümünde hata: {}", e.getMessage());
        }
        return null;
    }

    private String decap(String s) {
        if (s == null || s.isEmpty()) return s;
        return Character.toLowerCase(s.charAt(0)) + s.substring(1);
    }

    private Long convertToLong(Object idValue) {
        if (idValue instanceof Long) return (Long) idValue;
        if (idValue instanceof Integer) return ((Integer) idValue).longValue();
        if (idValue instanceof String) return Long.parseLong((String) idValue);
        throw new IllegalArgumentException("ID tipi desteklenmiyor: " + idValue.getClass());
    }

    private void saveActionLog(LogUserAction logUserAction, Object entity) {
        String detail = logUserAction.actionDetail();

        if (logUserAction.dynamicDetail() && entity != null) {
            detail = entity.toString();
        } else if (entity != null) {
            detail += " | " + entity.toString();
        }

        Long currentUserId = getCurrentUserId();
        if (currentUserId == null) {
            log.warn("Kullanıcı kimliği alınamadı — log kaydı atlandı.");
            return;
        }

        CreateUserActionDTO dto = new CreateUserActionDTO();
        dto.setUserId(currentUserId);
        dto.setActionType(logUserAction.actionType());
        dto.setActionDetail(detail);
        dto.setActionTimestamp(LocalDateTime.now());
        dto.setIpAddress(request.getRemoteAddr());
        dto.setSessionId(Optional.ofNullable(request.getHeader("Session-Id"))
                .orElse(UUID.randomUUID().toString()));
        dto.setDurationMs(0L);
        dto.setUserAgent(request.getHeader("User-Agent"));

        UserAction action = mapper.toEntity(dto);

        if (logUserAction.sendNotification() && currentUserId != null) {
            CreateNotificationDTO notif = new CreateNotificationDTO();
            notif.setRecipientId(currentUserId);
            notif.setMessage(
                logUserAction.notificationMessage().isEmpty() 
                    ? detail : logUserAction.notificationMessage()
            );
            notif.setCreatedAt(LocalDateTime.now());
            notif.setRead(false);
            notif.setLink(logUserAction.notificationLink().isEmpty() ? null : logUserAction.notificationLink());
            
            NotificationService notificationService = applicationContext.getBean(NotificationService.class);
            notificationService.createNotification(notif);
        }
        
        // Mapper user'ı ignore ettiğinden dolayı entity olarak set etmeliyiz
        userRepository.findById(currentUserId)
                .ifPresentOrElse(action::setUser, () -> {
                    throw new IllegalStateException("User not found for id: " + currentUserId);
                });

        actionRepo.save(action);
        log.debug("UserAction kaydedildi: userId={} actionType={} detail={}",
                currentUserId, logUserAction.actionType(), detail);
    }

    private Object safeFetchEntity(ProceedingJoinPoint joinPoint, LogUserAction logUserAction) {
        try {
            return fetchEntity(joinPoint, logUserAction);
        } catch (Exception e) {
            log.warn("Entity fetch failed during logging: {}", e.getMessage());
            return null;
        }
    }

    private Object fetchEntity(ProceedingJoinPoint joinPoint, LogUserAction logUserAction) {
        Class<?> entityClass = logUserAction.entityClass();
        String lookupField = logUserAction.lookupField();

        Object searchValue = findMatchingArg(joinPoint.getArgs(), lookupField);
        if (searchValue == null) return null;

        String repoBeanName = decap(entityClass.getSimpleName()) + "Repository";

        @SuppressWarnings("unchecked")
        JpaRepository<Object, Long> repository = (JpaRepository<Object, Long>) applicationContext.getBean(repoBeanName);

        if (lookupField.equalsIgnoreCase("id")) {
            return findById(repository, searchValue);
        } else {
            return findByCustomField(repository, lookupField, searchValue);
        }
    }

    private Object findById(JpaRepository<Object, Long> repository, Object idValue) {
        try {
            Long idLong = convertToLong(idValue);
            Optional<?> entityOpt = repository.findById(idLong);
            return entityOpt.orElse(null);
        } catch (Exception e) {
            throw new RuntimeException("findById invocation failed", e);
        }
    }

    private Object findByCustomField(JpaRepository<Object, Long> repository, String lookupField, Object value) {
        try {
            String methodName = "findBy" + StringUtils.capitalize(lookupField);

            // Güvenli arama: repository proxy'sinin tüm metodlarına bak, uygun parametreyi kabul eden metodu bul
            Method finder = Arrays.stream(repository.getClass().getMethods())
                    .filter(m -> m.getName().equals(methodName)
                            && m.getParameterCount() == 1
                            && m.getParameterTypes()[0].isAssignableFrom(value.getClass()))
                    .findFirst()
                    .orElseThrow(() -> new NoSuchMethodException("Finder not found: " + methodName + "(" + value.getClass() + ")"));

            Object result = finder.invoke(repository, value);
            // result tipleri değişebilir (Optional<T> ya da T)
            if (result == null) return null;
            if (result instanceof Optional) {
                return ((Optional<?>) result).orElse(null);
            }
            return result;
        } catch (Exception e) {
            throw new RuntimeException("Lookup method not found for " + lookupField, e);
        }
    }

    private Object findMatchingArg(Object[] args, String lookupField) {
        if (args == null || args.length == 0) return null;
        for (Object arg : args) {
            if (arg == null) continue;
            if (lookupField.equalsIgnoreCase("id")) {
                if (arg instanceof Long || arg instanceof Integer || isNumericString(arg)) {
                    return arg;
                }
            } else {
                // genel durumda String alan araması (username,email vb.)
                if (arg instanceof String) {
                    return arg;
                }
                // DTO içinden de değer çekmek istersen burada genişletebilirsin
            }
        }
        return null;
    }

    private boolean isNumericString(Object arg) {
        if (!(arg instanceof String)) return false;
        try {
            Long.parseLong((String) arg);
            return true;
        } catch (NumberFormatException ex) {
            return false;
        }
    }

    private Long getCurrentUserId() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            Object principal = auth.getPrincipal();
            log.debug("Principal class: {}", principal.getClass());
            if (principal instanceof UserDetailsImpl) {
                Long id = ((UserDetailsImpl) principal).getId();
                log.debug("Current User ID: {}", id);
                return id;
            }
        }
        log.warn("Kullanıcı kimliği alınamadı.");
        return null;
    }
}


