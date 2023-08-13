package com.power.likelion.domain.member;

import com.power.likelion.common.entity.AuditingFiled;
import com.power.likelion.dto.member.MemberUpdateReq;
import lombok.*;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
@Entity
@Table(name="member")
@Builder
@AllArgsConstructor
public class Member extends AuditingFiled {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = true,length = 40)  // TODO 나중에 다시 false
    private String nickname;

    @Column(nullable = false)
    private int age;

    @Column(nullable=false, length=50)
    private String email;

    @Column(nullable = true) // TODO 나중에 다시 false
    private String password;


    @Column(nullable = false)
    private int point;

    @Column(nullable = true)
    private String url;

    @Column(nullable = true)
    private String socialId; // 로그인한 소셜 타입의 식별자 값 (일반 로그인인 경우 null)

    @Column(nullable = true)
    private String tid;

    @OneToMany(mappedBy = "member", fetch = FetchType.EAGER, cascade = CascadeType.ALL)
    @Builder.Default
    private List<Authority> roles = new ArrayList<>();


    public void updateMember(MemberUpdateReq memberUpdateReq){
        this.age=memberUpdateReq.getAge();
        this.nickname=memberUpdateReq.getNickname();
    }
    public void setUrl(String url){
        this.url=url;
    }
    public void setRoles(List<Authority> role) {
        this.roles = role;
        role.forEach(o -> o.setMember(this));
    }
    public void minusPoint(int point){
        this.point=this.point-point;
    }
    public void plusPoint(int point){
        this.point=this.point+point;
    }

    public void updateTid(String tid){
        this.tid=tid;
    }



}
