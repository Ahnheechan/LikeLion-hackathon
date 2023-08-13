package com.power.likelion.utils.pay.request;


import com.power.likelion.utils.pay.PayInfoDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;

@Component
@RequiredArgsConstructor
public class MakePayRequest {

    public PayRequest getReadyRequest(Long id, PayInfoDto payInfoDto){
        LinkedMultiValueMap<String,String> map=new LinkedMultiValueMap<>();

        /** partner_user_id,partner_order_id는 결제 승인 요청에서도 동일해야함 */
        String memberId=id+"";

        String orderId="point"+id;
        // 가맹점 코드 테스트코드는 TC0ONETIME 이다.
        map.add("cid","TC0ONETIME");
        map.add("partner_order_id",orderId);
        map.add("partner_user_id","QAI");
        map.add("item_name",payInfoDto.getItemName());
        map.add("quantity","1");
        map.add("total_amount",payInfoDto.getPrice()+"");
        map.add("tax_free_amount", "0");
        map.add("approval_url", "http://27.96.131.106:8081/payment/success"+"/"+id); // 성공 시 redirect url
        map.add("cancel_url", "http://27.96.131.106:8081/payment/cancel"); // 취소 시 redirect url
        map.add("fail_url", "http://27.96.131.106:8081/payment/fail"); // 실패 시 redirect url

        return new PayRequest("https://kapi.kakao.com/v1/payment/ready",map);
    }

    public PayRequest getApproveRequest(String tid, Long id,String pgToken){
        LinkedMultiValueMap<String,String> map=new LinkedMultiValueMap<>();

        String orderId="point"+id;
        // 가맹점 코드 테스트코드는 TC0ONETIME 이다.
        map.add("cid", "TC0ONETIME");
        map.add("tid", tid);
        map.add("partner_order_id", orderId); // 주문명
        map.add("partner_user_id", "QAI");
        map.add("pg_token", pgToken);

        return new PayRequest("https://kapi.kakao.com/v1/payment/approve",map);
    }
}

/** 아래 내용을 만들어야 한다.
 * --data-urlencode "cid=TC0ONETIME" \
 * --data-urlencode "partner_order_id=partner_order_id" \
 * --data-urlencode "partner_user_id=partner_user_id" \
 * --data-urlencode "item_name=초코파이" \
 * --data-urlencode "quantity=1" \
 * --data-urlencode "total_amount=2200" \
 * --data-urlencode "vat_amount=200" \
 * --data-urlencode "tax_free_amount=0" \
 * --data-urlencode "approval_url=https://developers.kakao.com/success" \
 * --data-urlencode "fail_url=https://developers.kakao.com/fail" \
 * --data-urlencode "cancel_url=https://developers.kakao.com/cancel"
 *
 * */