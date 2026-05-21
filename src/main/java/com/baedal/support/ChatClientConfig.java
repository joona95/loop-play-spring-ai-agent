package com.baedal.support;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * ChatClient 빈을 엔드포인트별로 한 번만 빌드해 재사용한다.
 * (이전: 컨트롤러마다 요청 시점에 Builder.build() 호출 → 매 요청 빌드 비용 / advisor 매번 new)
 *
 * 빈 분리 원칙: 엔드포인트 간 설정이 같더라도 빈을 분리해두면
 * 이후 advisor·옵션 추가가 서로 영향 없이 가능해진다.
 */
@Configuration
public class ChatClientConfig {

    /** /api/v1/support — BaedalPrompt + 토큰·지연(advisor) + 요청·응답 본문 로깅. */
    @Bean
    ChatClient supportChatClient(ChatClient.Builder builder,
                                 PerformanceLoggingAdvisor performanceAdvisor) {
        return builder
                .defaultSystem(BaedalPrompt.SYSTEM_PROMPT)
                .defaultAdvisors(performanceAdvisor, new SimpleLoggerAdvisor())
                .build();
    }

    /** /api/v1/chat/stream — BaedalPrompt만 적용, advisor 없음(현 동작 유지). */
    @Bean
    ChatClient streamingChatClient(ChatClient.Builder builder) {
        return builder
                .defaultSystem(BaedalPrompt.SYSTEM_PROMPT)
                .build();
    }

    /** /api/v1/chat — 시스템 프롬프트·advisor 없는 순수 챗(현 동작 유지). */
    @Bean
    ChatClient plainChatClient(ChatClient.Builder builder) {
        return builder.build();
    }

    /**
     * /api/v1/prompt-lab — 시스템 프롬프트를 요청별로 주입해야 하므로
     * 빈에는 defaultSystem을 두지 않고, 호출 시 .prompt().system(...)로 오버라이드한다.
     * (이전: 매 요청 builder.defaultSystem(req.systemPrompt()).build())
     */
    @Bean
    ChatClient promptLabChatClient(ChatClient.Builder builder) {
        return builder.build();
    }
}
