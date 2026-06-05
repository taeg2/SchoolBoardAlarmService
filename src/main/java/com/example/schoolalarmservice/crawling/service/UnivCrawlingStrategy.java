package com.example.schoolalarmservice.crawling.service;

import com.example.schoolalarmservice.crawling.dto.NoticeDto;

import java.util.List;

//전략 패턴 활용 런타임에 의존성 주입 가능(Map 주입 사용)
public interface UnivCrawlingStrategy {
    List<NoticeDto> crawl(String boardUrl);
}
