package com.example.schoolalarmservice.crawling.service;


import com.example.schoolalarmservice.crawling.entity.Univ;
import com.example.schoolalarmservice.crawling.repostiory.UnivRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterCrawlerService { // 이름 변경: 전체 대학을 관장하는 마스터 서비스

    private final UnivRepository univRepository;
    private final UnivCrawlerFactory crawlerFactory; // 🔥 대학별 파싱 전략을 찾아주는 팩토리
    // ... 나머지 의존성 동일

    @Scheduled(cron = "0 0 9-18/3 * * MON-FRI")
    public void crawlAndNotifyNewPosts() {
        List<Univ> univList = univRepository.findAll();

        for (Univ univ : univList) {
            try {
                // 1. "나는 네가 어떻게 파싱하는지 모르겠고, 새 글 리스트만 줘!" (전략 패턴 호출)
                UnivCrawlingStrategy strategy = crawlerFactory.getStrategy(univ.getUnivCode());
                List<ParsedPost> newPosts = strategy.crawl(univ);

                // 2. 새로운 글이 없다면 다음 대학으로 패스
                if (newPosts.isEmpty()) continue;

                // 3. 텔레그램 발송 및 DB 업데이트 로직 (모든 대학 공통 로직)
                processNotificationsAndSave(univ, newPosts);

            } catch (Exception e) {
                log.error("{} 크롤링 중 오류: {}", univ.getUnivName(), e.getMessage());
            }
        }
    }

    private void processNotificationsAndSave(Univ univ, List<ParsedPost> newPosts) {
        // 기존에 작성하신 텔레그램 발송 (for문, Thread.sleep 등) + DB의 currentMaxNumber 업데이트 로직
        // 이 공통 로직은 이곳에 딱 한 번만 작성됩니다!
    }
}
