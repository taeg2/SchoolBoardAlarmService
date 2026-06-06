package com.example.schoolalarmservice.slack;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequiredArgsConstructor
@Slf4j
public class SlackWebhookController {

    // 슬랙 앱 설정에서 Slash Command Request URL을 이 주소로 설정해야 합니다. (예: /api/slack/command)
    private final SlackCommandService slackCommandService;

    @PostMapping(value = "/api/slack/command", consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    public Map<String, String> onCommandReceived(SlackSlashCommandDto requestDto) {

        log.info("슬랙 요청 수신: {}", requestDto); // record는 toString()도 자동으로 예쁘게 출력해줍니다.

        // 🚨 주의: record는 Getter 이름이 'get'으로 시작하지 않습니다. 필드명과 동일한 메서드를 호출합니다.
        String slackUserId = requestDto.user_id();
        String command = requestDto.command();
        String text = requestDto.text();

        String responseText = slackCommandService.handleCommand(slackUserId, command, text);

        Map<String, String> response = new HashMap<>();
        response.put("response_type", "ephemeral");
        response.put("text", responseText);

        return response;
    }
}