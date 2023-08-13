package com.power.likelion.dto.ai_info;


import com.power.likelion.dto.question.PageInfo;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.util.List;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AiInfoPageResDto {
    private PageInfo pageInfo;
    private List<AiInfoAllResDto> aiInfoList;

}
