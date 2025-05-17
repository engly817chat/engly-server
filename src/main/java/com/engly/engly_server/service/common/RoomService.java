package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.RoomRequestDto;
import com.engly.engly_server.models.dto.update.RoomUpdateRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface RoomService {
    RoomsDto createRoom(CategoryType name, RoomRequestDto roomRequestDto);

    List<RoomsDto> findAllRoomsByCategoryType(CategoryType category);

    void deleteRoomById(String id);

    RoomsDto updateRoom(String id, RoomUpdateRequest request);

    List<RoomsDto> findAllRoomsContainingKeyString(String keyString);
}
