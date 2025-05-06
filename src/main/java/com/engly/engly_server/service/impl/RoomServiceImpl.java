package com.engly.engly_server.service.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.RoomRequestDto;
import com.engly.engly_server.models.dto.update.RoomUpdateRequest;
import com.engly.engly_server.repo.CategoriesRepo;
import com.engly.engly_server.repo.RoomRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.RoomService;
import com.engly.engly_server.mapper.RoomMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;

import static com.engly.engly_server.utils.fieldvalidation.FieldUtil.isValid;

@Service
@Slf4j
@RequiredArgsConstructor
public class RoomServiceImpl implements RoomService {
    private final RoomRepo roomRepo;
    private final UserRepo userRepo;
    private final CategoriesRepo categoriesRepo;
    private final SecurityService service;

    @Override
    @Transactional
    public RoomsDto createRoom(CategoryType name, RoomRequestDto roomRequestDto) {
        final var username = service.getCurrentUserEmail();
        final var category = categoriesRepo.findByName(name)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        return userRepo.findByEmail(username)
                .map(creator -> RoomMapper.INSTANCE.roomToDto(roomRepo.save(
                        Rooms.builder()
                                .creator(creator)
                                .createdAt(Instant.now())
                                .category(category)
                                .description(roomRequestDto.description())
                                .name(roomRequestDto.name())
                                .build()
                )))
                .orElseThrow(() -> new NotFoundException("User not found"));
    }

    @Override
    @Transactional(readOnly = true)
    public Page<RoomsDto> findAllRoomsByCategoryType(CategoryType category, Pageable pageable) {
        return roomRepo.findAllByCategory_Name(category, pageable)
                .map(RoomMapper.INSTANCE::roomToDto);
    }

    @Override
    public void deleteRoomById(String id) {
        var room = roomRepo.findById(id)
                .orElseThrow(() -> new NotFoundException("You can't delete this room"));
        roomRepo.delete(room);
    }

    @Override
    public RoomsDto updateRoom(String id, RoomUpdateRequest request) {
        return roomRepo.findById(id)
                .map(room -> {
                    if (isValid(request.newCategory())) room.setCategory(categoriesRepo.findByName(request.newCategory())
                            .orElseThrow(() -> new NotFoundException("Category not found")));

                    if (isValid(request.updateCreatorByEmail()))
                        room.setCreator(userRepo.findByEmail(request.updateCreatorByEmail())
                                .orElseThrow(() -> new NotFoundException("Creator not found")));

                    if (isValid(request.description())) room.setDescription(request.description());
                    if (isValid(request.name())) room.setName(request.name());
                    return RoomMapper.INSTANCE.roomToDto(roomRepo.save(room));
                })
                .orElseThrow(() -> new NotFoundException("Room not found"));
    }

    @Override
    public Page<RoomsDto> findAllRoomsContainingKeyString(String keyString, Pageable pageable) {
        return roomRepo.findAllRoomsContainingKeyString(keyString, pageable)
                .map(RoomMapper.INSTANCE::roomToDto);
    }
}
