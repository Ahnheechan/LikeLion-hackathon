package com.power.likelion.domain.ai_info;

import com.power.likelion.common.entity.AuditingFiled;

import com.power.likelion.domain.member.Member;
import com.power.likelion.dto.ai_info.AiInfoReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class AiInfo extends AuditingFiled {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false,length = 10000)
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberId")
    private Member member;

    @Column(nullable = false,columnDefinition = "integer default 0")
    private int viewCount;

    @Column(nullable = true)
    private String image ;

    @Builder
    public AiInfo(Long id, String title, String content, Member member,String image) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.member = member;
        this.image=image;
    }

    public void updateAiInfo(AiInfoReqDto aiInfoReqDto){
        this.title=aiInfoReqDto.getTitle();
        this.content=aiInfoReqDto.getContent();
        this.image=aiInfoReqDto.getUrl();
    }


    public void updateView(){
        this.viewCount=this.viewCount+1;
    }
}
