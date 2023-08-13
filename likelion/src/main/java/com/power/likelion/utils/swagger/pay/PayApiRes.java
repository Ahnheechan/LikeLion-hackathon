package com.power.likelion.utils.swagger.pay;


import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청에 성공하였습니다.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        examples = {
                                @ExampleObject(name = "성공 예제",
                                        value = "{\n" +
                                                "    \"tid\": \"T4d7b1a14ce74cf59940\",\n" +
                                                "    \"next_redirect_pc_url\": \"https://online-pay.kakao.com/mockup/v1/c6a3301654f295128ff739ad0f9e6d51dedcc83984bbcfd6fbd4d0cba711a559/info\",\n" +
                                                "    \"created_at\": \"2023-08-13T01:21:53\"\n" +
                                                "}",
                                        summary = "API 성공 예제", description = "요청에 성공한 경우의 예제입니다.")
                        })),
})
public @interface PayApiRes {
}
