// WebSocketConfig.java の修正後の全文（configureWebSocketTransport を削除）

package com.example.revitech.config; // パッケージ名は適宜修正してください

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        config.enableSimpleBroker("/topic");
        config.setApplicationDestinationPrefixes("/app");
    }
    
    // 【重要】configureWebSocketTransport(WebSocketTransportRegistration registration) メソッドを削除

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // CORS設定はSecurityConfigに任せる
        registry.addEndpoint("/ws")
                .withSockJS(); 
    }
}