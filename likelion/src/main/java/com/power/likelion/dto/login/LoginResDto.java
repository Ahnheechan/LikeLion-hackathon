package com.power.likelion.dto.login;

import com.power.likelion.common.response.SignStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class LoginResDto {
    private SignStatus status;
    private String message;
    private String nickname;
    private String email;
    private int age;
    private String jwtToken;
    private int point;


    @Builder
    public LoginResDto(SignStatus status, String message, String nickname, String email,int age, String jwtToken,int point) {
        this.status = status;
        this.message = message;
        this.nickname = nickname;
        this.email = email;
        this.age=age;
        this.jwtToken = jwtToken;
        this.point=point;
    }
}
