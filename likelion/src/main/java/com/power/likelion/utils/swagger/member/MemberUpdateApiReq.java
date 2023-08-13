package com.power.likelion.utils.swagger.member;


import com.power.likelion.dto.member.MemberUpdateReq;
import com.power.likelion.dto.sign_up.SignUpReqDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "회원 정보 수정", description = "닉네임, 나이, 프로필 이미지만 수정가능" +
        "\n\n 이미지의 경우 이미지 업로드를 통해 얻은 url을 넣어주면 됩니다.")
@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "회원 정보", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = MemberUpdateReq.class)))
public @interface MemberUpdateApiReq {
}
