package com.power.likelion.dto.ai_info;

import com.power.likelion.domain.ai_info.AiInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class AiInfoResDto {
    private Long id;
    private String title;
    private String content;
    private String createdBy;
    private int viewCount;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String url;

    @Builder
    public AiInfoResDto(AiInfo aiInfo) {
        this.id = aiInfo.getId();
        this.title = aiInfo.getTitle();
        this.content = aiInfo.getContent();
        this.createdAt=aiInfo.getCreatedAt();
        this.modifiedAt=aiInfo.getModifiedAt();
        this.createdBy = aiInfo.getMember().getNickname();
        this.viewCount=aiInfo.getViewCount();
        this.url=aiInfo.getImage();
    }

}
