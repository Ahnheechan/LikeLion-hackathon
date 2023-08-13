package com.power.likelion.domain.board;

import com.power.likelion.common.entity.AuditingFiled;
import com.power.likelion.domain.member.Member;
import com.power.likelion.domain.question.Answer;
import com.power.likelion.dto.board.BoardUpdateDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;



@NoArgsConstructor
@AllArgsConstructor
@Getter
@Entity
public class Board extends AuditingFiled {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false, length = 10000)
    private String content;

    @Column(nullable = false)
    private String boardType;

    @Column(nullable = false,columnDefinition = "integer default 0")
    private int viewCount;


    /** 연관된 데이터가 사용될 때 가져옴 즉, 사용되지 않으면 가져오지 않는다.
     *  이러한 방식은 성능을 개선하고 불필요한 데이터 로딩을 피할 수 있도록 도와줍니다.
     *  반대로 EAGER 타입의 경우 즉시 가져온다.
     * */
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="memberId")
    private Member member;

    @OneToMany(mappedBy = "board")
    private List<Comment> comments=new ArrayList<Comment>();

    @Builder
    public Board(String title, String content, String boardType, Member member) {
        this.title = title;
        this.content = content;
        this.boardType = boardType;
        this.member = member;
    }

    public void update(BoardUpdateDto boardUpdateDto,String boardType){
        this.title=boardUpdateDto.getTitle();
        this.content=boardUpdateDto.getContent();
        this.boardType=boardType;

    }
    public void updateView(){
        this.viewCount=this.viewCount+1;
    }

}
