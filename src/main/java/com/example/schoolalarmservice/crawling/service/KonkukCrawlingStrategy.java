package com.example.schoolalarmservice.crawling.service;

import com.example.schoolalarmservice.crawling.dto.NoticeDto;
import com.example.schoolalarmservice.crawling.model.UnivCode;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Component("KONKUK ") // DB의 univCode와 매칭될 빈 이름
public class KonkukCrawlingStrategy implements UnivCrawlingStrategy {

    @Override
    public List<NoticeDto> crawl(String boardUrl) {
        List<NoticeDto> notices = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(boardUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000)
                    .get();

            Elements posts = doc.select("tr");

            for (Element post : posts) {
                // 1. 고정 공지사항(notice 클래스) 패스
                if (post.hasClass("notice")) {
                    continue;
                }

                // 2. 글 번호 추출
                Element numElement = post.selectFirst("td.td-num");
                if (numElement == null) {
                    continue;
                }

                long postNumber;
                try {
                    postNumber = Long.parseLong(numElement.text().trim());
                } catch (NumberFormatException e) {
                    // 번호가 숫자가 아닌 경우(테이블 헤더 등) 패스
                    continue;
                }

                // 3. 제목 및 링크 추출
                Element subjectElement = post.selectFirst("td.td-subject a");
                if (subjectElement != null) {
                    String title = subjectElement.text().trim();
                    String link = subjectElement.absUrl("href"); // 절대 경로 추출

                    // 파싱한 순수 데이터를 DTO에 담아 리스트에 추가
                    notices.add(new NoticeDto(title, link, postNumber));
                }
            }
        } catch (IOException e) {
            log.error("건국대 크롤링 중 IO 오류 발생: {}", e.getMessage());
        } catch (Exception e) {
            log.error("건국대 크롤링 중 알 수 없는 오류 발생: {}", e.getMessage());
        }

        return notices;
    }
}