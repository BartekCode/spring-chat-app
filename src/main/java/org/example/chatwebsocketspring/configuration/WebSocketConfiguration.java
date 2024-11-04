package org.example.chatwebsocketspring.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker // odpowiedzialny za mozliwosc korzystania z websocket
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // chcemy dodac nowe endpointy do naszej konfiguracji webSocketa
        registry.addEndpoint("/chat-socket").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // dodanie applicaton destination prefixes(where this should go) i konfiguracja borkera jako topic i queue
        registry.setApplicationDestinationPrefixes("/app");
        registry.enableSimpleBroker("/topic");
    }

    /* by poinformowac innych uczestnikow ze ktos juz nie jest połączony z naszym webSocket zapisany sie na
    session disconnetc event
     */
}
