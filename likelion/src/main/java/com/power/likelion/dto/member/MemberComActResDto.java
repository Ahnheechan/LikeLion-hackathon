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
public class MemberComActResDto {
    private String nickname;
    private String image;
    private int viewCnt;
    private List<MemberPageDto> commentList;
    private List<MemberPageDto> answerList;
    private PageInfo commentInfo;
    private PageInfo answerInfo;


    @Builder
    public MemberComActResDto(String nickname, String image, int viewCnt) {
        this.nickname = nickname;
        this.image = image;
        this.viewCnt = viewCnt;
    }



    public void setListAndPageInfo(List<MemberPageDto> commentList, PageInfo commentInfo, List<MemberPageDto> answerList, PageInfo answerInfo ) {
        this.commentList=commentList;
        this.answerList=answerList;
        this.answerInfo=answerInfo;
        this.commentInfo=commentInfo;
    }
}
