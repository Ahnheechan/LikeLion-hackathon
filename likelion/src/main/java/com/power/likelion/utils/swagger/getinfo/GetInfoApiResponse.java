package com.power.likelion.utils.swagger.getinfo;

import com.power.likelion.dto.sign_up.SingUpResDto;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import org.springframework.http.MediaType;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
@ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "요청된 정보입니다.",
                content = @Content(mediaType = MediaType.APPLICATION_JSON_VALUE,
                        examples = {
                                @ExampleObject(name = "성공 예제",
                                        value = "{\n" +
                                                "    \"status\": \"OK\",\n" +
                                                "    \"message\": \"요청된 정보입니다.\",\n" +
                                                "    \"email\": \"ahc700325@gmail.com\",\n" +
                                                "    \"nickname\": \"김땡땡\",\n" +
                                                "    \"age\": 24,\n" +
                                                "    \"point\": 100,\n" +
                                                "    \"url\": \"https://ncp-bucket-user.kr.object.ncloudstorage.com/profile/1299307499671500.jpg\"\n" +
                                                "}",
                                        summary = "API 성공 예제", description = "요청된 정보의 예제입니다.")
                        }))
})
public @interface GetInfoApiResponse {
}
