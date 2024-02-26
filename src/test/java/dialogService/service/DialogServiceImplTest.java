package dialogService.service;

import dialogService.JWUtil.JwtUtil;
import dialogService.controller.feign.AccountSFC;
import dialogService.controller.feign.AuthorizationSFC;
import dialogService.controller.feign.FriendSFC;
import dialogService.model.Dialog;
import dialogService.model.Message;
import dialogService.repository.DialogRepository;
import dialogService.repository.MessageRepository;
import dialogService.services.impl.AuthenticationService;
import dialogService.services.interfaces.DialogService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.time.LocalDateTime;
import java.util.UUID;

import static java.lang.Boolean.TRUE;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Testcontainers
@SpringBootTest
@AutoConfigureMockMvc
class DialogServiceImplTest {

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
    MockMvc mockMvc;

    private static String header = "Bearer " + "eyJhbGciOiJIUzUxMiJ9.eyJyb2xlcyI6W3siaWQiOjIwLCJuYW1lIjoiUk9MRV9VU0VSIiwiYXV0aG9yaXR5IjoiUk9MRV9VU0VSIn1dLCJpZCI6IjA0YTQwMWFmLWRiMGYtNDhjNC04NTIwLWM5M2JlOTI0YjM2YSIsInN1YiI6InNraWJpZGl0dWFsZXRAZ21haWwuY29tIiwiaWF0IjoxNzA3NTIxODM2LCJleHAiOjE3MDc2MDgyMzZ9.hhTF7i_t_nv06g81q1sLjjsGNEStAvdPvIqd10R1A9lBarQ4xoh8RKfECHMeNsG2bYvLg_k-KBQuvS2aXKtR-Q";


    @BeforeEach
    void setUp() {
        when(authSFC.tokenVerify(Mockito.any())).thenReturn(TRUE);
        when(authenticationService.getCurrentUserId()).thenReturn(UUID.fromString("04a401af-db0f-48c4-8520-c93be924b36a"));
        when(jwtUtil.getId(Mockito.any())).thenReturn("04a401af-db0f-48c4-8520-c93be924b36a");
    }

