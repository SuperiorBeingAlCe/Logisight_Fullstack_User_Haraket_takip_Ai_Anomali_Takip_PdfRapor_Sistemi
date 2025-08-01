package com.Logisight.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    // 1. Endpoint tanımı
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry
            .addEndpoint("/ws") // WebSocket bağlantısı buradan başlar
            .setAllowedOriginPatterns("*") // frontend nerelerden bağlanabilir
            .withSockJS(); // SockJS fallback desteği (tarayıcı uyumu için)
    }

    // 2. Broker ayarları
    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic", "/queue"); // frontend’in dinlediği alanlar
        config.setApplicationDestinationPrefixes("/app"); // frontend’in mesaj gönderdiği alanlar
    }
}
