package com.power.likelion.dto.question;

import lombok.Builder;
import lombok.Getter;

@Getter
public class QuesUpdateDto {
    private String title;
    private String content;
    private int point;
    private String image;

    @Builder
    public QuesUpdateDto(String title, String content, int point,String image) {
        this.title = title;
        this.content = content;
        this.point = point;
        this.image=image;
    }
}
