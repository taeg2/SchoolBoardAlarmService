package com.example.schoolalarmservice.crawling.service;

import com.example.schoolalarmservice.crawling.dto.NoticeDto;
import com.example.schoolalarmservice.crawling.entity.Univ;
import com.example.schoolalarmservice.crawling.model.UnivCode;
import com.example.schoolalarmservice.crawling.repostiory.UnivRepository;
import com.example.schoolalarmservice.crawling.repostiory.UserUnivRepository;
import com.example.schoolalarmservice.telegram.MySchoolAlarmWebhookBot;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class MasterCrawlerService {

    // K: 스프링 빈 이름 ("SANGMYUNG", "KONKUK" 등), V: 크롤링 전략 구현체
    private final Map<String, UnivCrawlingStrategy> crawlerStrategies;

    private final UnivRepository univRepository;
    private final UserUnivRepository userUnivRepository;
    private final MySchoolAlarmWebhookBot telegramBot;

    @Transactional
    @Scheduled(cron = "0 0 9-18/3 * * MON-FRI")
//    @Scheduled(cron = "0/30 * * * * *")
    public void crawlAndNotifyAllUniversities() {
        log.info("▶️ 전체 대학 정기 장학 게시판 크롤링 시작...");

        List<Univ> univList = univRepository.findAll();

        for (Univ univ : univList) {
            try {
                // 1. 해당 대학에 맞는 크롤링 전략 클래스 가져오기
                // 주의: getUnivName()이 한글("건국대학교")이라면, 빈 이름("KONKUK")과 매칭되는 식별자(예: getUnivCode())를 사용해야 합니다.

                String beanName = univ.getUnivCode().name();

                UnivCrawlingStrategy strategy = crawlerStrategies.get(beanName);

                if (strategy == null) {
                    log.warn("지원하지 않는 대학 코드입니다: {}", univ.getUnivName());
                    continue; // 다음 대학으로 넘어감
                }

                // 2. 크롤링 실행 (순수 HTML 파싱 -> NoticeDto 리스트 반환)
                List<NoticeDto> notices = strategy.crawl(univ.getURL());

                Long dbLatestNumber = univ.getLatestPostNumber() != null ? univ.getLatestPostNumber() : 0L;
                Long currentMaxNumber = dbLatestNumber;
                List<String> newPostMessages = new ArrayList<>();

                // 3. 새 글 필터링 비즈니스 로직
                for (NoticeDto notice : notices) {
                    // record 타입이므로 getter 없이 notice.postNumber() 로 접근
                    if (notice.postNumber() > dbLatestNumber) {
                        String message = String.format("🎓 [%s] 새로운 공지\n\n📌 %s\n🔗 %s",
                                univ.getUnivName(), notice.title(), notice.link());
                        newPostMessages.add(message);

                        if (notice.postNumber() > currentMaxNumber) {
                            currentMaxNumber = notice.postNumber();
                        }
                    } else {
                        // 이미 읽은 글 번호 이하로 내려가면 최적화를 위해 중단 (사이트가 최신순 정렬인 경우)
                        break;
                    }
                }

                // 4. 새로운 글이 있다면 알림 발송 및 DB 업데이트
                if (!newPostMessages.isEmpty()) {
                    Collections.reverse(newPostMessages); // 오래된 글부터 순서대로 알림 발송

                    log.info("▶️ [디버깅] {} 크롤링된 새 글 개수: {}개", univ.getUnivName(), newPostMessages.size());

                    List<Long> chatIdsLong = userUnivRepository.findChatIdsByUnivId(univ.getId());

                    for (String messageText : newPostMessages) {
                        for (Long chatId : chatIdsLong) {
                            sendTelegramMessage(String.valueOf(chatId), messageText);

                            // 텔레그램 API 도배 방지를 위한 0.5초 대기
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }

                    // 5. DB 업데이트
                    univ.updateLatestPostNumber(currentMaxNumber);
                    log.info("{} 최신 게시글 번호 업데이트 완료: {}", univ.getUnivName(), currentMaxNumber);

                } else {
                    log.info("{} : 새로 올라온 공지가 없습니다.", univ.getUnivName());
                }

            } catch (Exception e) {
                log.error("{} 크롤링 전체 프로세스 중 오류 발생: {}", univ.getUnivName(), e.getMessage());
            }
        }
    }

    // 텔레그램 발송 헬퍼 메서드
    private void sendTelegramMessage(String chatId, String text) {
        SendMessage message = new SendMessage();
        message.setChatId(chatId);
        message.setText(text);
        try {
            telegramBot.execute(message);
        } catch (TelegramApiException e) {
            log.error("텔레그램 발송 실패 - ChatId: {}, Reason: {}", chatId, e.getMessage());
        }
    }
}