package com.power.likelion.utils.oauth2.userinfo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class KakaoProfile {
    private String id;
    KakaoAccount kakao_account;

    @Data
    public class KakaoAccount{
        private String email;
    }
}
