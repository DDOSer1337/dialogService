package dialogService.controller;

import dialogService.dto.DialogDTO;
import dialogService.dto.MessageDTO;
import dialogService.dto.UnreadCountDTO;
import dialogService.exeptionHandler.exceptions.BadRequestException;
import dialogService.model.Dialog;
import dialogService.model.Message;
import dialogService.services.interfaces.*;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class ApiControllerImpl implements ApiController {
    private final MessageService messageService;
    private final DialogService dialogService;

    @Override
    public Page<Dialog> getDialogs(int page,int size, String sort) throws BadRequestException {
        return dialogService.getDialogs(PageRequest.of(page, size),sort);
    }

    @Override
    public void updateStatusDialogs(UUID dialogId) {
        dialogService.updateDialogStatus(dialogId);
    }

    @Override
    public DialogDTO createDialog(UUID user2) throws BadRequestException {
        return dialogService.getOrCreateDialog(user2);
    }

    @Override
    public DialogDTO getRecipientId(UUID recipientId) throws BadRequestException {
        return dialogService.getRecipientId(recipientId);
    }

    @Override
    public MessageDTO postMessage(String text, UUID user2) throws BadRequestException {
        return messageService.createMessage(user2, text);
    }

    @Override
    public Page<Message> getMessages(UUID recipientId, int page, int size, String sort) {
        return messageService.getMessages(recipientId,PageRequest.of(page, size), sort);
    }

    @Override
    public UnreadCountDTO getUnreadCountMessages() {
        return messageService.getCountUnreadMessageInDialog();
    }
}
