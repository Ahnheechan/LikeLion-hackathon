package com.power.likelion.dto.question;

import com.power.likelion.domain.question.Answer;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class AnswerResDto {
    private Long id;
    private String content;
    private String answerCheck;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long memberId;

    @Builder
    public AnswerResDto(Answer answer) {
        this.id = answer.getId();
        this.content = answer.getContent();
        this.answerCheck = answer.getAnswerCheck().getDescription();
        this.createdBy = answer.getMember().getNickname();
        this.createdAt = answer.getCreatedAt();
        this.modifiedAt = answer.getModifiedAt();
        this.memberId=answer.getMember().getId();
    }
}
