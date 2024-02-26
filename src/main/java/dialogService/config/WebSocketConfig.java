package dialogService.config;

import dialogService.services.impl.AuthenticationService;
import dialogService.services.impl.MessageServiceImpl;
import dialogService.services.webSocket.WebSocketHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.socket.config.annotation.*;

@Configuration
@EnableWebSocket
@RequiredArgsConstructor
public class WebSocketConfig implements WebSocketConfigurer {
    private final AuthenticationService authenticationService;
    private final MessageServiceImpl messageService;
    @Bean
    public dialogService.services.webSocket.WebSocketHandler getWebSocketHandler() {
        return new WebSocketHandler(messageService,authenticationService);
    }
    @Override
    public void registerWebSocketHandlers(WebSocketHandlerRegistry registry) {
        registry.addHandler(getWebSocketHandler(), "/api/v1/streaming/ws")
                .setAllowedOriginPatterns("*");

    }
}
