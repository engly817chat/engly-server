package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.RoomsDto;
import com.engly.engly_server.models.dto.StatisticsDto;
import com.engly.engly_server.models.entity.Statistics;
import java.time.LocalDateTime;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:40+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class StatisticMapperImpl implements StatisticMapper {

    @Override
    public StatisticsDto toDto(Statistics statistics) {
        if ( statistics == null ) {
            return null;
        }

        String id = null;
        Long messageCount = null;
        LocalDateTime lastMessageTime = null;

        id = statistics.getId();
        messageCount = statistics.getMessageCount();
        lastMessageTime = statistics.getLastMessageTime();

        RoomsDto room = null;

        StatisticsDto statisticsDto = new StatisticsDto( id, room, messageCount, lastMessageTime );

        return statisticsDto;
    }
}
