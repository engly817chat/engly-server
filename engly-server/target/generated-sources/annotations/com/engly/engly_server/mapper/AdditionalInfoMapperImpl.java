package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.AdditionalInfoDto;
import com.engly.engly_server.models.entity.AdditionalInfo;
import com.engly.engly_server.models.enums.EnglishLevels;
import com.engly.engly_server.models.enums.Goals;
import com.engly.engly_server.models.enums.NativeLanguage;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:41+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class AdditionalInfoMapperImpl implements AdditionalInfoMapper {

    @Override
    public AdditionalInfoDto toDto(AdditionalInfo additionalInfo) {
        if ( additionalInfo == null ) {
            return null;
        }

        String id = null;
        EnglishLevels englishLevel = null;
        NativeLanguage nativeLanguage = null;
        Goals goal = null;

        id = additionalInfo.getId();
        englishLevel = additionalInfo.getEnglishLevel();
        nativeLanguage = additionalInfo.getNativeLanguage();
        goal = additionalInfo.getGoal();

        AdditionalInfoDto additionalInfoDto = new AdditionalInfoDto( id, englishLevel, nativeLanguage, goal );

        return additionalInfoDto;
    }
}
