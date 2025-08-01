package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.EntityAlreadyExistsException;
import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.RoomMapper;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.dto.create.RoomRequest;
import com.engly.engly_server.models.dto.update.RoomUpdateRequest;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.enums.RoomRoles;
import com.engly.engly_server.repo.RoomRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.CategoriesService;
import com.engly.engly_server.service.common.ChatParticipantsService;
import com.engly.engly_server.service.common.RoomService;
import com.engly.engly_server.service.common.UserService;
import com.engly.engly_server.utils.cache.CacheName;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
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
    private final UserService userService;
    private final CategoriesService categoriesService;
    private final SecurityService service;
    private final ChatParticipantsService chatParticipantsService;

    @Override
    @Transactional
    @Caching(
            put = @CachePut(value = CacheName.ROOM_DTO_ID, key = "#result.id()"),
            evict = {
                    @CacheEvict(value = CacheName.PARTICIPANTS_BY_ROOM, key = "#result.id()"),
                    @CacheEvict(value = CacheName.ROOMS_BY_CATEGORY, allEntries = true)
            }
    )
    public RoomsDto createRoom(CategoryType name, RoomRequest roomRequestDto) {
        if (roomRepo.existsByName(roomRequestDto.name()))
            throw new EntityAlreadyExistsException(ROOM_ALREADY_EXISTS);

        final var username = service.getCurrentUserEmail();
        final var creator = userService.findUserEntityByEmail(username);

        final var room = roomRepo.save(Rooms.builder()
                .creator(creator)
                .createdAt(Instant.now())
                .category(categoriesService.findByName(name))
                .description(roomRequestDto.description())
                .name(roomRequestDto.name())
                .build());

        chatParticipantsService.addParticipant(room, creator, RoomRoles.ADMIN);
        return RoomMapper.INSTANCE.roomToDto(room);
    }

    @Override
    @Transactional
    @Caching(evict = {
            @CacheEvict(value = CacheName.ROOM_ID, key = "#id"),
            @CacheEvict(value = CacheName.ROOM_DTO_ID, key = "#id"),
            @CacheEvict(value = CacheName.ROOM_ENTITY_ID, key = "#id"),
            @CacheEvict(value = CacheName.ROOMS_BY_CATEGORY, allEntries = true),
            @CacheEvict(value = CacheName.ROOM_BY_CATEGORY_AND_KEY, allEntries = true)
    })
    public void deleteRoomById(String id) {
        roomRepo.findById(id).ifPresentOrElse(room -> roomRepo.deleteById(room.getId()),
                () -> {
                    throw new NotFoundException(ROOM_NOT_FOUND);
                });
    }

    @Override
    @Caching(
            put = {@CachePut(value = CacheName.ROOM_DTO_ID, key = "#id")},
            evict = {
                    @CacheEvict(value = CacheName.ROOM_ENTITY_ID, key = "#id"),
                    @CacheEvict(value = CacheName.ROOMS_BY_CATEGORY, allEntries = true)
            }
    )
    public RoomsDto updateRoom(String id, RoomUpdateRequest request) {
        return roomRepo.findById(id)
                .map(room -> {
                    if (isValid(request.newCategory()))
                        room.setCategory(categoriesService.findByName(request.newCategory()));

                    if (isValid(request.updateCreatorByEmail()))
                        room.setCreator(userService.findUserEntityByEmail(request.updateCreatorByEmail()));

                    if (isValid(request.description())) room.setDescription(request.description());
                    if (isValid(request.name())) room.setName(request.name());
                    return RoomMapper.INSTANCE.roomToDto(roomRepo.save(room));
                })
                .orElseThrow(() -> new NotFoundException(ROOM_NOT_FOUND));
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.ROOMS_BY_CATEGORY,
            key = "#category.name() + ':native:' + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()",
            condition = "#pageable.pageNumber < 10 && #pageable.pageSize <= 100",
            unless = "#result.content.isEmpty()"
    )
    public Page<RoomsDto> findAllRoomsByCategoryType(CategoryType category, Pageable pageable) {
        return roomRepo.findByCategoryName(category, pageable).map(RoomMapper.INSTANCE::roomToDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(
            value = CacheName.ROOM_BY_CATEGORY_AND_KEY,
            key = "#categoryType.name() + ':Key:' + #keyString + #pageable.pageNumber + ':' + #pageable.pageSize + ':' + #pageable.sort.toString()",
            condition = "#pageable.pageNumber < 10 && #pageable.pageSize <= 100",
            unless = "#result.content.isEmpty()"
    )
    public Page<RoomsDto> findAllRoomsByCategoryTypeContainingKeyString(CategoryType categoryType, String keyString, Pageable pageable) {
        return roomRepo.searchRooms(categoryType, keyString, pageable)
                .map(RoomMapper.INSTANCE::roomToDto);
    }

    @Override
    @Transactional(readOnly = true)
    @Cacheable(value = CacheName.ROOM_ENTITY_ID, key = "#id", sync = true)
    public Rooms findRoomEntityById(String id) {
        return roomRepo.findById(id).orElseThrow(() -> new NotFoundException(ROOM_NOT_FOUND));
    }
}
