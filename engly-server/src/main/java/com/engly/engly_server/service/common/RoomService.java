package com.engly.engly_server.service.common;

import com.engly.engly_server.models.dto.ApiResponse;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.RoomRequestDto;
import com.engly.engly_server.models.dto.update.RoomUpdateRequest;

import java.util.List;

public interface RoomService {
    RoomsDto createRoom(CategoryType name, RoomRequestDto roomRequestDto);

    List<RoomsDto> findAllRoomsByCategoryType(CategoryType category);

    ApiResponse deleteRoomById(String id);

    RoomsDto updateRoom(String id, RoomUpdateRequest request);

    List<RoomsDto> findAllRoomsContainingKeyString(String keyString);

    List<RoomsDto> findAllRoomsByCategoryTypeContainingKeyString(CategoryType categoryType, String keyString);
}
