package com.power.likelion.dto.ai_info;


import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Getter;

import java.util.List;

@Getter
public class AiInfoReqDto {

    @Schema(name = "title",example = "Chat Gpt")
    private String title;

    @Schema(name = "content",example = "Chat Gpt는 Open Ai의 채팅형 AI로 사람들에게 AI를 각인 시켜준 장본인이다.")
    private String content;

    @Schema(name = "url",example = "https://ncp-bucket-user.kr.object.ncloudstorage.com/aiinfo/921193333073900.jpg")
    private String url;


}