    @Test
    @DisplayName("POST /dialogs/createDialog создание диалога со статусом 200 ОК с возвратом DialogDTO")
    void createDialogTest() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                .post("/api/v1/dialogs/createDialog?conversationPartner=9e815a99-aa49-40cd-a61d-6173e44fd42f")
                .header("AUTHORIZATION", header)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("GET /dialogs возвращение Page<Dialog> диалогов со статусом 200 ОК")
    void GetExistingDialogTest() throws Exception {
        UUID recipientId = UUID.fromString("3c5fb134-12a7-4843-9a93-9cf3ba906e65");
        Dialog dialog = createDialog(recipientId);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/dialogs?page=0&size=10")
                .header("AUTHORIZATION", header)
        ).andExpectAll(
                status().isOk(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON),
                content().json(getJsonDialogContent(dialog.getId(),dialog.getConversationPartner1(),dialog.getConversationPartner2()))
        );
    }
    @Test
    @DisplayName("GET /dialogs возвращение Page<Dialog> диалогов со статусом 200 ОК")
    void GetExistingDialogWithMessageTest() throws Exception {
        UUID recipientId = UUID.fromString("3c5fb134-12a7-4843-9a93-9cf3ba906e65");
        Dialog dialog =createDialog(recipientId);
        createMessage(authenticationService.getCurrentUserId(),recipientId,dialog.getId());
        createMessage(recipientId,authenticationService.getCurrentUserId(),dialog.getId());
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/dialogs?page=0&size=10")
                .header("AUTHORIZATION", header)
        ).andExpectAll(
                status().isOk(),
                content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
        );
    }


    @Test
    @DisplayName("GET /dialogs создание и возвращение Page<Dialog> диалогов со статусом 200 ОК")
    void GetNonExistingDialogTest() throws Exception {
        dialogRepository.deleteAll();
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/api/v1/dialogs?page=0&size=10")
                        .header("AUTHORIZATION", header))
                .andExpectAll(
                        status().isOk(),
                        content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON)
                );
    }

    // Не работает из-за того что сервисы не запущены
    @Test
    @DisplayName("GET /dialogs/recipientId/{id} получение данных о диалоге со статусом 200 ОК с возратом DialogDTO ")
    void getExistingDialogByRecipientIDTest() throws Exception {
        UUID recipientID = UUID.randomUUID();
        Dialog dialog = createDialog(recipientID);
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/dialogs/recipientId/" + recipientID)
                .header("AUTHORIZATION", header)
        ).andExpect(status().isOk())
                .andExpect(content().json(getJsonMessageContent(dialog.getId(),dialog.getConversationPartner1(),dialog.getConversationPartner2())));
    }



    @Test
    @DisplayName("GET /dialogs/recipientId/{id} Создание данных о диалоге по id recipientId 200 ОК с возратом DialogDTO")
    void getNonExistingDialogByRecipientIDTest() throws Exception  {
        UUID recipientID = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders
                .get("/api/v1/dialogs/recipientId/" + recipientID)
                .header("AUTHORIZATION", header)
        ).andExpect(status().isOk());
    }

    @Test
    @DisplayName("Put /dialogs/{dialogId} обновление статуса сообщений со статусом 200 ОК")
    void updateDialogStatus() throws Exception  {
        UUID recipientId = UUID.randomUUID();
        Dialog dialog = createDialog(recipientId);
        messageRepository.deleteAll();
        createMessage(authenticationService.getCurrentUserId(),recipientId,dialog.getId());
        mockMvc.perform(MockMvcRequestBuilders
                        .put("/api/v1/dialogs/"+dialog.getId())
                        .header("AUTHORIZATION", header))
                .andExpectAll(
                        status().isOk()
                );
    }
    private Dialog createDialog(UUID RecipientId) {
        dialogRepository.deleteAll();
        Dialog dialog = new Dialog();
        UUID currentUserId = authenticationService.getCurrentUserId();
        int checkComparisonResul = currentUserId.compareTo(RecipientId);
        boolean checkComparisonResult = checkComparisonResul > 0;
        UUID id1 = checkComparisonResult ? RecipientId : currentUserId;
        UUID id2 = checkComparisonResult ? currentUserId : RecipientId;
        dialog.setConversationPartner1(id1);
        dialog.setConversationPartner2(id2);
        dialog.setUnreadCount(0);
        return dialogRepository.save(dialog);
    }

    private Message createMessage(UUID author,UUID recipientId,UUID dialogId) {
        Message message = new Message("Hello",author,
                false,false,false,
                LocalDateTime.now(),dialogId,recipientId);
        return messageRepository.save(message);
    }
    private String getJsonDialogContent(UUID dialogID,UUID id1,UUID id2){
        return "{\n" +
                "    \"content\": [\n" +
                "        {\n" +
                "            \"id\": \""+dialogID+"\",\n" +
                "            \"conversationPartner1\": \""+id1+"\",\n" +
                "            \"conversationPartner2\": \""+id2+"\",\n" +
                "            \"unreadCount\": 0\n" +
                "        }\n" +
                "    ],\n" +
                "    \"pageable\": {\n" +
                "        \"pageNumber\": 0,\n" +
                "        \"pageSize\": 10,\n" +
                "        \"sort\": [\n],\n" +
                "        \"offset\": 0,\n" +
                "        \"unpaged\": false,\n" +
                "        \"paged\": true\n" +
                "    },\n" +
                "    \"last\": true,\n" +
                "    \"totalPages\": 1,\n" +
                "    \"totalElements\": 1,\n" +
                "    \"size\": 10,\n" +
                "    \"number\": 0,\n" +
                "    \"sort\": [\n" +
                "    ],\n" +
                "    \"first\": true,\n" +
                "    \"numberOfElements\": 1,\n" +
                "    \"empty\": false\n" +
                "}";
    }
    private String getJsonMessageContent(UUID dialogID,UUID id1,UUID id2){
        return "{\n" +
                "    \"id\": \""+dialogID+"\",\n" +
                "    \"conversationPartner1\": \""+id1+"\",\n" +
                "    \"conversationPartner2\": \""+id2+"\",\n" +
                "    \"unreadCount\": 0,\n" +
                "    \"lastMessage\": {\n" +
                "        \"id\": null,\n" +
                "        \"conversationPartner1\": null,\n" +
                "        \"conversationPartner2\": null,\n" +
                "        \"messageText\": null,\n" +
                "        \"time\": null,\n" +
                "        \"readStatus\": null,\n" +
                "        \"dialogId\": null,\n" +
                "        \"delete\": false\n" +
                "    },\n" +
                "    \"deleted\": false\n" +
                "}";
    }
}
