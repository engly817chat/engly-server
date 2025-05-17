package com.engly.engly_server.service.common.impl;

import com.engly.engly_server.exception.NotFoundException;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.enums.CategoryType;
import com.engly.engly_server.models.dto.create.RoomRequestDto;
import com.engly.engly_server.models.dto.update.RoomUpdateRequest;
import com.engly.engly_server.repository.CategoriesRepository;
import com.engly.engly_server.repository.RoomRepository;
import com.engly.engly_server.repository.UserRepository;
import com.engly.engly_server.security.config.SecurityService;
import com.engly.engly_server.service.common.RoomService;
import com.engly.engly_server.mapper.RoomMapper;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
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
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final CategoriesRepository categoriesRepository;
    private final SecurityService service;

    @Override
    @RateLimiter(name = "RoomService")
    @CacheEvict(value = "roomsByCategory", key = "#name")
    @CachePut(value = "roomById", key = "#result.id")
    @Transactional
    public RoomsDto createRoom(CategoryType name, RoomRequestDto roomRequestDto) {
        final var username = service.getCurrentUserEmail();
        final var category = categoriesRepository.findByName(name)
                .orElseThrow(() -> new NotFoundException("Category not found"));
        return userRepository.findByEmail(username)
                .map(creator -> RoomMapper.INSTANCE.roomToDto(roomRepository.save(
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
    @Cacheable(value = "roomsByCategory", key = "#category")
    @Transactional(readOnly = true)
    public List<RoomsDto> findAllRoomsByCategoryType(CategoryType category) {
        return roomRepository.findAllByCategory_Name(category)
                .stream()
                .map(RoomMapper.INSTANCE::roomToDto)
                .toList();
    }

    @Override
    @RateLimiter(name = "RoomService")
    @Caching(evict = {
            @CacheEvict(value = "roomById", key = "#id"),
            @CacheEvict(value = "roomsByCategory", allEntries = true),
            @CacheEvict(value = "roomSearchResults", allEntries = true)
    })
    public void deleteRoomById(String id) {
        final var room = roomRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("You can't delete this room"));
        roomRepository.delete(room);
    }

    @Override
    @RateLimiter(name = "RoomService")
    public RoomsDto updateRoom(String id, RoomUpdateRequest request) {
        return roomRepository.findById(id)
                .map(room -> {
                    if (isValid(request.newCategory())) room.setCategory(categoriesRepository.findByName(request.newCategory())
                            .orElseThrow(() -> new NotFoundException("Category not found")));

                    if (isValid(request.updateCreatorByEmail()))
                        room.setCreator(userRepository.findByEmail(request.updateCreatorByEmail())
                                .orElseThrow(() -> new NotFoundException("Creator not found")));

                    if (isValid(request.description())) room.setDescription(request.description());
                    if (isValid(request.name())) room.setName(request.name());
                    return RoomMapper.INSTANCE.roomToDto(roomRepository.save(room));
                })
                .orElseThrow(() -> new NotFoundException("Room not found"));
    }

    @Override
    @Cacheable(value = "roomSearchResults", key = "#keyString")
    @Transactional(readOnly = true)
    public List<RoomsDto> findAllRoomsContainingKeyString(String keyString) {
        return roomRepository.findAllRoomsContainingKeyString(keyString)
                .stream()
                .map(RoomMapper.INSTANCE::roomToDto)
                .toList();
    }
}
