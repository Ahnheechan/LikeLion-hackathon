package com.power.likelion.controller;

import com.power.likelion.common.exception.AuthorMismatchException;
import com.power.likelion.common.response.BaseResponse;
import com.power.likelion.dto.question.QuesReqDto;
import com.power.likelion.dto.question.QuesResDto;
import com.power.likelion.dto.question.QuesUpdateDto;
import com.power.likelion.service.QuestionService;
import com.power.likelion.utils.swagger.question.*;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import javax.validation.constraints.Min;
import java.util.NoSuchElementException;


@Tag(name = "Q&A 생성, 조회, 수정, 삭제", description = "질문을 올리고 수정하고 삭제하고 조회할 수 있다.")
@RestController
@RequiredArgsConstructor
@RequestMapping("/questions")
public class QuestionController {

    private final QuestionService questionService;

    /** 질문 생성 */
    @QuesApiRequest
    @PostMapping("/create")
    public ResponseEntity<?> createQuestion(@RequestBody QuesReqDto quesReqDto) {
        try{
            QuesResDto quesResDto=questionService.createQuestion(quesReqDto);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(quesResDto)
                            .build());
        }
        catch(Exception e){ // 유저가 존재하지 않을 경우
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }

    }




    //TODO:  수정해야됨


    /** 난 프론트에서 현재 페이지 번호와 한페이지당 size만 받아오면 현재 페이지의 size만큼의 게시글을 보여주고 총 데이터, 페이지 개수를 프론트에게 넘
     * 길 예정 */
    @GetAllQuesRequest
    @GetMapping()
    public ResponseEntity<?> searchQuestion(@RequestParam(name = "page") @Min(0) Integer page,
                                            @RequestParam(name= "size") @Min(0) Integer size,
                                            @RequestParam(required = false,name = "option") String option,
                                            @RequestParam(required = false,name = "searchKeyword") String searchKeyword)
    {
        if(searchKeyword==null){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(questionService.getQuestions(page,size))
                            .build()
                    );
        }

        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(questionService.searchQuestions(page,size,searchKeyword,option))
                            .build());
        }

        catch(NullPointerException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
        catch(NoSuchElementException e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(HttpStatus.NOT_FOUND.value(), e.getMessage()));
        }
    }

    /** 글 하나 상세조회에서 조회수 증가 로직을 구현해야함  */
    @GetQuesApiRequest
    @GetMapping("/{id}")
    public ResponseEntity<?> getQuestion(@PathVariable("id") Long id
                                         ) throws Exception{
        try{
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(questionService.getQuestion(id))
                            .build());
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
    }

    @QuesUpdateApiRequest
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateQues(@PathVariable("id") Long id, @RequestBody QuesUpdateDto quesUpdateDto){
        try{
            QuesResDto quesResDto=questionService.updateQuestion(id,quesUpdateDto);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(quesResDto)
                            .build());
        }
        catch (NullPointerException e){
            return ResponseEntity
                    .status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
        catch (AuthorMismatchException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(),e.getMessage()));
        }
    }

    @QuesDeleteApiRequest
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteQues(@PathVariable("id")Long id){
        try{
            questionService.deleteQuestion(id);
            return ResponseEntity
                    .ok()
                    .body(new BaseResponse<>(HttpStatus.OK.value(),"질문이 삭제되었습니다."));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(new BaseResponse<>(HttpStatus.NOT_FOUND.value(),e.getMessage()));
        }
    }



}