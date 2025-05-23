package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.mapper.RoomMapper;
import com.engly.engly_server.models.dto.ApiResponse;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.dto.create.RoomRequestDto;
import com.engly.engly_server.models.dto.update.RoomUpdateRequest;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.repo.CategoriesRepo;
import com.engly.engly_server.repo.RoomRepo;
import com.engly.engly_server.repo.UserRepo;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.RoomService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.cache.annotation.Caching;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.List;

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
    @Caching(
            evict = {
                    @CacheEvict(value = "roomsByCategory", key = "#name"),
                    @CacheEvict(value = "rooms", allEntries = true),
                    @CacheEvict(value = "roomSearchResults", allEntries = true)
            },
            put = {
                    @CachePut(value = "roomById", key = "#result.id")
            }
    )
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
    @Cacheable(value = "roomsByCategory", key = "#category", sync = true)
    @Transactional(readOnly = true)
    public List<RoomsDto> findAllRoomsByCategoryType(CategoryType category) {
        return roomRepo.findAllByCategory_Name(category)
                .stream()
                .map(RoomMapper.INSTANCE::roomToDto)
                .toList();
    }

    @Override
    @Caching(evict = {
            @CacheEvict(value = "roomById", key = "#id"),
            @CacheEvict(value = "roomsByCategory", allEntries = true),
            @CacheEvict(value = "roomSearchResults", allEntries = true),
            @CacheEvict(value = "rooms", allEntries = true)
    })
    public ApiResponse deleteRoomById(String id) {
        return roomRepo.findById(id)
                .map(rooms -> {
                    roomRepo.delete(rooms);
                    return new ApiResponse("Room deleted successfully", true, Instant.now());
                })
                .orElseThrow(() -> new NotFoundException("You can't delete this room"));
    }

    @Override
    @Caching(
            put = {
                    @CachePut(value = "roomById", key = "#id")
            },
            evict = {
                    @CacheEvict(value = "roomsByCategory", allEntries = true),
                    @CacheEvict(value = "roomSearchResults", allEntries = true),
                    @CacheEvict(value = "rooms", allEntries = true)
            }
    )
    public RoomsDto updateRoom(String id, RoomUpdateRequest request) {
        return roomRepo.findById(id)
                .map(room -> {
                    if (isValid(request.newCategory()))
                        room.setCategory(categoriesRepo.findByName(request.newCategory())
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
    @Cacheable(value = "rooms", key = "'keyString_' + #keyString", sync = true)
    @Transactional(readOnly = true)
    public List<RoomsDto> findAllRoomsContainingKeyString(String keyString) {
        return roomRepo.findAllRoomsContainingKeyString(keyString)
                .stream()
                .map(RoomMapper.INSTANCE::roomToDto)
                .toList();
    }

    @Override
    @Cacheable(value = "roomSearchResults", key = "'category_' + #categoryType + '_key_' + #keyString", sync = true)
    @Transactional(readOnly = true)
    public List<RoomsDto> findAllRoomsByCategoryTypeContainingKeyString(CategoryType categoryType, String keyString) {
        return roomRepo.findAllByNameContainingIgnoreCaseAndCategoryName(keyString, categoryType)
                .stream()
                .map(RoomMapper.INSTANCE::roomToDto)
                .toList();
    }
}
