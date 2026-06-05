package com.example.schoolalarmservice.crawling.service;

import com.example.schoolalarmservice.crawling.dto.NoticeDto;
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
@Component("SANGMYUNG") // DB에 저장된 univCode와 동일하게 맞춰주세요.
public class SangmyungCrawlingStrategy implements UnivCrawlingStrategy {

    @Override
    public List<NoticeDto> crawl(String boardUrl) {
        List<NoticeDto> notices = new ArrayList<>();

        try {
            // boardUrl은 DB(Univ 엔티티)에서 가져온 상명대 URL이 주입됩니다.
            Document doc = Jsoup.connect(boardUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(5000)
                    .get();

            // 테스트 성공 코드 반영: 전체 게시글 래퍼 선택
            Elements posts = doc.select("dl.board-thumb-content-wrap");

            for (Element post : posts) {
                // 1. 고정 공지(확성기 아이콘) 패스
                if (post.hasClass("noti")) {
                    continue;
                }

                // 2. 글 번호 추출 및 파싱
                Element numElement = post.selectFirst("li.board-thumb-content-number");
                if (numElement == null) {
                    continue;
                }

                String numText = numElement.text().replace("No.", "").trim();

                // 숫자가 아닌 값이 포함된 경우 안전하게 패스
                if (!numText.matches("\\d+")) {
                    continue;
                }

                long postNumber = Long.parseLong(numText);

                // 3. 카테고리 태그 낚시 방지 및 진짜 제목/링크 추출
                Elements aTags = post.select("dt.board-thumb-content-title td a");

                if (!aTags.isEmpty()) {
                    // 리스트의 가장 마지막 요소(last) 선택
                    Element titleElement = aTags.last();

                    String title = titleElement.text().trim();
                    String link = titleElement.absUrl("href"); // 절대 경로 추출

                    // 파싱한 순수 데이터를 DTO(record)에 담아 리스트에 추가
                    notices.add(new NoticeDto(title, link, postNumber));
                }
            }
        } catch (IOException e) {
            log.error("상명대 크롤링 중 IO 오류 발생: {}", e.getMessage());
        } catch (Exception e) {
            log.error("상명대 크롤링 중 알 수 없는 오류 발생: {}", e.getMessage());
        }

        return notices;
    }
}