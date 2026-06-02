package com.example.schoolalarmservice.crawling.service;

import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class SangmyungAlarmClawerTestService {

    @PostConstruct
    public void testCrawling() {
        String targetUrl = "https://www.smu.ac.kr/kor/life/notice.do?srCampus=smu";

        try {
            System.out.println("====== 상명대 공지사항 크롤링 테스트 시작 ======");

            Document doc = Jsoup.connect(targetUrl)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(5000)
                    .get();

            // 게시글 레이아웃 전체를 감싸는 dl 태그 선택
            Elements posts = doc.select("dl.board-thumb-content-wrap");
            System.out.println("찾은 전체 게시글 개수: " + posts.size());

            for (Element post : posts) {
                // 💡 핵심 1: 'noti' 클래스가 있는 고정 공지(확성기 아이콘)는 패스!
                if (post.hasClass("noti")) {
                    continue;
                }

                // 1. 번호 추출
                Element numElement = post.selectFirst("li.board-thumb-content-number");
                if (numElement == null) continue;

                String numText = numElement.text().replace("No.", "").trim();

                // 만약 숫자가 아닌 값이 있다면 안전하게 패스
                if (!numText.matches("\\d+")) continue;

                // 💡 핵심 2: dt 태그 안의 모든 a 태그를 리스트 형태로 가져옵니다.
                Elements aTags = post.select("dt.board-thumb-content-title td a");

                // a 태그 리스트가 비어있지 않다면
                if (!aTags.isEmpty()) {
                    // 💡 핵심 3: 리스트의 가장 마지막 요소(last)를 꺼냅니다. (이것이 진짜 제목!)
                    Element titleElement = aTags.last();

                    String title = titleElement.text().trim();
                    String link = titleElement.absUrl("href");

                    System.out.println("글 번호: " + numText);
                    System.out.println("제  목: " + title);
                    System.out.println("링  크: " + link);
                    System.out.println("-------------------------");
                }
            }
            System.out.println("====== 크롤링 테스트 종료 ======");

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
