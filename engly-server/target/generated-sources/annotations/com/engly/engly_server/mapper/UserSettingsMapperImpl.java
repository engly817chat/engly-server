package com.engly.engly_server.mapper;

import com.engly.engly_server.models.dto.UserSettingsDto;
import com.engly.engly_server.models.dto.UsersDto;
import com.engly.engly_server.models.entity.UserSettings;
import com.engly.engly_server.models.enums.NativeLanguage;
import com.engly.engly_server.models.enums.Theme;
import javax.annotation.processing.Generated;

@Generated(
    value = "org.mapstruct.ap.MappingProcessor",
    date = "2025-05-24T02:53:41+0300",
    comments = "version: 1.6.3, compiler: javac, environment: Java 24.0.1 (Oracle Corporation)"
)
public class UserSettingsMapperImpl implements UserSettingsMapper {

    private final UserMapper userMapper = UserMapper.INSTANCE;

    @Override
    public UserSettingsDto toUserSettingsDto(UserSettings userSettings) {
        if ( userSettings == null ) {
            return null;
        }

        String id = null;
        UsersDto user = null;
        Theme theme = null;
        boolean notifications = false;
        NativeLanguage interfaceLanguage = null;

        id = userSettings.getId();
        user = userMapper.toUsersDto( userSettings.getUser() );
        theme = userSettings.getTheme();
        notifications = userSettings.isNotifications();
        interfaceLanguage = userSettings.getInterfaceLanguage();

        UserSettingsDto userSettingsDto = new UserSettingsDto( id, user, theme, notifications, interfaceLanguage );

        return userSettingsDto;
    }
}
