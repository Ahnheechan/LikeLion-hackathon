package com.power.likelion.dto.sign_up;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class SignUpReqDto {
    @Schema(description = "이메일", example = "example@example.com")
    private String email;

    @Schema(description = "비밀번호", example = "1234")
    private String password;

    @Schema(description = "닉네임", example = "김부자")
    private String nickname;

    @Schema(description = "나이", example = "30")
    private int age;

    @Schema(description = "이미지",example="https://ncp-bucket-user.kr.object.ncloudstorage.com/aiinfo/921193333073900.jpg")
    private String url;

    public SignUpReqDto(String email, String password, String nickname, int age) {
        this.email = email;
        this.password = password;
        this.nickname = nickname;
        this.age = age;
    }
}
