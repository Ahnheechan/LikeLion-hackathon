package com.power.likelion.utils.pay;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

@Getter
public class PayInfoDto {
    @Schema(description = "가격", example = "10000")
    private int price;

    @Schema(description = "상품 이름", example = "10000 포인트")
    private String itemName;
}
