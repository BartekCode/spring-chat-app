package org.example.chatwebsocketspring.configuration;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.converter.DefaultContentTypeResolver;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.converter.MessageConverter;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.MimeTypeUtils;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

import java.util.List;

@Configuration
@EnableWebSocketMessageBroker // odpowiedzialny za mozliwosc korzystania z websocket
public class WebSocketConfiguration implements WebSocketMessageBrokerConfigurer {

    private final ObjectMapper mapper;

    public WebSocketConfiguration(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // chcemy dodac nowe endpointy do naszej konfiguracji webSocketa
        registry.addEndpoint("/chat-socket").withSockJS();
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // dodanie applicaton destination prefixes(where this should go) i konfiguracja borkera jako topic i queue
        registry.setApplicationDestinationPrefixes("/app"); //prefix do wiadomosci
        registry.enableSimpleBroker("/topic", "/user");
        registry.setUserDestinationPrefix("/user"); // kanał do prywatynch wiadomowci
    }

    // how we want to convert our messages
    @Override
    public boolean configureMessageConverters(List<MessageConverter> messageConverters) {
        DefaultContentTypeResolver resolver = new DefaultContentTypeResolver();
        resolver.setDefaultMimeType(MimeTypeUtils.APPLICATION_JSON);
        MappingJackson2MessageConverter converter = new MappingJackson2MessageConverter();
        converter.setObjectMapper(mapper);
        converter.setContentTypeResolver(resolver);
        messageConverters.add(converter);
        return false;
    }
}
