package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.ChatParticipantsDto;
import com.engly.engly_server.models.dto.MessagesDto;
import com.engly.engly_server.models.dto.ModerationDto;
import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.dto.StatisticsDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.ChatParticipants;
import com.engly.engly_server.models.entity.Moderation;
import com.engly.engly_server.models.entity.Rooms;
import com.engly.engly_server.models.entity.Statistics;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:40+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class RoomMapperImpl implements RoomMapper {

    private final UserMapper userMapper = UserMapper.INSTANCE;
    private final ChatParticipantMapper chatParticipantMapper = ChatParticipantMapper.INSTANCE;
    private final ModerationMapper moderationMapper = ModerationMapper.INSTANCE;
    private final StatisticMapper statisticMapper = StatisticMapper.INSTANCE;

    @Override
    public RoomsDto roomToDto(Rooms rooms) {
        if ( rooms == null ) {
            return null;
        }

        UsersDto creator = null;
        List<ChatParticipantsDto> chatParticipants = null;
        List<ModerationDto> moderation = null;
        List<StatisticsDto> statistics = null;
        String id = null;
        String name = null;
        String description = null;
        Instant createdAt = null;
        Instant updatedAt = null;

        creator = userMapper.toUsersDto( rooms.getCreator() );
        chatParticipants = chatParticipantsListToChatParticipantsDtoList( rooms.getChatParticipants() );
        moderation = moderationListToModerationDtoList( rooms.getModeration() );
        statistics = statisticsListToStatisticsDtoList( rooms.getStatistics() );
        id = rooms.getId();
        name = rooms.getName();
        description = rooms.getDescription();
        createdAt = rooms.getCreatedAt();
        updatedAt = rooms.getUpdatedAt();

        List<MessagesDto> messages = null;

        RoomsDto roomsDto = new RoomsDto( id, name, description, createdAt, updatedAt, creator, messages, chatParticipants, moderation, statistics );

        return roomsDto;
    }

    protected List<ChatParticipantsDto> chatParticipantsListToChatParticipantsDtoList(List<ChatParticipants> list) {
        if ( list == null ) {
            return null;
        }

        List<ChatParticipantsDto> list1 = new ArrayList<ChatParticipantsDto>( list.size() );
        for ( ChatParticipants chatParticipants : list ) {
            list1.add( chatParticipantMapper.toDtoForRooms( chatParticipants ) );
        }

        return list1;
    }

    protected List<ModerationDto> moderationListToModerationDtoList(List<Moderation> list) {
        if ( list == null ) {
            return null;
        }

        List<ModerationDto> list1 = new ArrayList<ModerationDto>( list.size() );
        for ( Moderation moderation : list ) {
            list1.add( moderationMapper.toDtoForRoom( moderation ) );
        }

        return list1;
    }

    protected List<StatisticsDto> statisticsListToStatisticsDtoList(List<Statistics> list) {
        if ( list == null ) {
            return null;
        }

        List<StatisticsDto> list1 = new ArrayList<StatisticsDto>( list.size() );
        for ( Statistics statistics : list ) {
            list1.add( statisticMapper.toDto( statistics ) );
        }

        return list1;
    }
}
