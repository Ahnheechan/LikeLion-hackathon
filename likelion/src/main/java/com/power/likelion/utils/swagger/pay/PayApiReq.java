package com.power.likelion.utils.swagger.pay;

import com.power.likelion.dto.member.MemberUpdateReq;
import com.power.likelion.utils.pay.PayInfoDto;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import org.springframework.http.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@Operation(summary = "카카오 페이 결제", description = "상품이름과 가격을 보내주세요" +
        "\n\n 해당 내용을 바탕으로 결제 url이 만들어져서 Response로 제공 될 예정입니다." +
        "\n\n 따라서, 해당 url로 클라이언트를 redirect 시켜주면 됩니다.")
@io.swagger.v3.oas.annotations.parameters.RequestBody(description = "결제 정보", required = true, content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
        schema = @Schema(implementation = PayInfoDto.class)))
public @interface PayApiReq {
}
