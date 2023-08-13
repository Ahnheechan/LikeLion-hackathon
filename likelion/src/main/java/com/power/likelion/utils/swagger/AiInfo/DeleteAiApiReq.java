package com.power.likelion.utils.swagger.AiInfo;

import io.swagger.v3.oas.annotations.Operation;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "Ai 게시글 삭제", description = "Ai 소개글 ID를 보내주면 해당 게시글이 삭제된다.")
public @interface DeleteAiApiReq {
}
