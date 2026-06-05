package com.example.schoolalarmservice.crawling.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UnivCode {
    KONKUK("건국대학교"),
    SANGMYUNG("상명대학교");

    private final String description;
}
