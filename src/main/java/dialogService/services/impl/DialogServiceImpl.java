package dialogService.services.impl;

import dialogService.dto.DialogDTO;
import dialogService.dto.MessageShortDTO;
import dialogService.exeptionHandler.exceptions.BadRequestException;
import dialogService.model.Dialog;
import dialogService.model.Message;
import dialogService.repository.DialogRepository;
import dialogService.repository.MessageRepository;
import dialogService.services.interfaces.DialogService;
import dialogService.services.interfaces.KafkaDialogService;
import dialogService.services.logic.AvailabilityСheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static dialogService.services.impl.MessageServiceImpl.getPageable;

@Slf4j
@Service
@RequiredArgsConstructor
public class DialogServiceImpl implements DialogService {
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final AvailabilityСheck availabilityСheck;
    private final KafkaDialogService kafkaDialogService;
    private final AuthenticationService authenticationService;

    @Override
    public Page<Dialog> getDialogs(PageRequest pageRequest, String sort) {
        log.info("get dialogs by user: " + authenticationService.getCurrentUserId());
        return dialogRepository.findByConversationPartner1OrAndConversationPartner2(
                authenticationService.getCurrentUserId(),
                authenticationService.getCurrentUserId(),
                createPageable(sort,pageRequest));
    }

    @Override
    @Transactional
    public void updateDialogStatus(UUID dialogId) {
        log.info("changeDialogStatus : " + dialogId);
        dialogRepository.changeMessageStatus(dialogId, authenticationService.getCurrentUserId());
        dialogRepository.changeDialogStatus(dialogId);
    }

    @Override
    public DialogDTO getRecipientId(UUID RecipientId) throws BadRequestException {
        return getOrCreateDialog(RecipientId);
    }

    @Override
    public DialogDTO getOrCreateDialog(UUID RecipientId) throws BadRequestException {
        if (availabilityСheck.isBlocked(RecipientId)) {
            log.info("user is blocked: " + authenticationService.getCurrentUserId());
            throw new BadRequestException("User is blocked");
        }
        UUID currentUserId = authenticationService.getCurrentUserId();
        int checkComparisonResul = currentUserId.compareTo(RecipientId);
        boolean checkComparisonResult = checkComparisonResul > 0;
        UUID id1 = checkComparisonResult ? RecipientId : currentUserId;
        UUID id2 = checkComparisonResult ? currentUserId : RecipientId;
        Dialog dialog = dialogRepository.findByConversationPartner1AndConversationPartner2(id1, id2);
        if (dialog == null){
            dialog = dialogRepository.findByConversationPartner1AndConversationPartner2(id2,id1);
        }
        return dialog != null ? get(id1,id2, dialog) : create(id1,id2);
    }

    private DialogDTO get(UUID uu1,UUID uu2, Dialog dialog) {
        UUID currentUserId = authenticationService.getCurrentUserId();
        log.info("get dialog with users: " + uu1 + " and " + uu2);
        int countUnreadMessage = messageRepository.getCountUnreadMessageInDialog(currentUserId, dialog.getId()).orElse(0);
        Page<Message> message = messageRepository.findByDialog(dialog.getId(), createPageable("time,Desc",PageRequest.of(0,10) ));

        MessageShortDTO messageShortDTO = getMessageShortDTO(message);
        return new DialogDTO(dialog.getId(), dialog.getConversationPartner1(),
                dialog.getConversationPartner2(), false,
                countUnreadMessage, messageShortDTO);
    }

    private MessageShortDTO getMessageShortDTO(Page<Message> message) {
        MessageShortDTO messageShortDTO = new MessageShortDTO();

        if (!message.getContent().isEmpty()) {
            messageShortDTO.setConversationPartner1(message.getContent().get(0).getAuthor());
            messageShortDTO.setConversationPartner2(message.getContent().get(0).getRecipient());
            messageShortDTO.setMessageText(message.getContent().get(0).getMessageText());
            messageShortDTO.setId(message.getContent().get(0).getId());
            messageShortDTO.setTime(message.getContent().get(0).getTime());
            messageShortDTO.setDelete(message.getContent().get(0).isDeleted());
            messageShortDTO.setDialogId(message.getContent().get(0).getDialog());
            messageShortDTO.setReadStatus(message.getContent().get(0).isRead()?messageStatus.READ.toString():messageStatus.SENT.toString());
        }
        log.info("get message short dto");
        return messageShortDTO;
    }

    private DialogDTO create(UUID uu1,UUID uu2) {
        log.info("create dialog with users: " + uu1 + " and " + uu2);
        Dialog dialog = new Dialog();
        dialog.setConversationPartner1(uu1);
        dialog.setConversationPartner2(uu2);
        dialog.setUnreadCount(0);
        dialog = dialogRepository.save(dialog);
        kafkaDialogService.createDialog("create dialog with users: " + uu1 + " and " + uu2);
        return get(uu1,uu2, dialog);
    }

    private Pageable createPageable(String sort,PageRequest pageRequest) {
        return getPageable(sort,pageRequest);
    }
}