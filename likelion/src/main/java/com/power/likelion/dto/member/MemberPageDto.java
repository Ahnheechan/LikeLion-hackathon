package com.power.likelion.dto.member;

import com.power.likelion.domain.question.Question;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@NoArgsConstructor
@Getter
@ToString
public class MemberPageDto {
    private String title;
    private LocalDateTime createdAt;
    private String boardType;

    // 게시글 id, 댓글이나 답변같은경우 질문 id, 게시글 id 제공
    private Long id;



    @Builder
    public MemberPageDto(String title, LocalDateTime createdAt,Long id,String boardType) {
        this.title = title;
        this.createdAt = createdAt;
        this.id=id;
        this.boardType=boardType;
    }


}
