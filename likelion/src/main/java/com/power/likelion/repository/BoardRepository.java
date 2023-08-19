package com.power.likelion.repository;

import com.power.likelion.domain.board.Board;
import com.power.likelion.domain.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;


public interface BoardRepository extends JpaRepository<Board,Long> {
    Page<Board> findAll(Pageable pageable);

    Page<Board> findByTitleContaining(String searchKeyword, Pageable pageable);
    Page<Board> findByTitleContainingAndBoardTypeContaining(String searchKeyword, String boardType, Pageable pageable);

    Page<Board> findByContentContaining(String searchKeyword, Pageable pageable);
    Page<Board> findByContentContainingAndBoardTypeContaining(String searchKeyword, String boardType, Pageable pageable);
    Page<Board> findByTitleContainingOrContentContaining(String title,String content, Pageable pageable);
    Page<Board> findByTitleContainingOrContentContainingAndBoardTypeContaining(String title, String content, String boardType, Pageable pageable);

    Page<Board> findByBoardTypeContaining(String boardType, Pageable pageable);

    Page<Board> findAllByMemberId(Long Id, Pageable pageable);
}
