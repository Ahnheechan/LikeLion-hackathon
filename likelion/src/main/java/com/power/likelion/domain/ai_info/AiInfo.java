package com.power.likelion.domain.ai_info;

import com.power.likelion.common.entity.AuditingFiled;
import com.power.likelion.domain.image.Image;
import com.power.likelion.domain.member.Member;
import com.power.likelion.dto.ai_info.AiInfoReqDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

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

    @OneToMany(mappedBy="aiInfo")
    private List<Image> image = new ArrayList<Image>();

    @Builder
    public AiInfo(Long id, String title, String content, Member member) {
        this.id = id;
        this.title = title;
        this.content = content;
        this.member = member;
    }

    public void updateAiInfo(AiInfoReqDto aiInfoReqDto){
        this.title=aiInfoReqDto.getTitle();
        this.content=aiInfoReqDto.getContent();
    }


    public void setImage(List<Image> images){
        this.image=images;
    }
    public void updateView(){
        this.viewCount=this.viewCount+1;
    }
}
