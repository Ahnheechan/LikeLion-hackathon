package com.power.likelion.repository;

import com.power.likelion.domain.ai_info.AiInfo;
import com.power.likelion.domain.question.Question;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiInfoRepository extends JpaRepository<AiInfo,Long> {
    Page<AiInfo> findByTitleContaining(String searchKeyword, Pageable pageable);
    Page<AiInfo> findByContentContaining(String searchKeyword, Pageable pageable);

    Page<AiInfo> findByTitleContainingOrContentContaining(String title, String content, Pageable pageable);
}
