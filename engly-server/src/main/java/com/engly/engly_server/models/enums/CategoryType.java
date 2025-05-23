package com.engly.engly_server.models.enums;

import lombok.Getter;

@Getter
public enum CategoryType {
    HOBBIES("Hobbies", "hobbies.svg"),
    TECH("Tech", "tech.svg"),
    NEWS("News", "news.svg"),
    GENERAL_CHAT("General Chat", "chat.svg"),
    SPORTS("Sports", "sports.svg"),
    TRAVEL_AND_FOOD("Travel and Food", "travel.svg"),
    CAREER("Career", "career.svg"),
    MOVIES("Movies", "movies.svg");

    private final String val;
    private final String icon;

    CategoryType(String val, String icon) {
        this.val = val;
        this.icon = icon;
    }
}
