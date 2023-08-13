package com.power.likelion.controller;

import com.power.likelion.common.response.BaseResponse;
import com.power.likelion.dto.login.LoginResDto;
import com.power.likelion.service.OAuth2Service;
import com.power.likelion.utils.oauth2.userinfo.ProfileDto;
import com.power.likelion.utils.swagger.OAuth2.OAuth2LoginApiRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Tag(name = "소셜 로그인", description = "코드를 제공하면 해당 코드로 소셜 플랫폼에서 회원정보를 받아서 회원 가입까지 진행시킨다." +
        "\n\n 닉네임은 임의로 설정되고 나이는 0으로 설정되어서 회원이 생성됨")
@RestController
@RequiredArgsConstructor
@Slf4j
public class OAuth2Controller {

    private final OAuth2Service oAuth2Service;

    @OAuth2LoginApiRes
    @GetMapping("/login/oauth2/callback/kakao")
    public ResponseEntity<?> kakaoLogin(@RequestParam("code") String code) {

        String kakaoAccessToken = oAuth2Service.getAccessToken(code,"kakao").getAccess_token();

        try {

            ProfileDto profileDto = oAuth2Service.getProfile(kakaoAccessToken, "kakao");


            LoginResDto loginResDto=oAuth2Service.findUser(profileDto,"kakao");

            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder().result(loginResDto).build());

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @OAuth2LoginApiRes
    @GetMapping("/login/oauth2/callback/google")
    public ResponseEntity<?> googleLogin(@RequestParam("code") String code) {
        log.info("Access 토큰 얻기");
        String googleAccessToken = oAuth2Service.getAccessToken(code,"google").getAccess_token();
        log.info("AccessToke{}",googleAccessToken);

        try {
            log.info("profile 받아오기");
            ProfileDto profileDto = oAuth2Service.getProfile(googleAccessToken, "google");
            log.info("profileDto:{}",profileDto);

            LoginResDto loginResDto=oAuth2Service.findUser(profileDto,"google");

            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder().result(loginResDto).build());

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @OAuth2LoginApiRes
    @GetMapping("/login/oauth2/callback/naver")
    public ResponseEntity<?> naverLogin(@RequestParam("code") String code) {


        String naverAccessToken = oAuth2Service.getAccessToken(code,"naver").getAccess_token();


        try {

            ProfileDto profileDto = oAuth2Service.getProfile(naverAccessToken, "naver");


            LoginResDto loginResDto=oAuth2Service.findUser(profileDto,"naver");

            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder().result(loginResDto).build());

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }
}
