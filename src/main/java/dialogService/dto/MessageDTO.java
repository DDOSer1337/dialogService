package dialogService.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class MessageDTO {
    UUID id;
    UUID conversationPartner1;
    UUID conversationPartner2;
    UUID dialogID;
    String messageText;
    String readStatus;
    String time;
    boolean isDeleted;

}
