package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.UserWhoReadsMessageDto;
import com.engly.engly_server.service.common.MessageReadService;
import com.engly.engly_server.service.common.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
@Tag(name = "08. Messages", description = "APIs for retrieving chat messages within a room.")
@SecurityRequirement(name = "bearerAuth")
public class MessageController {
    private final MessageService messageService;
    private final MessageReadService messageReadService;

    public MessageController(MessageService messageService, MessageReadService messageReadService){
        this.messageService = messageService;
        this.messageReadService = messageReadService;
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
    public ResponseEntity<Page<MessagesDto>> findAllMessageInCurrentRoom(@PathVariable String roomId,
                                                                         @ParameterObject @PageableDefault(page = 0, size = 8,
                                                                                                    sort = {"created_at"}, direction = Sort.Direction.ASC) Pageable pageable,
                                                                         @RequestParam String keyString) {
        return ResponseEntity.ok(messageService.findAllMessagesContainingKeyString(roomId, keyString, pageable));
    }

    @Operation(
            summary = "Get all users who have read a specific message",
            description = "Retrieves a paginated list of users that have marked a specific message as read.",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Successfully retrieved the list of users."),
                    @ApiResponse(responseCode = "404", description = "The message with the specified ID was not found.")
            }
    )
    @GetMapping("/{messageId}/readers")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<Page<UserWhoReadsMessageDto>> findAllUsersWhoReadMessage(
            @PathVariable String messageId,
            @ParameterObject @PageableDefault(page = 0, size = 8,
                    sort = {"read_at"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(messageReadService.getUsersWhoReadMessage(messageId, pageable));
    }

    @Operation(
            summary = "Get all messages in a room (paginated)",
            description = """
                    Retrieves a paginated list of all messages within a specific chat room.
                    The response is structured according to Native query, including page metadata.
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
    @GetMapping("/current-room/native/{roomId}")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<Page<MessagesDto>> findAllAvailableMessagesByRoomId(@PathVariable String roomId,
                                                                                @ParameterObject @PageableDefault(page = 0, size = 8,
                                                                                        sort = {"created_at"}, direction = Sort.Direction.ASC) Pageable pageable) {
        return ResponseEntity.ok(messageService.findAllMessageInCurrentRoomNative(roomId, pageable));
    }
}
