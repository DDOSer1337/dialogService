package dialogService.services.logic;

import dialogService.controller.feign.FriendSFC;
import dialogService.exeptionHandler.exceptions.BadRequestException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class AvailabilityСheck {
    private final FriendSFC friendSFC;

    // не работает из-за ошибки со стороны friends
    public boolean isBlocked(UUID user2) {
        //boolean isBlock = friendSFC.isBlocked().contains(user2);
        if (false) {
            log.info("User " + "does not have" + " access");
            try {
                throw new BadRequestException("User " + "does not have" + " access");
            } catch (BadRequestException e) {
                throw new RuntimeException(e);
            }
        }
        return false;
    }
}
