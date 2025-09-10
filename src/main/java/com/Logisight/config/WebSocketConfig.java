package com.Logisight.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import com.Logisight.security.WebSocketAuthInterceptor;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

	  private final WebSocketAuthInterceptor authInterceptor;
	  
	  public WebSocketConfig(WebSocketAuthInterceptor authInterceptor) {
	        this.authInterceptor = authInterceptor;
	    }

	    @Override
	    public void registerStompEndpoints(StompEndpointRegistry registry) {
	        registry
	            .addEndpoint("/api/ws") // 🔥 frontend ile tam eşleşmeli
	            .setAllowedOriginPatterns("*")
	            .withSockJS();
	    }

	    @Override
	    public void configureMessageBroker(MessageBrokerRegistry config) {
	        config.enableSimpleBroker("/topic", "/queue");
	        config.setApplicationDestinationPrefixes("/app");
	    }

	    @Override
	    public void configureClientInboundChannel(ChannelRegistration registration) {
	        registration.interceptors(authInterceptor);
	    }
	}
