package com.power.likelion.dto.question;

import com.power.likelion.common.entity.CheckStatus;
import com.power.likelion.domain.question.Question;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Getter
@NoArgsConstructor
public class AllQuesResDto {

    private Long questionId;
    private String title;
    private String content;
    private int viewCount;
    private String createdBy;
    private String checkStatus;
    private int point;
    private int answerCnt;
    private LocalDateTime createdAt;


    public AllQuesResDto(Question question) {
        this.questionId= question.getId();
        this.title = question.getTitle();
        this.content = question.getContent();
        this.viewCount = question.getViewCount();
        this.createdBy = question.getMember().getNickname();
        this.point = question.getPoint();
        this.createdAt = question.getCreatedAt();
        this.checkStatus=question.getQuestionCheck().getDescription();
        this.answerCnt=question.getAnswers().size();
    }
}
