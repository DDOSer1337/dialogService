package dialogService.dto;

import lombok.Getter;
import lombok.Setter;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class MessageShortDTO {
    UUID id;
    UUID conversationPartner1;
    UUID conversationPartner2;
    String messageText;
    LocalDateTime time;
    boolean isDelete;
    String readStatus;
    UUID dialogId;
}
