package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.ChatParticipantsDto;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.entity.Users;
import com.engly.engly_server.models.enums.RoomRoles;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ChatParticipantsService {

    String NOT_FOUND_MESSAGE = "Participant with id %s not found";

    void addParticipant(Rooms rooms, Users user, RoomRoles role);

    void removeParticipant(String participantId);

    void updateRoleOfParticipant(String participantId, RoomRoles role);

    Page<ChatParticipantsDto> getParticipantsByRoomId(String roomId, Pageable pageable);
}
