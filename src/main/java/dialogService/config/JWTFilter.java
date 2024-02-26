package dialogService.config;

import dialogService.JWUtil.JwtUtil;
import dialogService.controller.feign.AuthorizationSFC;
import dialogService.exeptionHandler.exceptions.UnauthorizedException;
import dialogService.services.webSocket.WebSocketHandler;
import dialogService.services.webSocket.WebSocketSessionManager;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javapro_team44.dto.authorization.AuthenticateResponseDto;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Slf4j
@Component
@RequiredArgsConstructor
public class JWTFilter extends OncePerRequestFilter {
    private final JwtUtil jwtUtil;
    private final AuthorizationSFC authService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");
        String account_id = null;
        String token = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader;
            String authToken = token.substring(7);
            account_id = jwtUtil.getId(authToken);
            AuthenticateResponseDto authDTO = AuthenticateResponseDto.builder()
                    .accessToken(authToken)
                    .build();
            Boolean verificationSuccess = authService.tokenVerify(authDTO);
            if (verificationSuccess == null || !verificationSuccess) {
                try {
                    throw new UnauthorizedException("authorization is failed.");
                } catch (UnauthorizedException e) {
                    throw new RuntimeException(e);
                }
            }

        }

        if (request.getRequestURI().equals("/api/v1/streaming/ws")) {
            final Cookie[] cookies = request.getCookies();
            if (cookies != null) {
                for (Cookie cookie : cookies) {
                    if (cookie.getName().equals("jwt")) {
                        account_id = jwtUtil.getId(cookie.getValue()); // не забыть включить
                        token = "Bearer " + cookie.getValue();
                    }
                }
            }
        }
        if (account_id != null && token != null) {
            UsernamePasswordAuthenticationToken springContextToken;
            springContextToken = new UsernamePasswordAuthenticationToken(account_id, token);
            SecurityContextHolder.getContext().setAuthentication(springContextToken);
        }

        filterChain.doFilter(request, response);
    }

}


