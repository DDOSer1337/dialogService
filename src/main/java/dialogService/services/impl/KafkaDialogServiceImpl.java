package dialogService.services.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import dialogService.dto.DialogDTO;
import dialogService.dto.MessageDTO;
import dialogService.services.interfaces.KafkaDialogService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.javapro_team44.dto.account.AccountDTO;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaDialogServiceImpl implements KafkaDialogService {

    private final ObjectMapper mapper;

    @Override
    public void createDialog(String message) {
        try {
            DialogDTO dialogDTO = mapper.readValue(message, DialogDTO.class);
            log.info("New Dialog has been created from kafka. Dialog ID: ");
        } catch (JsonProcessingException e) {
            log.error("Error occur while receiving new user creation from kafka.");
        }
    }

    @Override
    public void createMessage(String message) {
        try {
            MessageDTO messageDTO = mapper.readValue(message, MessageDTO.class);
            log.info("New MessageDTO has been created from kafka. Message ID: ");
        } catch (JsonProcessingException e) {
            log.error("Error occur while receiving new user creation from kafka.");
        }
    }
}
