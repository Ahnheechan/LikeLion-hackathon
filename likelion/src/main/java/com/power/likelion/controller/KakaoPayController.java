package com.power.likelion.controller;

import com.power.likelion.common.response.BaseResponse;
import com.power.likelion.service.KakaoPayService;
import com.power.likelion.utils.pay.response.PayApproveResDto;
import com.power.likelion.utils.pay.PayInfoDto;
import com.power.likelion.utils.swagger.pay.PayApiReq;
import com.power.likelion.utils.swagger.pay.PayApiRes;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@Tag(name = "카카오 결제", description = "결제 기능을 통해 결제 서비스를 이용해 볼 수 있다. /payment/ready만 직접적으로 사용가능하고" +
        "\n\n 나머지 기능은 redirect 되는 url 들이다. 따라서 /payment/ready만 구현하면 된다.")
public class KakaoPayController {

    private final KakaoPayService kakaoPayService;

    /** 결제 준비 redirect url 받기 --> 상품명과 가격을 같이 보내줘야함 */
    @PayApiReq
    @PayApiRes
    @GetMapping("/ready")
    public ResponseEntity<?> getRedirectUrl(@RequestBody PayInfoDto payInfoDto) {
        try {
            return ResponseEntity.status(HttpStatus.OK)
                    .body(kakaoPayService.getRedirectUrl(payInfoDto));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));

        }
    }
    /**
     * 결제 성공 pid 를  받기 위해 request를 받고 pgToken은 rediret url에 뒤에 붙어오는걸 떼서 쓰기 위함
     */
    @GetMapping("/success/{id}")
    public ResponseEntity<?> afterGetRedirectUrl(@PathVariable("id")Long id,
                                                 @RequestParam("pg_token") String pgToken) {
        try {
            PayApproveResDto kakaoApprove = kakaoPayService.getApprove(pgToken,id);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(kakaoApprove);
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }



    /**
     * 결제 진행 중 취소
     */
    @GetMapping("/cancel")
    public ResponseEntity<?> cancel() {
        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body(new BaseResponse<>(HttpStatus.EXPECTATION_FAILED.value(),"사용자가 결제를 취소하였습니다."));
    }

    /**
     * 결제 실패
     */
    @GetMapping("/fail")
    public ResponseEntity<?> fail() {

        return ResponseEntity.status(HttpStatus.EXPECTATION_FAILED)
                .body(new BaseResponse<>(HttpStatus.EXPECTATION_FAILED.value(),"결제가 실패하였습니다."));

    }

}
