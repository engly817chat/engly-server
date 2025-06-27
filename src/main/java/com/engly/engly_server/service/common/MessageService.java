package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.dto.create.MessageRequestDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;


public interface MessageService {
    MessagesDto sendMessage(MessageRequestDto messageRequestDto);

    MessagesDto deleteMessage(String id);

    MessagesDto findById(String id);

    MessagesDto editMessage(String id, String content);

    Page<MessagesDto> findAllMessageInCurrentRoom(String id, Pageable pageable);

    Page<MessagesDto> findAllMessagesContainingKeyString(String roomId, String keyString, Pageable pageable);
    List<UsersDto> findUsersWhoReadMessage(String messageId);
}
