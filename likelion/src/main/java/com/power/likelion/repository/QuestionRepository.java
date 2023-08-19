package com.power.likelion.repository;

import com.power.likelion.common.entity.CheckStatus;
import com.power.likelion.domain.member.Member;
import com.power.likelion.domain.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    Page<Question> findAll(Pageable pageable);
    Page<Question> findByQuestionCheckAndTitleContaining(CheckStatus status,String searchKeyword, Pageable pageable);
    Page<Question> findByQuestionCheckAndContentContaining(CheckStatus status,String searchKeyword, Pageable pageable);

    Page<Question> findByQuestionCheckAndTitleContainingOrContentContaining(CheckStatus status, String title,String content, Pageable pageable);

    Page<Question> findAllByMemberId(Long Id,Pageable pageable);

    Page<Question> findAllByQuestionCheck(CheckStatus status, Pageable pageable);
}

