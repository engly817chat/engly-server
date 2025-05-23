package com.engly.engly_server.controller;

import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.create.MessageRequestDto;
import com.engly.engly_server.service.common.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.PagedModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/message")
public class MessageController {
    private final MessageService messageService;

    public MessageController(MessageService messageService) {
        this.messageService = messageService;
    }

    @PostMapping("/send")
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    public ResponseEntity<MessagesDto> sendMessage(@RequestBody MessageRequestDto messageRequestDto) {
        return new ResponseEntity<>(messageService.sendMessage(messageRequestDto), HttpStatus.CREATED);
    }

    @PutMapping("/edit/{id}")
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    public ResponseEntity<MessagesDto> editMessage(@PathVariable String id,
                                                   @NotBlank(message = "Pls input some new message")
                                                   @Size(max = 200)
                                                   @RequestParam String content) {
        return new ResponseEntity<>(messageService.editMessage(id, content), HttpStatus.OK);
    }

    @GetMapping("/current-room/{roomId}")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<PagedModel<EntityModel<MessagesDto>>> findAllMessageInCurrentRoom(@PathVariable String roomId,
                                                                                            @ParameterObject @PageableDefault(page = 0, size = 8,
                                                                                                    sort = "createdDate,asc") Pageable pageable,
                                                                                            PagedResourcesAssembler<MessagesDto> assembler) {
        final var messages = messageService.findAllMessageInCurrentRoom(roomId);
        return ResponseEntity.ok(assembler.toModel(new PageImpl<>(messages, pageable, messages.size())));
    }

    @Operation(
            summary = "Retrieving a list of messages that contains a KeyString",
            description = """
                        roomId id of the room where to find
                        page starts from 0 and more
                        size is the amount of messages retrieves
                        keyString is a string that needs to be found
                        If the message doesn't contain a keyString it should not return it
                    \s""",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Посилання було надіслано на email"),
                    @ApiResponse(responseCode = "409", description = "Посилання не було надіслано або token не був згенерований коректно")
            }
    )
    @GetMapping("/current-room/{roomId}/by-keyString")
    @PreAuthorize("hasAuthority('SCOPE_READ')")
    public ResponseEntity<PagedModel<EntityModel<MessagesDto>>> findAllMessageInCurrentRoom(@PathVariable String roomId,
                                                                                            @ParameterObject @PageableDefault(page = 0, size = 8,
                                                                                                    sort = "createdAt,asc") Pageable pageable,
                                                                                            @RequestParam String keyString,
                                                                                            PagedResourcesAssembler<MessagesDto> assembler) {
        final var messages = messageService.findAllMessagesContainingKeyString(roomId, keyString);
        return ResponseEntity.ok(assembler.toModel(new PageImpl<>(messages, pageable, messages.size())));
    }


    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('SCOPE_WRITE')")
    public ResponseEntity<String> deleteMessage(@PathVariable String id) {
        messageService.deleteMessage(id);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build();
    }
}
