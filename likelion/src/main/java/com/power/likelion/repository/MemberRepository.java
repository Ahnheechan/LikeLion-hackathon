package com.power.likelion.repository;

import com.power.likelion.domain.member.Member;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface MemberRepository extends JpaRepository<Member,Long> {
    Optional<Member> findByEmail(String email);
    Optional<Member> findByNickname(String nickname);

    Optional<Member> findBySocialId(String socialId);

    Optional<Member> findTopByOrderByIdDesc();

}


