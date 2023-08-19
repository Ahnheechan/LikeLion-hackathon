package com.power.likelion.dto.member;

import com.power.likelion.dto.question.PageInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.util.List;

@Getter
@NoArgsConstructor
@ToString
public class MemberActivityResDto {
    private String nickname;
    private String image;
    private int viewCnt;
    private List<MemberPageDto> list;
    private PageInfo pageInfo;

    @Builder
    public MemberActivityResDto(String nickname, String image,int viewCnt) {
        this.nickname = nickname;
        this.image = image;
        this.viewCnt=viewCnt;
    }

    public void setListAndPageInfo(List<MemberPageDto> list, PageInfo pageInfo) {
        this.list = list;
        this.pageInfo=pageInfo;
    }

}
