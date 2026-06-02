package com.example.schoolalarmservice.crawling.service;

import com.example.schoolalarmservice.crawling.entity.Univ;
import com.example.schoolalarmservice.crawling.repostiory.UnivRepository;
import com.example.schoolalarmservice.crawling.repostiory.UserRepository;
import com.example.schoolalarmservice.crawling.repostiory.UserUnivRepository;
import com.example.schoolalarmservice.telegram.MySchoolAlarmWebhookBot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class KonkukSchoolAlarmCrawlerService {

    private final UnivRepository univRepository;
    private final UserUnivRepository userUnivRepository;
    private final UserRepository userRepository;
    private final MySchoolAlarmWebhookBot telegramBot; // 직접 만드신 웹훅 봇 클래스 의존성 주입

    @Transactional
    @Scheduled(cron = "0 0 9-18/3 * * MON-FRI") // MVP 테스트 후 스케줄러 활성화
//    @Scheduled(initialDelay = 5000, fixedDelay = 1000)
    public void crawlAndNotifyNewPosts() {
        log.info("정기 장학 게시판 크롤링 시작...");

        // 1. 등록된 모든 대학교 정보를 가져옵니다.
        List<Univ> univList = univRepository.findAll();

        for (Univ univ : univList) {
            try {
                // 2. 해당 대학교의 게시판 URL로 딱 한 번만 크롤링 수행
                Document doc = Jsoup.connect(univ.getURL())
                        .userAgent("Mozilla/5.0")
                        .timeout(5000)
                        .get();

                Elements posts = doc.select("tr");
                Long dbLatestNumber = univ.getLatestPostNumber() != null ? univ.getLatestPostNumber() : 0L;
                Long currentMaxNumber = dbLatestNumber;

                // 새로 발견된 게시글들을 담을 리스트 (여러 개가 올라왔을 수 있으므로)
                List<String> newPostMessages = new ArrayList<>();

                for (Element post : posts) {
                    if (post.hasClass("notice")) continue; // 공지사항 패스

                    Element numElement = post.selectFirst("td.td-num");
                    if (numElement == null) continue;

                    Long postNumber = Long.parseLong(numElement.text().trim());

                    // 3. DB의 최근 게시글 번호와 비교
                    if (postNumber > dbLatestNumber) {
                        Element subjectElement = post.selectFirst("td.td-subject a");
                        if (subjectElement != null) {
                            String title = subjectElement.text().trim();
                            String link = subjectElement.absUrl("href");

                            // 텔레그램으로 보낼 메시지 포맷팅
                            String message = String.format("🎓 [%s] 새로운 공지\n\n📌 %s\n🔗 %s",
                                    univ.getUnivName(), title, link);
                            newPostMessages.add(message);

                            // 읽어온 글 번호 중 가장 큰 값을 갱신용으로 저장
                            if (postNumber > currentMaxNumber) {
                                currentMaxNumber = postNumber;
                            }
                        }
                    } else {
                        // 이미 읽은 글 번호 이하로 내려가면 더 이상 파싱할 필요 없음 (최적화)
                        break;
                    }
                }

                // 4. 새로운 글이 있다면 알림 발송 및 DB 업데이트
                if (!newPostMessages.isEmpty()) {
                    Collections.reverse(newPostMessages);

                    // 🔍 [디버깅용 로그 추가] 현재 어떤 학교 ID로 조회하는지, 크롤링된 글은 몇 개인지 확인
                    log.info("▶️ [디버깅] 현재 크롤링된 학교 ID: {}, 총 메시지 개수: {}개", univ.getId(), newPostMessages.size());

                    List<Long> chatIdsLong = userUnivRepository.findChatIdsByUnivId(univ.getId());

                    // 🔍 [디버깅용 로그 추가] DB에서 찾아온 구독자 수 확인
                    log.info("▶️ [디버깅] DB에서 조회된 구독자(ChatID) 수: {}개", chatIdsLong.size());

                    List<String> chatIdsString = chatIdsLong.stream()
                            .map(String::valueOf)
                            .toList();

                    for (String messageText : newPostMessages) {
                        for (String chatId : chatIdsString) {
                            sendTelegramMessage(chatId, messageText);

                            // ✨ [추가] 텔레그램 API 도배 방지를 위해 0.5초 대기
                            try {
                                Thread.sleep(500);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                            }
                        }
                    }

                    // 5. 대학교의 최신 게시글 번호 DB 업데이트
                    univ.updateLatestPostNumber(currentMaxNumber);
                    log.info("{} 최신 게시글 번호 업데이트 완료: {}", univ.getUnivName(), currentMaxNumber);

                } else {
                    log.info("{} : 새로 올라온 공지가 없습니다.", univ.getUnivName());
                }

            } catch (Exception e) {
                log.error("{} 크롤링 중 오류 발생: {}", univ.getUnivName(), e.getMessage());
            }
        }
    }

    // 텔레그램 API 호출 래핑 메서드
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