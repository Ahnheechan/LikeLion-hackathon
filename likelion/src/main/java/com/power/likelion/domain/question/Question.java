package com.power.likelion.domain.question;

import com.power.likelion.common.entity.AuditingFiled;
import com.power.likelion.common.entity.CheckStatus;
import com.power.likelion.domain.member.Member;
import com.power.likelion.dto.question.QuesUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Getter
public class Question extends AuditingFiled {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false ,length = 10000)
    private String content;

    @Column(nullable =false)
    private int point;

    @Column( nullable = false,columnDefinition = "integer default 0")
    private int viewCount;

    @Column(nullable = true)
    private String image;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name ="memberId")
    private Member member;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private CheckStatus questionCheck;


    @OneToMany(mappedBy = "question")
    private List<Answer> answers=new ArrayList<Answer>();

    @Builder
    public Question(String title, String content, int point, int viewCount, Member member, CheckStatus questionCheck,String image) {
        this.title = title;
        this.content = content;
        this.point = point;
        this.viewCount = viewCount;
        this.member = member;
        this.questionCheck = questionCheck;
        this.image=image;
    }

    public void update(QuesUpdateDto quesUpdateDto){
        this.title=quesUpdateDto.getTitle();
        this.content=quesUpdateDto.getContent();
        this.point=quesUpdateDto.getPoint();
        this.image=quesUpdateDto.getImage();
    }

    public void updateView(){
        this.viewCount=this.viewCount+1;
    }

    public void changeCheck(){
        this.questionCheck=CheckStatus.True;
    }
}
