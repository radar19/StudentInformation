package com.studentinformation.domain;

import lombok.Getter;

@Getter
public enum Week {
    MONDAY("월요일"),
    TUESDAY("화요일"),
    WEDNESDAY("수요일"),
    THURSDAY("목요일"),
    FRIDAY("금요일"),
    SATURDAY("토요일"),
    SUNDAY("일요일");

    private final String name;

    Week(String name) {
        this.name = name;
    }
}