package com.example.schoolalarmservice.crawling.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public enum UnivStatus {
    ENROLLED("등록 완료"),
    CANCEL("등록 취소");

    private final String description;
}
