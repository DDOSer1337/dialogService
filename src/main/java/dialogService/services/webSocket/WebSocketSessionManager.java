package dialogService.services.webSocket;

import lombok.Getter;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.WebSocketSession;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@Component
public class WebSocketSessionManager {
    private final Set<WebSocketSession> webSocketSessions = new HashSet<>();

    public Set<WebSocketSession> getWebSocketSessionExcept(WebSocketSession webSocketSession) {
        var nonMatchingSessions = this.webSocketSessions
                .stream()
                .filter(x -> !x.getId().equalsIgnoreCase(webSocketSession.getId()))
                .collect(Collectors.toSet());
        return nonMatchingSessions;
    }

    public void addWebSocketSessions(WebSocketSession webSocketSession) {
        this.webSocketSessions.add(webSocketSession);
    }

    public void removeWebSocketSessions(WebSocketSession webSocketSession) {
        var matchingSessions = this.webSocketSessions
                .stream()
                .filter(x -> !x.getId().equalsIgnoreCase(webSocketSession.getId()))
                .collect(Collectors.toSet());

        this.webSocketSessions.removeAll(matchingSessions);
    }
}
