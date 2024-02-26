package dialogService.services.interfaces;

import dialogService.dto.DialogDTO;
import dialogService.exeptionHandler.exceptions.BadRequestException;
import dialogService.model.Dialog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;
import java.util.UUID;

public interface DialogService {
    Page<Dialog> getDialogs(PageRequest pageRequest, String sort) throws BadRequestException;
    DialogDTO getOrCreateDialog(UUID user2) throws BadRequestException;
    void updateDialogStatus(UUID dialogId);
    DialogDTO getRecipientId(UUID recipientId) throws BadRequestException;
}
