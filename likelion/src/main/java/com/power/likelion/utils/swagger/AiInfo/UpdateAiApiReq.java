package com.power.likelion.utils.swagger.AiInfo;


import com.power.likelion.dto.ai_info.AiInfoReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Ai 게시글 수정", description = "수정가능 한 내용은 제목, 내용, 이미지 입니다.")
@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "AI 게시글 수정 사항", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = AiInfoReqDto.class)))
public @interface UpdateAiApiReq {
}
