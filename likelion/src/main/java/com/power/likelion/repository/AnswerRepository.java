package com.power.likelion.repository;

import com.power.likelion.domain.question.Answer;
import com.power.likelion.domain.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface AnswerRepository extends JpaRepository<Answer, Long> {
    List<Answer> findByQuestionId(Long id);

    Page<Answer> findAllByMemberId(Long Id, Pageable pageable);
}
