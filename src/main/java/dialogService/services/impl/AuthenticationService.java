package dialogService.services.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AuthenticationService {
    public UUID getCurrentUserId() {
        return getContextAccountID();
    }

    private UUID getContextAccountID() {
        return UUID.fromString(
                (String) SecurityContextHolder
                        .getContext()
                        .getAuthentication()
                        .getPrincipal()
        );
    }

    private String getContextToken() {
        return (String) SecurityContextHolder.getContext().getAuthentication().getCredentials();
    }
}
