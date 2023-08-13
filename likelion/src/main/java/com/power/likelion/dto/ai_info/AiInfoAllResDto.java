package com.power.likelion.dto.ai_info;

import com.power.likelion.domain.ai_info.AiInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;


/** 페이징으로 갈  AI INFO 정보 */
@Getter
@NoArgsConstructor
public class AiInfoAllResDto {
    private Long aiInfoId;
    private String title;
    private int viewCount;
    private String createdBy;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;

    @Builder
    public AiInfoAllResDto(AiInfo aiInfo) {
        this.aiInfoId = aiInfo.getId();
        this.title = aiInfo.getTitle();
        this.viewCount = aiInfo.getViewCount();
        this.createdBy = aiInfo.getMember().getNickname();
        this.createdAt = aiInfo.getCreatedAt();
        this.modifiedAt = aiInfo.getModifiedAt();

    }
}
