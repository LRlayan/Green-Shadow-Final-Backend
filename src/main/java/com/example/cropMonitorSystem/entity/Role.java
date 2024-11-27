package com.example.cropMonitorSystem.entity;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum Role {
    MANAGER,ADMINISTRATIVE,SCIENTIST,OTHER;

    @JsonCreator
    public static Role fromString(String value) {
        return Role.valueOf(value.toUpperCase());
    }
}
