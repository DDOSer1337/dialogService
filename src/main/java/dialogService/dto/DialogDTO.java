package dialogService.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DialogDTO {
    UUID id;
    UUID conversationPartner1;
    UUID conversationPartner2;
    boolean isDeleted;
    int unreadCount;
    MessageShortDTO lastMessage;
}
