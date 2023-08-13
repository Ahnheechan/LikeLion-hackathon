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
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
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
                .build();


        Question resQuestion =questionRepository.save(question);


        return QuesResDto.builder()
                .question(resQuestion)
                .build();

    }

    /** 페이징 처리로 size개의 게시글이 간다. */
    @Transactional
    public QuesPageResDto getQuestions(int page,int size){
        Page<Question> questions = questionRepository.findAll(PageRequest.of(page, size));
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



    @Transactional
    public QuesPageResDto searchQuestions(int page,int size ,String searchKeyword, String option) throws NullPointerException, NoSuchElementException{
        Page<Question> questions=null;


        if(option.equals("제목")){
            questions=questionRepository.findByTitleContaining(searchKeyword,PageRequest.of(page, size));
        }
        else if(option.equals("내용")){
            questions=questionRepository.findByContentContaining(searchKeyword,PageRequest.of(page,size));
        }
        else if(option.equals("제목 내용")){
            questions=questionRepository.findByTitleContainingOrContentContaining(searchKeyword,searchKeyword,PageRequest.of(page,size));
        }
        else{
            throw new NullPointerException("해당 검색 옵션은 존재하지 안습니다.");
        }

        if(questions==null){
            throw new NoSuchElementException("해당 검색어에 해당하는 질문이 존재하지 않습니다.");
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

        question.update(quesUpdateDto);

        return QuesResDto.builder().question(question).build();
    }

    @Transactional
    public void deleteQuestion(Long id)throws Exception{
        Question question=questionRepository.findById(id)
                .orElseThrow(()->new Exception("질문이 존재하지 않습니다."));
        questionRepository.delete(question);
    }



}
