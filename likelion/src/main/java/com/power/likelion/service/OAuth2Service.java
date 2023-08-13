package com.power.likelion.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.power.likelion.common.response.SignStatus;
import com.power.likelion.domain.member.Authority;
import com.power.likelion.domain.member.Member;
import com.power.likelion.dto.login.LoginResDto;
import com.power.likelion.repository.MemberRepository;
import com.power.likelion.utils.jwts.JwtProvider;
import com.power.likelion.utils.oauth2.oauthrequest.OAuth2Request;
import com.power.likelion.utils.oauth2.oauthrequest.OAuthRequestFactory;
import com.power.likelion.utils.oauth2.token.OAuth2AccessToken;
import com.power.likelion.utils.oauth2.userinfo.GoogleProfile;
import com.power.likelion.utils.oauth2.userinfo.KakaoProfile;
import com.power.likelion.utils.oauth2.userinfo.NaverProfile;
import com.power.likelion.utils.oauth2.userinfo.ProfileDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;

@Service
@RequiredArgsConstructor
public class OAuth2Service {

    private final OAuthRequestFactory oAuthRequestFactory;
    private final MemberRepository memberRepository;
    private final JwtProvider jwtProvider;

    @Transactional
    public OAuth2AccessToken getAccessToken(String code, String provider) {

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // token 주소와, 토큰 주소로 보낼 http request를 갖고있는 oAuth2Request

        OAuth2Request oAuth2Request=oAuthRequestFactory.getRequest(code,provider);



        // 헤더와 바디 합치기 위해 Http Entity 객체 생성
        HttpEntity<MultiValueMap<String, String>> tokenRequest = new HttpEntity<>(oAuth2Request.getMap(), headers);


        // 카카오로, 구글, 네이버로부터 Access token 받아오기
        RestTemplate rt = new RestTemplate();
        ResponseEntity<String> tokenResponse = rt.postForEntity(oAuth2Request.getUrl(), tokenRequest, String.class);


        // JSON Parsing (-> KakaoTokenDto)
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        OAuth2AccessToken oAuth2AccessToken = null;
        try {
            oAuth2AccessToken = objectMapper.readValue(tokenResponse.getBody(), OAuth2AccessToken.class);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return oAuth2AccessToken;
    }

    @Transactional
    public ProfileDto getProfile(String accessToken, String provider) throws Exception {
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        httpHeaders.set("Authorization", "Bearer " + accessToken);

        String profileUrl = oAuthRequestFactory.getProfileUrl(provider);
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(null, httpHeaders);
        RestTemplate rt = new RestTemplate();

        //카카오, 네이버, 구글 유저 프로필 정보 받아오기
        ResponseEntity<String> response = rt.postForEntity(profileUrl, request, String.class);



        if (response.getStatusCode() == HttpStatus.OK) {
            return extractProfile(response, provider);
        }
        else{
            throw new Exception("해당 access token에 대한 유저 정보를 불러올 수 없습니다.");
        }
    }




    private ProfileDto extractProfile(ResponseEntity<String> response, String provider) throws Exception {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        if (provider.equals("kakao")) {

            KakaoProfile kakaoProfile = objectMapper.readValue(response.getBody(), KakaoProfile.class);

            return new ProfileDto(kakaoProfile.getId(),kakaoProfile.getKakao_account().getEmail());

        } else if(provider.equals("google")) {
            GoogleProfile googleProfile = objectMapper.readValue(response.getBody(), GoogleProfile.class);
            return new ProfileDto(googleProfile.getSub(),googleProfile.getEmail());

        } else {
            NaverProfile naverProfile = objectMapper.readValue(response.getBody(), NaverProfile.class);
            return new ProfileDto(naverProfile.getResponse().getId(),naverProfile.getResponse().getEmail());
        }
    }


    @Transactional
    public LoginResDto findUser(ProfileDto profileDto,String provider){
        try {
            Member member = memberRepository.findBySocialId(profileDto.getId())
                    .orElseThrow(()->new Exception("유저가 존재하지 않습니다."));

            return LoginResDto.builder()
                    .message("로그인 성공")
                    .status(SignStatus.OK)
                    .nickname(member.getNickname())
                    .email(member.getEmail())
                    .age(member.getAge())
                    .jwtToken(jwtProvider.createToken(member.getEmail(), member.getRoles()))
                    .point(member.getPoint())
                    .build();

        }
        catch(Exception e){
            String nickname;
            if(memberRepository.findTopByOrderByIdDesc().isEmpty()){
                nickname="1";
            }
            else{
                Long id=memberRepository.findTopByOrderByIdDesc().get().getId()+1;
                nickname=id+"";
            }

            memberRepository.findTopByOrderByIdDesc().get().getId().toString();
            Member member = Member.builder()
                    .nickname(provider+nickname)
                    .email(profileDto.getEmail())
                    .age(0)
                    .socialId(profileDto.getId())
                    .point(100)
                    .build();
            member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

            memberRepository.save(member);

            return LoginResDto.builder()
                    .message("첫 소셜 로그인 입니다. 닉네임이 임의로 설정되어있으므로 수정 바랍니다. 나이 초기 설정 값은 NULL 입니다.")
                    .status(SignStatus.OK)
                    .nickname(member.getNickname())
                    .email(member.getEmail())
                    .age(member.getAge())
                    .jwtToken(jwtProvider.createToken(member.getEmail(), member.getRoles()))
                    .point(member.getPoint())
                    .build();
        }

    }






}
