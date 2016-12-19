package me.xkuyax.hdfilme.rest.api;

import lombok.Getter;

public enum QualityLevel {

    FULL_HD("1080p"),
    HD("720p"),
    SD("480p"),
    BULLSHIT("360p");

    @Getter
    private String identifier;

    QualityLevel(String identifier) {
        this.identifier = identifier;
    }
}
