package com.example.schoolalarmservice.telegram;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.telegram.telegrambots.meta.api.methods.BotApiMethod;
import org.telegram.telegrambots.meta.api.objects.Update;

@RestController
@RequiredArgsConstructor
public class TelegramWebhookController {

    // 우리가 만든 봇 클래스를 주입받습니다.
    private final MySchoolAlarmWebhookBot telegramBot;

    // 텔레그램 서버가 HTTP POST로 보내는 데이터를 여기서 받습니다.
    @PostMapping("/api/telegram")
    public BotApiMethod<?> onUpdateReceived(@RequestBody Update update) {

        // 받은 데이터를 봇의 onWebhookUpdateReceived 메서드로 토스합니다!
        // 그리고 봇이 처리한 결과(SendMessage 객체)를 텔레그램 서버로 바로 Return(응답) 합니다.
        return telegramBot.onWebhookUpdateReceived(update);
    }
}
