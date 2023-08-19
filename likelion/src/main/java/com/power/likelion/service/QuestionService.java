package com.power.likelion.service;


import com.power.likelion.common.entity.CheckStatus;
import com.power.likelion.common.exception.AuthorMismatchException;
import com.power.likelion.domain.member.Member;
import com.power.likelion.domain.question.Question;
import com.power.likelion.dto.question.*;
import com.power.likelion.repository.MemberRepository;
import com.power.likelion.repository.QuestionRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.expression.ExpressionException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.BadLocationException;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class QuestionService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final AnswerSerivce answerSerivce;

    @Transactional
    public QuesResDto createQuestion(QuesReqDto quesReqDto)throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        Member member=memberRepository.findByEmail(name)
                .orElseThrow(()->new Exception("유저가 존재하지 않습니다."));

        if(quesReqDto.getPoint()<=0){
            throw new Exception("0포인트 이하로는 질문을 작성할 수 없습니다.");
        }

        if(member.getPoint()<quesReqDto.getPoint()){
            throw new Exception("현재 가지고 있는 포인트보다 질문 작성에 사용한 포인트가 많습니다.");
        }

        member.minusPoint(quesReqDto.getPoint());

        Question question=Question.builder()
                .point(quesReqDto.getPoint())
                .title(quesReqDto.getTitle())
                .content(quesReqDto.getContent())
                .questionCheck(CheckStatus.False)
                .member(member)
                .image(quesReqDto.getImage())
                .build();


        Question resQuestion =questionRepository.save(question);
        log.info("{}:",resQuestion);

        return QuesResDto.builder()
                .question(resQuestion)
                .build();

    }

    /** 페이징 처리로 size개의 게시글이 간다. */
    @Transactional
    public QuesPageResDto getQuestions(int page,int size,String checkStatus){
        Page<Question> questions;

        /** 채택과 채택전으로 나눔 */
        if(checkStatus.equals("채택")){
            questions = questionRepository.findAllByQuestionCheck(CheckStatus.True, PageRequest.of(page, size));
        }
        else{
            questions=questionRepository.findAllByQuestionCheck(CheckStatus.False,PageRequest.of(page, size));
        }


        PageInfo pageInfo= PageInfo.builder()
                .page(page)
                .pageSize(size)
                .totalPages(questions.getTotalPages())
                .totalNumber(questions.getTotalElements())
                .build();

        List<AllQuesResDto> results = questions.getContent().stream().map(question -> new AllQuesResDto(question)
        ).collect(Collectors.toList());


        return QuesPageResDto.builder()
                .pageInfo(pageInfo)
                .questionList(results)
                .build();

    }


    /** 페이징 검색 */
    @Transactional
    public QuesPageResDto searchQuestions(int page,int size ,String searchKeyword, String option,String checkStatus) throws NullPointerException, NoSuchElementException{
        Page<Question> questions=null;

        /** 채택과 채택전으로 나누기 */
        if(checkStatus.equals("채택")){
            if(option.equals("제목")){
                questions=questionRepository.findByQuestionCheckAndTitleContaining(CheckStatus.True,searchKeyword,PageRequest.of(page, size));
            }
            else if(option.equals("내용")){
                questions=questionRepository.findByQuestionCheckAndContentContaining(CheckStatus.True,searchKeyword,PageRequest.of(page,size));
            }
            else if(option.equals("제목 내용")){
                questions=questionRepository.findByQuestionCheckAndTitleContainingOrContentContaining(CheckStatus.True,searchKeyword,searchKeyword,PageRequest.of(page,size));
            }
            else{
                throw new NullPointerException("해당 검색 옵션은 존재하지 안습니다.");
            }


        }
        /** 채택 전 */
        else{
            if(option.equals("제목")){
                questions=questionRepository.findByQuestionCheckAndTitleContaining(CheckStatus.False,searchKeyword,PageRequest.of(page, size));
            }
            else if(option.equals("내용")){
                questions=questionRepository.findByQuestionCheckAndContentContaining(CheckStatus.False,searchKeyword,PageRequest.of(page,size));
            }
            else if(option.equals("제목 내용")){
                questions=questionRepository.findByQuestionCheckAndTitleContainingOrContentContaining(CheckStatus.False,searchKeyword,searchKeyword,PageRequest.of(page,size));
            }
            else{
                throw new NullPointerException("해당 검색 옵션은 존재하지 안습니다.");
            }

            if(questions==null){
                throw new NoSuchElementException("해당 검색어에 해당하는 질문이 존재하지 않습니다.");
            }
        }


        List<AllQuesResDto> results=questions.getContent().stream()
                .map(o -> new AllQuesResDto(o)).collect(Collectors.toList());

        PageInfo pageInfo= PageInfo.builder()
                .page(page)
                .pageSize(size)
                .totalPages(questions.getTotalPages())
                .totalNumber(questions.getTotalElements())
                .build();


        return QuesPageResDto.builder()
                .pageInfo(pageInfo)
                .questionList(results)
                .build();
    }


    /** 게시글 하나를 읽어갈때 댓글과 함께 읽어감 */
    @Transactional
    public QuesResDto getQuestion(Long id)throws Exception {

        Question question=questionRepository.findById(id).orElseThrow(() -> new Exception("질문이 존재하지 않습니다."));


        question.updateView();



        QuesResDto quesResDto = QuesResDto.builder()
                .question(question)
                .build();

        quesResDto.setAnswers(answerSerivce.getAnswers(id));

        return quesResDto;
    }

    @Transactional
    public QuesResDto updateQuestion(Long id,QuesUpdateDto quesUpdateDto)throws NullPointerException,AuthorMismatchException{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        Question question=questionRepository.findById(id)
                .orElseThrow(()->new NullPointerException("질문이 존재하지 않습니다."));

        String writer = question.getMember().getEmail();


        if(!writer.equals(name)){
            throw new AuthorMismatchException("동일한 작성자가 아닙니다.");
        }
        /** 버킷 삭제 구현 안함 */
        question.update(quesUpdateDto);

        return QuesResDto.builder().question(question).build();
    }

    @Transactional
    public void deleteQuestion(Long id)throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        Question question=questionRepository.findById(id)
                .orElseThrow(()->new Exception("질문이 존재하지 않습니다."));
        if(!name.equals(question.getMember().getEmail())){
            throw new Exception("질문 작성자가 아닙니다.");
        }

        if(question.getQuestionCheck().equals(CheckStatus.True)){
            throw new Exception("채택된 질문은 삭제할 수 없습니다.");
        }

        Member member=memberRepository.findByEmail(name)
                .orElseThrow(()->new Exception("해당 유저가 존재하지 않습니다."));

        member.plusPoint(question.getPoint());

        questionRepository.delete(question);
    }



}
