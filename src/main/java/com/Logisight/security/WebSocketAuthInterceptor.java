package com.Logisight.security;

import java.util.List;

import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Component;

import com.Logisight.util.JwtUtil;

import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class WebSocketAuthInterceptor implements ChannelInterceptor{

	 private final JwtUtil jwtUtil;

	    @Override
	    public Message<?> preSend(Message<?> message, MessageChannel channel) {
	        StompHeaderAccessor accessor = StompHeaderAccessor.wrap(message);

	        if (accessor.getCommand() == null) return message; // info/heartbeat frame’leri geç

	        if (StompCommand.CONNECT.equals(accessor.getCommand())) {
	            String authHeader = accessor.getFirstNativeHeader("Authorization");
	           
	            String token = authHeader.substring(7);
	            if (!jwtUtil.validateToken(token)) throw new IllegalArgumentException("Geçersiz token");

	            UsernamePasswordAuthenticationToken auth =
	                new UsernamePasswordAuthenticationToken(jwtUtil.getUsernameFromToken(token), null, List.of());
	            accessor.setUser(auth);
	        }

	        return message;
	    }
	}