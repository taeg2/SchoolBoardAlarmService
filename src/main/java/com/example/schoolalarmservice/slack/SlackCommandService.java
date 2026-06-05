package com.example.schoolalarmservice.slack;

import com.example.schoolalarmservice.crawling.entity.Univ;
import com.example.schoolalarmservice.crawling.entity.User;
import com.example.schoolalarmservice.crawling.entity.UserUniv;
import com.example.schoolalarmservice.crawling.model.UnivStatus;
// ... (기존 레포지토리 임포트)
import com.example.schoolalarmservice.crawling.repostiory.UnivRepository;
import com.example.schoolalarmservice.crawling.repostiory.UserRepository;
import com.example.schoolalarmservice.crawling.repostiory.UserUnivRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SlackCommandService {

    private final UserRepository userRepository;
    private final UnivRepository univRepository;
    private final UserUnivRepository userUnivRepository;

    @Transactional
    public String handleCommand(String slackUserId, String command, String text) {
        String argument = (text != null) ? text.trim() : "";

        return switch (command) {
            case "/start" -> handleStart(slackUserId);
            case "/add" -> handleAdd(slackUserId, argument);
            case "/list" -> handleList(slackUserId);
            case "/cancel" -> handleCancel(slackUserId, argument);
            default -> "알 수 없는 명령어입니다. /start, /add, /list, /cancel 중 하나를 입력해주세요.";
        };
    }

    private String handleStart(String slackUserId) {
        // DB에 회원이 없으면 슬랙 ID를 기반으로 새로 저장
        if (!userRepository.existsBySlackId(slackUserId)) {
            User newUser = User.builder().slackId(slackUserId).build();
            userRepository.save(newUser);
        }
        return "환영합니다! 장학 알림 슬랙 봇입니다.\n\n사용법:\n/add 학교이름\n/list\n/cancel 학교이름";
    }

    private String handleAdd(String slackUserId, String univName) {
        if (univName.isEmpty()) return "학교 이름을 함께 입력해주세요. (예: /add 건국대학교)";

        User user = userRepository.findBySlackId(slackUserId).orElse(null);
        if (user == null) return "/start 를 먼저 입력해주세요.";

        Univ univ = univRepository.findByUnivName(univName).orElse(null);
        if (univ == null) return "등록되지 않은 학교입니다. 정확한 이름을 입력해주세요.";

        // 1. 이미 데이터가 존재하는지 확인
        UserUniv existingUserUniv = userUnivRepository.findByUserAndUniv(user, univ).orElse(null);

        if (existingUserUniv != null) {
            // 1-1. 상태가 ENROLLED라면 중복 가입 방지
            if (UnivStatus.ENROLLED.equals(existingUserUniv.getIsActive())) {
                return "이미 알림을 받고 있는 학교입니다.";
            }
            // 1-2. 상태가 CANCEL이라면, 다시 ENROLLED로 상태만 UPDATE
            else {
                existingUserUniv.enrollSubscription();
                return univName + " 장학 게시판 알림이 다시 등록되었습니다! 🔔";
            }
        }

        // 2. 아예 처음 구독하는 경우 새로 INSERT
        UserUniv newUserUniv = UserUniv.builder()
                .user(user)
                .univ(univ)
                .isActive(UnivStatus.ENROLLED)
                .build();
        userUnivRepository.save(newUserUniv);

        return univName + " 장학 게시판 알림 등록이 완료되었습니다! 🔔";
    }


    private String handleList(String slackId) {

        User user = userRepository.findBySlackId(slackId).orElse(null);
        if (user == null) return "/start 를 먼저 입력해주세요.";

        // 해당 유저가 구독 중인 학교 목록 조회
        List<UserUniv> subscriptions = userUnivRepository.findAllByUser(user);

        if (subscriptions.isEmpty()) {
            return "현재 알림을 받고 있는 학교가 없습니다.";
        }

        String listStr = subscriptions.stream()
                .map(su -> "- " + su.getUniv().getUnivName())
                .collect(Collectors.joining("\n"));

        return "📌 [현재 구독 중인 학교 목록]\n" + listStr;
    }

    private String handleCancel(String slackId, String univName) {
        if (univName.isEmpty()) return "취소할 학교 이름을 함께 입력해주세요. (예: /cancel 건국대학교)";

        User user = userRepository.findBySlackId(slackId).orElse(null);
        if (user == null) return "등록된 정보가 없습니다.";

        Univ univ = univRepository.findByUnivName(univName).orElse(null);
        if (univ == null) return "존재하지 않는 학교입니다.";

        // 1. 해당 유저-학교 매핑 정보를 DB에서 꺼내옵니다.
        UserUniv userUniv = userUnivRepository.findByUserAndUniv(user, univ).orElse(null);

        if (userUniv == null) {
            return "구독 중인 학교가 아닙니다.";
        }

        if (UnivStatus.CANCEL.equals(userUniv.getIsActive())) {
            return "이미 알림이 취소된 학교입니다.";
        }

        // 2. 물리적 삭제(delete) 대신 엔티티의 상태값만 변경합니다.
        userUniv.cancelSubscription();

        // @Transactional 안에서 엔티티의 값이 변경되었으므로,
        // save()를 호출하지 않아도 메서드가 끝날 때 알아서 UPDATE 쿼리가 날아갑니다. (Dirty Checking)

        return univName + " 알림이 취소되었습니다. 🔕";
    }

}