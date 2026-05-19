package com.example.schoolalarmservice.crawling.service;

import com.example.schoolalarmservice.crawling.entity.Univ;
import com.example.schoolalarmservice.crawling.repostiory.UnivRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional // 테스트가 끝나면 DB에 넣었던 데이터를 깔끔하게 롤백(삭제) 해줍니다.
class SchoolAlarmCrawlerServiceTest {

    @Autowired
    SchoolAlarmCrawlerService crawlerService;

    @Autowired
    UnivRepository univRepository;

    @Test
    @DisplayName("DB의 과거 글 번호와 비교하여 신규 글을 감지하고, DB 최신 번호를 갱신해야 한다.")
    void crawlAndCompareTest() {
        // Given (준비): 테스트용 학교 데이터를 DB에 저장합니다.
        // 현재 건국대 장학 게시판의 최신 번호가 1002번이라면, 일부러 1000번으로 세팅합니다.
        Univ testUniv = new Univ();
        testUniv.setUnivName("건국대학교");
        testUniv.setURL("https://www.konkuk.ac.kr/konkuk/2239/subview.do?enc=Zm5jdDF8QEB8JTJGYmJzJTJGa29ua3VrJTJGMjM1JTJGYXJ0Y2xMaXN0LmRvJTNG");
        testUniv.setLatestPostNumber(1000L);

        Univ savedUniv = univRepository.save(testUniv);
        System.out.println("크롤링 전 DB 글 번호: " + savedUniv.getLatestPostNumber());

        // When (실행): 크롤링 서비스 로직을 실행합니다.
        // (1001번, 1002번 글을 새 글로 인식하고 로그에 출력해야 합니다.)
        crawlerService.crawlAndNotifyNewPosts();

        // Then (검증): DB에 저장된 학교 정보의 최신 글 번호가 1000보다 커졌는지(갱신되었는지) 확인합니다.
        Univ updatedUniv = univRepository.findById(savedUniv.getId()).orElseThrow();
        System.out.println("크롤링 후 갱신된 DB 글 번호: " + updatedUniv.getLatestPostNumber());

        // AssertJ를 이용한 검증: 갱신된 번호가 기존 세팅한 1000L보다 커야만 테스트 성공!
        assertThat(updatedUniv.getLatestPostNumber()).isGreaterThan(1000L);
    }
}