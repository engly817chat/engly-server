package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.create.MessageRequestDto;

import java.util.List;

public interface MessageService {
    MessagesDto sendMessage(MessageRequestDto messageRequestDto);

    MessagesDto deleteMessage(String id);

    MessagesDto editMessage(String id, String content);

    List<MessagesDto> findAllMessageInCurrentRoom(String id);

    List<MessagesDto> findAllMessagesContainingKeyString(String roomId, String keyString);
}
