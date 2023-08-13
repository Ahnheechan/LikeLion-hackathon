package com.power.likelion.utils.swagger.AiInfo;


import com.power.likelion.dto.ai_info.AiInfoReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Ai 게시글 전체 조회,검색", description = "Ai 소개글을 페이징으로 전체 조회와 검색을 한다." +
        "\n\n 질문과 통합게시판과 같은 원리로 작동하니 해당 내용을 참고해 주세요")
public @interface GetInfosApiReq {
}
