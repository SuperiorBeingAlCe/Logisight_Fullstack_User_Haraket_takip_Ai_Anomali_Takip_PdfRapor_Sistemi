package com.Logisight.service.concretes;

import java.time.Clock;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.Logisight.dto.create.CreateAnomalyDTO;
import com.Logisight.dto.create.CreateNotificationDTO;
import com.Logisight.dto.create.CreateSessionDTO;
import com.Logisight.dto.create.CreateUserActionDTO;
import com.Logisight.dto.request.LoginRequestDTO;
import com.Logisight.dto.response.AnomalyResponseDTO;
import com.Logisight.dto.response.LoginResponseDTO;
import com.Logisight.dto.response.SessionResponseDTO;
import com.Logisight.dto.update.UpdateSessionDTO;
import com.Logisight.jexception.BusinessException;
import com.Logisight.jexception.ErrorCode;
import com.Logisight.service.abstracts.AnomalyService;
import com.Logisight.service.abstracts.AuthService;
import com.Logisight.service.abstracts.NotificationService;
import com.Logisight.service.abstracts.SessionService;
import com.Logisight.service.abstracts.UserActionService;
import com.Logisight.util.IpUtil;
import com.Logisight.util.JwtUtil;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class AuthManager implements AuthService {

    private final AuthenticationManager authManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;
    private final SessionService sessionService;
    private final UserActionService userActionService;
  private final AnomalyService anomalyService;
  private final NotificationService notificationService;
  
  private final Clock clock;
    
    @Override
    public void logout(String sessionId, HttpServletRequest httpRequest) {
    	
    	LocalDateTime now = LocalDateTime.now(clock);
    	
        SessionResponseDTO session = sessionService.getBySessionId(sessionId)
                .orElseThrow(() -> new BusinessException(ErrorCode.SESSION_NOT_FOUND));

        if (!session.isActive()) {
            throw new BusinessException(ErrorCode.SESSION_ALREADY_LOGGED_OUT);
        }
        
        long duration = Duration.between(session.getCreatedAt(), now).toMillis();
        UpdateSessionDTO updateDTO = new UpdateSessionDTO();
        updateDTO.setActive(false);
     
        sessionService.updateSession(session.getSessionId(), updateDTO);
        
        CreateUserActionDTO actionDTO = new CreateUserActionDTO();
        actionDTO.setUserId(session.getUserId());
        actionDTO.setSessionId(session.getSessionId());
        actionDTO.setActionType("LOGOUT");
        actionDTO.setActionDetail("Kullanıcı oturumu sonlandırdı.");
        actionDTO.setActionTimestamp(now);
        actionDTO.setDurationMs(duration);
        actionDTO.setIpAddress(IpUtil.getClientIp(httpRequest));
        actionDTO.setUserAgent(httpRequest.getHeader("User-Agent"));

        userActionService.createUserAction(actionDTO);
    }
    
    @Override
    public LoginResponseDTO login(LoginRequestDTO request, HttpServletRequest httpRequest) {
        // Kullanıcı doğrulama
        Authentication authentication = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();

        // Session oluştur
        String sessionId = UUID.randomUUID().toString();
        LocalDateTime now = LocalDateTime.now(clock);
        LocalDateTime expireAt = now.plus(Duration.ofMillis(jwtUtil.getExpirationMillis()));

        CreateSessionDTO sessionDTO = new CreateSessionDTO();
        sessionDTO.setSessionId(sessionId);
        sessionDTO.setUserId(userDetails.getId());
        sessionDTO.setCreatedAt(now);
        sessionDTO.setExpiredAt(expireAt);
        sessionDTO.setActive(true);
        sessionDTO.setIpAddress(IpUtil.getClientIp(httpRequest));
        sessionDTO.setUserAgent(httpRequest.getHeader("User-Agent"));

        SessionResponseDTO session = sessionService.createSession(sessionDTO);
        
       
        
        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .orElse("ROLE_USER");
        
        
        // JWT üret
        String token = jwtUtil.generateToken(
                userDetails.getUsername(),
                role,  
                sessionId
        );
        AnomalyResponseDTO anomalyResponse = null;
        Long anomalyId = null;

        if (now.toLocalTime().isAfter(LocalTime.of(21, 0))) {
            CreateAnomalyDTO anomalyDTO = new CreateAnomalyDTO();
            anomalyDTO.setUserId(userDetails.getId());
            anomalyDTO.setAnomalyType("LATE_LOGIN");
            anomalyDTO.setDescription("User logged in after 9 PM");
            anomalyDTO.setDetectedAt(now);

            anomalyResponse = anomalyService.createAnomaly(anomalyDTO);
            if (anomalyResponse != null) {        // ⚡ null check
                anomalyId = anomalyResponse.getId();
            } 
        }

        // User Action kaydı (tek seferde)
        CreateUserActionDTO actionDTO = new CreateUserActionDTO();
        actionDTO.setUserId(userDetails.getId());
        actionDTO.setSessionId(session.getSessionId());
        actionDTO.setActionType("LOGIN");
        actionDTO.setActionDetail("Kullanıcı başarılı şekilde giriş yaptı.");
        actionDTO.setActionTimestamp(now);
        actionDTO.setDurationMs(0L);
        actionDTO.setIpAddress(IpUtil.getClientIp(httpRequest));
        actionDTO.setUserAgent(httpRequest.getHeader("User-Agent"));
        actionDTO.setAnomalyId(anomalyId); // null olabilir, sorun değil

        userActionService.createUserAction(actionDTO);
        
        CreateNotificationDTO welcomeNotification = new CreateNotificationDTO();
        welcomeNotification.setRecipientId(userDetails.getId());
        welcomeNotification.setMessage("Hoş geldin! .");
        welcomeNotification.setLink("/home");
        welcomeNotification.setCreatedAt(now);
        welcomeNotification.setRead(false);

        notificationService.createNotification(welcomeNotification);
        
        return new LoginResponseDTO(
        		 token,
                 session.getSessionId(),
                 session.getUserId(),
                 role,
                 session.getExpiredAt()
        );
        
    }
}
