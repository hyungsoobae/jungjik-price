package com.zz95.jungjik.global.slack.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record SlackMessageRequest(
        List<Attachment> attachments
) {
    // Slack 메시지 내 가독성을 높이기 위한 첨부 카드 구조 (Attachment)
    public record Attachment(
        // 카드 왼쪽 사이드바의 색상 (예: #ff0000)
        String color,

        // 제목(title) 바로 위에 나타나는 짧은 요약 텍스트
        String pretext,

        // 카드의 메인 제목
        String title,

        // 제목을 클릭했을 때 연결될 URL
        @JsonProperty("title_link") String titleLink,

        // 카드의 본문 내용 (마크다운 지원)
        String text,

        // 하단에 작게 표시될 정보 (예: 서비스명)
        String footer,

        // 하단 푸터 옆에 표시될 타임스탬프 (epoch time)
        Long ts,

        // 표 형식으로 정보를 나열할 때 사용하는 필드 리스트
        List<Field> fields
    ) {}

    // Attachment 내부에 표(Grid) 형태로 표시되는 세부 필드
    public record Field(
            // 필드 제목
            String title,

            // 필드 내용
            String value,

            // 가로 전체를 차지할지(false), 옆 필드와 나란히 배치할지(true) 여부
            @JsonProperty("short") boolean isShort
    ) {}
}