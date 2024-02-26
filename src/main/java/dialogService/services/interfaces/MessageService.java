package dialogService.services.interfaces;

import dialogService.dto.MessageDTO;
import dialogService.dto.UnreadCountDTO;
import dialogService.exeptionHandler.exceptions.BadRequestException;
import dialogService.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.UUID;

public interface MessageService{
    MessageDTO createMessage(UUID user2, String text) throws BadRequestException;
    UnreadCountDTO getCountUnreadMessageInDialog();
    Page<Message> getMessages(UUID recipientId, PageRequest pageRequest, String sort);
}
