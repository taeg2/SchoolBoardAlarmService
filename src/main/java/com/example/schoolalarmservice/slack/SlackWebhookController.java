package com.example.schoolalarmservice.slack;

import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
public class SlackWebhookController {

    private final SlackCommandService slackCommandService;

    // 슬랙 앱 설정에서 Slash Command Request URL을 이 주소로 설정해야 합니다. (예: /api/slack/command)
    @PostMapping(value = "/api/slack/command", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, String> onCommandReceived(@RequestParam Map<String, String> payload) {

        // 슬랙이 보내는 데이터 중 필요한 부분 추출
        String slackUserId = payload.get("user_id"); // 예: U0123ABCD
        String command = payload.get("command");     // 예: /add
        String text = payload.get("text");           // 예: 건국대학교 (명령어 뒤에 붙은 텍스트)

        // 서비스 로직 실행 후 결과 텍스트 받아오기
        String responseText = slackCommandService.handleCommand(slackUserId, command, text);

        // 슬랙이 이해할 수 있는 JSON 형태로 응답 (텔레그램의 SendMessage 역할)
        Map<String, String> response = new HashMap<>();
        response.put("response_type", "ephemeral"); // ephemeral: 나에게만 보이는 메시지, in_channel: 모두에게 보이는 메시지
        response.put("text", responseText);

        return response; // 텔레그램처럼 이 return 값이 바로 슬랙 유저에게 텍스트로 날아갑니다.
    }
}