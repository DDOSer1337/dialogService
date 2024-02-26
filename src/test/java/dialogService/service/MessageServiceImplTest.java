package dialogService.service;
import dialogService.JWUtil.JwtUtil;
import dialogService.controller.feign.AccountSFC;
import dialogService.controller.feign.AuthorizationSFC;
import dialogService.model.Dialog;
import dialogService.model.Message;
import dialogService.repository.DialogRepository;
import dialogService.repository.MessageRepository;
import dialogService.services.impl.AuthenticationService;
import dialogService.services.interfaces.DialogService;
import dialogService.services.interfaces.MessageService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.Boolean.TRUE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
public class MessageServiceImplTest {
    @MockBean
    AuthorizationSFC authSFC;
    @MockBean
    AccountSFC accountSFC;
    @MockBean
    AuthenticationService authenticationService;
    @MockBean
    JwtUtil jwtUtil;

    @Autowired
    DialogService dialogService;
    @Autowired
    DialogRepository dialogRepository;
    @Autowired
    MessageRepository messageRepository;
    @Autowired
    MessageService messageService;

    @Autowired
    MockMvc mockMvc;

    private static String header = "Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6W3siaWQiOjIwLCJuYW1lIjoiUk9MRV9VU0VSIiwiYXV0aG9yaXR5IjoiUk9MRV9VU0VSIn1dLCJpZCI6IjA0YTQwMWFmLWRiMGYtNDhjNC04NTIwLWM5M2JlOTI0YjM2YSIsInN1YiI6InNraWJpZGl0dWFsZXRAZ21haWwuY29tIiwiaWF0IjoxNzA3NTIxODM2LCJleHAiOjE3MDc2MDgyMzZ9.hhTF7i_t_nv06g81q1sLjjsGNEStAvdPvIqd10R1A9lBarQ4xoh8RKfECHMeNsG2bYvLg_k-KBQuvS2aXKtR-Q";


    @BeforeEach
    void setUp() {
        when(authSFC.tokenVerify(Mockito.any())).thenReturn(TRUE);
        when(authenticationService.getCurrentUserId()).thenReturn(UUID.fromString("04a401af-db0f-48c4-8520-c93be924b36a"));
        when(jwtUtil.getId(Mockito.any())).thenReturn("04a401af-db0f-48c4-8520-c93be924b36a");
    }

    @Test
    @DisplayName("POST /dialogs/messages Создание сообщения со статусом 200 ОК с возвратом MessageDTO")
    void postMessage() throws Exception{
        UUID conversationPartner = UUID.randomUUID();
        createDialog(conversationPartner);
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/dialogs/messages?text=sometext123&conversationPartner="+conversationPartner)
                .header("AUTHORIZATION", header)
        ).andExpect(status().isOk());
    }
    @Test
    @DisplayName("GET /dialogs/messages получение списка всех, в указанном диалоге, сообщений со статусом 200 OK с ответом Page<Message>")
    void getExistingMessages() throws Exception{
        UUID conversationPartner = UUID.randomUUID();
        Dialog dialog =createDialog(conversationPartner);
        createMessage(authenticationService.getCurrentUserId(),conversationPartner,dialog.getId());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/dialogs/messages?sort=time,desc&recipientId="+conversationPartner+"&page=0&size=10")
                .header("AUTHORIZATION", header)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /dialogs/messages получение пустого списка, в указанном диалоге, сообщений со статусом 200 OK с ответом Page<Message>")
    void getNonExistingMessages() throws Exception{
        UUID conversationPartner = UUID.randomUUID();
        createDialog(conversationPartner);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/dialogs/messages?sort=time,desc&recipientId="+conversationPartner+"&page=0&size=10")
                .header("AUTHORIZATION", header)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /dialogs/unread получение данных о кол-ве непрочитанных сообщений" +
            "со статусом 200 ОК с ответом UnreadCountDTO ")
    void getUnreadCountMessage() throws Exception{
        UUID conversationPartner = UUID.randomUUID();
        Dialog dialog =createDialog(conversationPartner);
        createMessage(conversationPartner,authenticationService.getCurrentUserId(),dialog.getId());
        createMessage(conversationPartner,authenticationService.getCurrentUserId(),dialog.getId());
        createMessage(authenticationService.getCurrentUserId(),conversationPartner,dialog.getId());
        createMessage(authenticationService.getCurrentUserId(),conversationPartner,dialog.getId());
        createMessage(authenticationService.getCurrentUserId(),conversationPartner,dialog.getId());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/dialogs/unread")
                .header("AUTHORIZATION", header)
        ).andExpect(status().isOk());
    }
    private Dialog createDialog(UUID conversationPartner) {
        dialogRepository.deleteAll();
        Dialog dialog = new Dialog();
        UUID currentUserId = authenticationService.getCurrentUserId();
        int checkComparisonResul = currentUserId.compareTo(conversationPartner);
        boolean checkComparisonResult = checkComparisonResul > 0;
        UUID id1 = checkComparisonResult ? conversationPartner : currentUserId;
        UUID id2 = checkComparisonResult ? currentUserId : conversationPartner;
        dialog.setConversationPartner1(id1);
        dialog.setConversationPartner2(id2);
        dialog.setUnreadCount(0);
        return dialogRepository.save(dialog);
    }

    private void createMessage(UUID author, UUID conversationPartner, UUID dialogId) {
        Message message = new Message();
        message.setMessageText("Hello");
        message.setAuthor(author);
        message.setRead(false);
        message.setDeleted(false);
        message.setEdited(false);
        message.setTime(LocalDateTime.now());
        message.setRecipient(conversationPartner);
        message.setDialog(dialogId);
        messageRepository.save(message);
    }
}
