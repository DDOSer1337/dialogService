package dialogService.services.impl;

import dialogService.dto.MessageDTO;
import dialogService.dto.UnreadCountDTO;
import dialogService.exeptionHandler.exceptions.BadRequestException;
import dialogService.model.Dialog;
import dialogService.model.Message;
import dialogService.repository.DialogRepository;
import dialogService.repository.MessageRepository;
import dialogService.services.interfaces.MessageService;
import dialogService.services.logic.AvailabilityСheck;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.json.JSONObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.socket.TextMessage;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
public class MessageServiceImpl implements MessageService {
    private final DialogRepository dialogRepository;
    private final MessageRepository messageRepository;
    private final AvailabilityСheck availabilityСheck;
    private final AuthenticationService authenticationService;

    @Override
    public MessageDTO createMessage(UUID user2, String text) throws BadRequestException {
        System.out.println("createMessage");
        if (availabilityСheck.isBlocked(user2)) {
            throw new BadRequestException("User is blocked");
        }
        UUID currentUserId = authenticationService.getCurrentUserId();
        boolean checkComparisonResult = currentUserId.compareTo(user2) > 0;
        UUID id1 = checkComparisonResult ? user2 : currentUserId;
        UUID id2 = checkComparisonResult ? currentUserId : user2;

        Dialog dialog = dialogRepository.findByConversationPartner1AndConversationPartner2(id1, id2);
        if (dialog == null) {
            dialog = dialogRepository.findByConversationPartner1AndConversationPartner2(id2, id1);
        }
        Message message = new Message(text, currentUserId, false, false, false, LocalDateTime.now(), dialog.getId(), user2);
        message = messageRepository.save(message);
        return new MessageDTO(message.getId(),
                currentUserId, user2, dialog.getId(), LocalDateTime.now().toString(), text,
                message.isRead() ? messageStatus.READ.toString() : messageStatus.SENT.toString(), false);
    }

    @Override
    @Transactional
    public UnreadCountDTO getCountUnreadMessageInDialog() {
        System.out.println("getCountUnreadMessageInDialog");
        UUID currentUserId = authenticationService.getCurrentUserId();
        log.info("a request for dialogue with id {} was received", currentUserId);
        UnreadCountDTO unreadCountDTO = new UnreadCountDTO();
        unreadCountDTO.setCount(Math.toIntExact(messageRepository.getCountUnreadMessage(currentUserId).orElse(0L)));
        return unreadCountDTO;
    }

    @Override
    public Page<Message> getMessages(UUID recipientId, PageRequest pageRequest, String sort) {
        UUID currentUserId = authenticationService.getCurrentUserId();
        boolean checkComparisonResult = currentUserId.compareTo(recipientId) > 0;
        UUID id1 = checkComparisonResult ? recipientId : currentUserId;
        UUID id2 = checkComparisonResult ? currentUserId : recipientId;
        Dialog dialog = dialogRepository.findByConversationPartner1AndConversationPartner2(id1, id2);
        if (dialog == null) {
            dialog = dialogRepository.findByConversationPartner1AndConversationPartner2(id2, id1);
        }
        return dialog != null ? messageRepository.findByDialog(dialog.getId(), createPageable(sort, 0,10)) : null;
    }

    private Pageable createPageable(String sort,int page, int size) {
        return getPageable(sort,PageRequest.of(page,size));
    }


    static Pageable getPageable(String sort,PageRequest pageRequest) {
        Pageable pageable;
        if (sort != null && !sort.isEmpty()) {
            String[] sortParams = sort.split(",");
            if (sortParams.length > 0) {
                String sortBy = sortParams[0];
                String sortOrder = sortParams.length > 1 ? sortParams[1] : "asc";
                pageable = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize(), Sort.Direction.valueOf(sortOrder.toUpperCase()), sortBy);
            } else {
                pageable = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize());
            }
        } else {
            pageable = PageRequest.of(pageRequest.getPageNumber(), pageRequest.getPageSize());
        }
        return pageable;
    }

    public void handleSocketMessage(TextMessage socketMessage) {
        JSONObject jsonSocketMessage = new JSONObject(socketMessage.getPayload());
        JSONObject jsonMessageDto = (jsonSocketMessage.getJSONObject("data"));
//        dialogRepository.changeMessageStatus(UUID.fromString(jsonMessageDto.getString("dialogId")));
//        dialogRepository.changeDialogStatus(UUID.fromString(jsonMessageDto.getString("dialogId")));
        Message message = new Message(
                jsonMessageDto.getString("messageText"),
                UUID.fromString(jsonMessageDto.getString("conversationPartner1")),
                false, false, false,
                LocalDateTime.parse(jsonMessageDto.getString("time"), DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSSSS'Z'")),
                UUID.fromString(jsonMessageDto.getString("dialogId")),
                UUID.fromString(jsonMessageDto.getString("conversationPartner2"))
        );
        message = messageRepository.save(message);
//        createNotification(message);
        System.out.println(socketMessage);
    }
}

