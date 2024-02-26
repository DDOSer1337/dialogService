package dialogService.controller;

import dialogService.dto.DialogDTO;
import dialogService.dto.MessageDTO;
import dialogService.dto.UnreadCountDTO;
import dialogService.exeptionHandler.exceptions.BadRequestException;
import dialogService.model.Dialog;
import dialogService.model.Message;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.data.domain.Page;
import org.springframework.web.bind.annotation.*;


import java.util.UUID;

@RestController
@RequestMapping("${application.controllers.dialogService.mapping}"+"/dialogs")
@Tag(name = "${application.controllers.dialogService.name}", description = "${application.controllers.dialogService.description}")
public interface ApiController {
    @Operation(
            summary = "Get dialogs by UserID",
            description = "Get dialogs by userId, Return page with dialogs",
            tags = {"dialog"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content()),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content()),
    })


    @GetMapping()
    Page<Dialog> getDialogs(@RequestParam(value = "page", required = false) int page,
                            @RequestParam(value = "size", required = false,defaultValue = "10") int size,
                            @RequestParam(value = "sort", required = false) String sort) throws BadRequestException;
    @Operation(
            summary = "Change dialog status on read",
            description = "",
            tags = {"dialog"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content()),
            @ApiResponse(
                    responseCode = "400", content = @Content()),
            @ApiResponse(
                    responseCode = "401", content = @Content())
    })

    @PutMapping("/{dialogId}")
    void updateStatusDialogs(
            @PathVariable("dialogId") UUID dialogId);

    @Operation(
            summary = "Creates a dialogue between users",
            description = "",
            tags = {"dialog"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content()),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content())
    })
    //+
    @PostMapping("/createDialog")
    DialogDTO createDialog(
            @RequestParam(value = "conversationPartner") UUID user2) throws BadRequestException;


    @Operation(
            summary = "idk",
            description = "idk",
            tags = {"dialog"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content()),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content())
    })
    @GetMapping("/recipientId/{id}")
    DialogDTO getRecipientId(
            @PathVariable("id") UUID userID) throws BadRequestException;

    @Operation(
            summary = "Create message between users",
            description = "",
            tags = {"message"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content(schema = @Schema())),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content(schema = @Schema()))
    })
    @PostMapping("/messages")
    MessageDTO postMessage(
            @RequestParam(value = "text") String text,
            @RequestParam(value = "conversationPartner") UUID user2) throws BadRequestException;

    @Operation(
            summary = "returns all received messages",
            description = "returns all received messages with the specified conditions",
            tags = {"message"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content()),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content())
    })
    @GetMapping("/messages")
    Page<Message> getMessages(
            @RequestParam(value = "recipientId") UUID recipientId,
            @RequestParam(value = "page", required = false, defaultValue = "0") int page,
            @RequestParam(value = "size", required = false, defaultValue = "10") int size,
            @RequestParam(value = "sort", required = false) String sort);

    @Operation(
            summary = "get count unread messages",
            description = "",
            tags = {"message"}
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    content = @Content(schema = @Schema(implementation = Page.class))),
            @ApiResponse(
                    responseCode = "400",
                    content = @Content()),
            @ApiResponse(
                    responseCode = "401",
                    content = @Content())
    })

    @GetMapping("/unread")
    UnreadCountDTO getUnreadCountMessages();
}