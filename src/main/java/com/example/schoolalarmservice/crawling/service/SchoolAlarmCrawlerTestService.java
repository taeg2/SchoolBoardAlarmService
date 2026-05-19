package com.example.schoolalarmservice.crawling.service;

import jakarta.annotation.PostConstruct;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;

@Service
public class SchoolAlarmCrawlerTestService {

//    @PostConstruct
    public void testCrawling() {
        // 실제 크롤링할 타겟 게시판 URL로 변경해주세요.
        String targetUrl = "https://www.konkuk.ac.kr/konkuk/2239/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGa29ua3VrJTJGMjM1JTJGYXJ0Y2xMaXN0LmRvJTNG";
        String baseUrl = "https://www.konkuk.ac.kr";

        try {
            System.out.println("====== 장학 게시판 크롤링 테스트 시작 ======");

            // 1. 대상 웹 페이지 HTML 가져오기 (User-Agent는 봇 차단 방지용)
            Document doc = Jsoup.connect(targetUrl)
                    .userAgent("Mozilla/5.0")
                    .timeout(5000) // 5초 내에 응답 없으면 예외 발생
                    .get();

            // 2. 게시글 목록을 감싸고 있는 요소 선택 (F12 개발자 도구로 확인 필요)
            // 예시: <table class="board_table"> 안의 <tbody> 안의 <tr> 태그들
            Elements posts = doc.select("tr");

            for (Element post : posts) {
                // 💡 핵심 1: 'notice' 클래스가 있는 공지사항 행은 건너뜁니다!
                if (post.hasClass("notice")) {
                    continue;
                }

                // 💡 핵심 2: td-num을 찾습니다. (이게 없으면 테이블 헤더(th) 같은 엉뚱한 줄입니다)
                Element numElement = post.selectFirst("td.td-num");
                if (numElement == null) {
                    continue;
                }

                String postNumber = numElement.text().trim();

                // 제목과 링크가 있는 td-subject 안의 <a> 태그를 찾습니다.
                Element subjectElement = post.selectFirst("td.td-subject a");

                if (subjectElement != null) {
                    String title = subjectElement.text().trim();
                    String link = subjectElement.attr("href");

                    System.out.println("글 번호: " + postNumber);
                    System.out.println("제  목: " + title);
                    System.out.println("링  크: " + link);
                    System.out.println("-------------------------");
                }
            }
            System.out.println("====== 크롤링 테스트 종료 ======");

        } catch (Exception e) {
            System.err.println("크롤링 중 에러 발생: " + e.getMessage());
        }
    }
}
