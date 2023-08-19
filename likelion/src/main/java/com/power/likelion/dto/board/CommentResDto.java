package com.power.likelion.dto.board;

import com.power.likelion.domain.board.Comment;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class CommentResDto {
    private Long commentId;
    private String content;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private Long memberId;

    @Builder
    public CommentResDto(Comment comment) {
        this.commentId = comment.getId();
        this.content = comment.getContent();
        this.createdBy = comment.getMember().getNickname();
        this.createdAt = comment.getCreatedAt();
        this.modifiedAt = comment.getModifiedAt();
        this.memberId=comment.getMember().getId();
    }


}
