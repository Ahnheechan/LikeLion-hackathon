package com.power.likelion.dto.board;

import com.power.likelion.domain.board.Board;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
public class BoardResDto {
    private Long boardId;
    private String title;
    private String content;
    private String boardType;
    private String createdBy;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private List<CommentResDto> comments;
    private int commentCnt;

    @Builder
    public BoardResDto(Board board) {
        this.boardId = board.getId();
        this.title = board.getTitle();
        this.content = board.getContent();
        this.boardType = board.getBoardType();
        this.createdBy = board.getMember().getNickname();
        this.viewCount = board.getViewCount();
        this.createdAt = board.getCreatedAt();
        this.modifiedAt = board.getModifiedAt();
        this.commentCnt=board.getComments().size();
    }


    public void setComments(List<CommentResDto> comments){
        this.comments=comments;
    }
}
