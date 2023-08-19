package com.power.likelion.dto.login;

import com.power.likelion.common.response.SignStatus;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Getter
@NoArgsConstructor
@ToString
public class GetInfoRes {
    private SignStatus status;
    private String message;
    private String email;
    private String nickname;
    private int age;
    private int point;
    private String url;
    private int viewCnt;

    @Builder
    public GetInfoRes(SignStatus status, String message, String email, String nickname, int age,int point,String url,int viewCnt) {
        this.status = status;
        this.message = message;
        this.email = email;
        this.nickname = nickname;
        this.age = age;
        this.point=point;
        this.url=url;
        this.viewCnt=viewCnt;
    }


}
