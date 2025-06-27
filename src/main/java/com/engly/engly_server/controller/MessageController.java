package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.service.common.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/message")
@Tag(name = "08. Messages", description = "APIs for retrieving chat messages within a room.")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @Operation(
            summary = "Get all messages in a room (paginated)",
            description = """
                    Retrieves a paginated list of all messages within a specific chat room.
                    The response is structured according to Spring HATEOAS `PagedModel`, including page metadata and navigation links.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "A paginated list of messages.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_READ'.", content = @Content),
    })
    @GetMapping("/current-room/{roomId}")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<PagedModel<EntityModel<MessagesDto>>> findAllMessageInCurrentRoom(@PathVariable String roomId,
                                                                                            @ParameterObject @PageableDefault(page = 0, size = 8,
                                                                                                    sort = {"createdAt"}, direction = Sort.Direction.ASC) Pageable pageable,
                                                                                            PagedResourcesAssembler<MessagesDto> assembler) {
        final var messages = messageService.findAllMessageInCurrentRoom(roomId, pageable);
        return ResponseEntity.ok(assembler.toModel(messages));
    }

    @Operation(
            summary = "Search for messages in a room (paginated)",
            description = """
                    Retrieves a paginated list of messages within a specific room that contain a given search string.
                    The search is typically case-insensitive.
                    The response is also a `PagedModel`.
                    """
    )
    @ApiResponses({
            @ApiResponse(
                    responseCode = "200",
                    description = "A paginated list of matching messages.",
                    content = @Content
            ),
            @ApiResponse(responseCode = "400", description = "Bad Request. The 'search' query parameter is missing or empty.", content = @Content),
            @ApiResponse(responseCode = "403", description = "Forbidden. User does not have 'SCOPE_READ'.", content = @Content),
    })
    @GetMapping("/current-room/{roomId}/by-keyString")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<PagedModel<EntityModel<MessagesDto>>> findAllMessageInCurrentRoom(@PathVariable String roomId,
                                                                                            @ParameterObject @PageableDefault(page = 0, size = 8,
                                                                                                    sort = {"createdAt"}, direction = Sort.Direction.ASC) Pageable pageable,
                                                                                            @RequestParam String keyString,
                                                                                            PagedResourcesAssembler<MessagesDto> assembler) {
        final var messages = messageService.findAllMessagesContainingKeyString(roomId, keyString, pageable);
        return ResponseEntity.ok(assembler.toModel(messages));
    }

    @Operation(
            summary = "Retrieving a list of users who read message",
            description = """
                        roomId id of the room where to find
                        page starts from 0 and more
                        size is the amount of messages retrieves
                        messageId is a id of message
                        If the message doesn't exist it should return 409
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Все виконано успішно"),
                    @ApiResponse(responseCode = "409", description = "Повідомлення не існує")
            }
    )
    @GetMapping("/find/users/who-read/{messageId}/by-message-id")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<List<UsersDto>> findAllUsersWhoReadMessage(@PathVariable String messageId) {
        final var users = messageService.findUsersWhoReadMessage(messageId);
        return ResponseEntity.ok(users);
    }
}
