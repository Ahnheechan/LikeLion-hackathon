package com.power.likelion.repository;

import com.power.likelion.domain.board.Comment;
import com.power.likelion.domain.question.Answer;
import com.power.likelion.domain.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CommentRepository extends JpaRepository<Comment,Long> {

    List<Comment> findByBoardId(Long id);

    Page<Comment> findAllByMemberId(Long Id, Pageable pageable);
}
