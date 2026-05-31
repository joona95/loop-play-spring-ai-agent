package com.baedal.support;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.web.bind.annotation.*;

/**
 * Tool Calling이 적용된 자연어 응답 엔드포인트.
 * <p>
 * {@code /api/v1/support}가 Structured Output(JSON)을 반환하는 데 반해,
 * 이 엔드포인트는 <b>Tool 호출의 흐름을 평문으로 관찰</b>하기 위한 용도다.
 * DEBUG 로그와 함께 보면 Tool이 언제 어떻게 호출되는지 직관적으로 이해할 수 있다.
 * <p>
 * 빈 주입 방식: Week 1에서 정착한 "엔드포인트당 1개 ChatClient 빈" 원칙을 유지한다.
 * 매 요청마다 builder.build() 하지 않고 {@code assistantChatClient} 빈을 재사용한다.
 */
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/assistant")
public class AssistantController {

    @Qualifier("assistantChatClient")
    private final ChatClient assistantChatClient;

    @PostMapping
    public String ask(@Valid @RequestBody ChatRequest req) {
        return assistantChatClient.prompt()
                .user(req.message())
                .call()
                .content();
    }
}
