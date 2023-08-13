package com.power.likelion.controller;

import com.power.likelion.common.response.BaseResponse;
import com.power.likelion.common.response.SignStatus;
import com.power.likelion.dto.login.GetInfoRes;
import com.power.likelion.dto.login.LoginDto;
import com.power.likelion.dto.login.LoginResDto;
import com.power.likelion.dto.member.MemberUpdateReq;
import com.power.likelion.dto.sign_up.SignUpReqDto;
import com.power.likelion.dto.sign_up.SingUpResDto;
import com.power.likelion.service.MemberService;
import com.power.likelion.utils.swagger.getinfo.GetInfoApiRequest;
import com.power.likelion.utils.swagger.getinfo.GetInfoApiResponse;
import com.power.likelion.utils.swagger.login.LoginApiRequest;
import com.power.likelion.utils.swagger.login.LoginApiResponse;
import com.power.likelion.utils.swagger.member.MemberUpdateApiReq;
import com.power.likelion.utils.swagger.sign_up.SignUpApiRequest;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.power.likelion.utils.swagger.sign_up.SignUpApiResponse;

@Tag(name = "로그인, 회원가입, 내정보 조회", description = "회원가입을 진행하고 로그인을 수행해 본다 그리고 JWT 토큰을 이용하여 인증 절차를 확인한다.")
@RestController
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    @SignUpApiRequest
    @SignUpApiResponse
    @PostMapping("/sign-up")
    public ResponseEntity<SingUpResDto> createMember(@RequestBody SignUpReqDto signUpReqDto){
        try{

            memberService.createMember(signUpReqDto);
        }
        catch(IllegalAccessException e){

            SingUpResDto singUpResDto=SingUpResDto.builder()
                    .message(e.getMessage())
                    .status(SignStatus.EXIST_EMAIL)
                    .data(null)
                    .build();


            return ResponseEntity
                    .status(HttpStatus.CONFLICT.value())
                    .body(singUpResDto)
                    ;



        }

        return ResponseEntity
                .ok(SingUpResDto.builder()
                        .data(signUpReqDto.getEmail())
                        .message("회원가입에 성공하였습니다")
                        .status(SignStatus.OK)
                        .build()
                );
    }



    @LoginApiRequest
    @LoginApiResponse
    @PostMapping("/login")
    public ResponseEntity<LoginResDto> checkMember(@RequestBody LoginDto loginDto){

        try {

             LoginResDto loginResDto=memberService.login(loginDto);
             return ResponseEntity
                     .ok(loginResDto);
        }
        catch(Exception e){

            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND.value())
                    .body(LoginResDto.builder()
                            .status(SignStatus.LOGIN_FAILED)
                            .message(e.getMessage())
                            .build())
                    ;
        }



    }

    /** 내 정보 조회  */

    @GetInfoApiRequest
    @GetInfoApiResponse
    @GetMapping("/get-info")
    public ResponseEntity<GetInfoRes> getUser(@RequestParam String email) throws Exception {
        return new ResponseEntity<>( memberService.getMember(email), HttpStatus.OK);
    }


    /** 내 정보 수정  */
    @MemberUpdateApiReq
    @GetInfoApiResponse
    @PostMapping("/update/info")
    public ResponseEntity<?> updateUser(@RequestBody MemberUpdateReq memberUpdateReq){
        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                        .result(memberService.updateUser(memberUpdateReq))
                        .build());
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }


    /** 관리자 계정 생성 */
    @PostMapping("/admin/sign-up")
    public ResponseEntity<?> createAdmin(@RequestBody SignUpReqDto signUpReqDto){
        try{
            memberService.createAdmin(signUpReqDto);
            return ResponseEntity
                    .ok(SingUpResDto.builder()
                            .data(signUpReqDto.getEmail())
                            .message("회원가입에 성공하였습니다")
                            .status(SignStatus.OK)
                            .build()
                    );
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }



    }


}
