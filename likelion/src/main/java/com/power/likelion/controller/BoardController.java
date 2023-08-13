package com.power.likelion.controller;


import com.power.likelion.common.exception.AuthorMismatchException;
import com.power.likelion.common.response.BaseResponse;
import com.power.likelion.dto.board.BoardReqDto;
import com.power.likelion.dto.board.BoardResDto;
import com.power.likelion.dto.board.BoardUpdateDto;
import com.power.likelion.service.BoardService;
import com.power.likelion.utils.swagger.board.*;
import io.swagger.v3.oas.annotations.tags.Tag;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import javax.validation.constraints.Min;
import java.util.NoSuchElementException;


@RestController
@RequiredArgsConstructor
@RequestMapping("/boards")
@Tag(name = "게시글 생성, 조회, 삭제, 수정", description = "게시글 관련 요청 모음입니다.")
public class BoardController {

    private final BoardService boardService;

    /** 게시글 생성 */
    @BoardCreateApiReq
    @BoardCreateApiRes
    @PostMapping("/create")
    public ResponseEntity<?> createBoard(@RequestBody BoardReqDto boardReqDto, @RequestParam(name = "boardType") String boardType) {
        try{
            BoardResDto boardResDto =boardService.createBoard(boardReqDto,boardType);

            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(boardResDto)
                            .build());
        }

        catch(Exception e){ // 유저가 존재하지 않을 경우
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }

    }

    /** 전체 게시글을 페이징으로 조회하거나 검색내용을 페이징으로 조회*/
    @BoardPageApiRes
    @BoardPageApiReq
    @GetMapping()
    public ResponseEntity<?> searchQuestion(@RequestParam(name = "page") @Min(0) Integer page,
                                            @RequestParam(name= "size") @Min(0) Integer size,
                                            @RequestParam(required = false,name="boardType") String boardType,
                                            @RequestParam(required = false,name = "option") String option,
                                            @RequestParam(required = false,name = "searchKeyword") String searchKeyword)
    {
        if(searchKeyword==null){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(boardService.getBoards(page,size,boardType))
                            .build()
                    );
        }

        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(boardService.searchBoards(page,size,searchKeyword,option,boardType))
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


    @BoardGetApiReq
    @BoardGetApiRes
    @GetMapping("/{id}")
    public ResponseEntity<?> getBoard(@PathVariable("id") Long id) throws Exception{
        try{
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(boardService.getBoard(id))
                            .build());
        }
        catch(Exception e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }


    @BoardUpdateApiReq
    @BoardUpdateApiRes
    @PatchMapping("/{id}")
    public ResponseEntity<?> updateBoard(@PathVariable("id") Long id,@RequestParam(name = "boardType") String boardType ,@RequestBody BoardUpdateDto boardUpdateDto){
        try{
            BoardResDto boardResDto=boardService.updateBoard(id,boardUpdateDto,boardType);
            return ResponseEntity
                    .status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(boardResDto)
                            .build());
        }
        catch (NullPointerException e){
            return ResponseEntity
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
        catch (AuthorMismatchException e) {
            return ResponseEntity
                    .status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(),e.getMessage()));
        }
    }


    @BoardDeleteApiReq
    @BoardDeleteApiRes
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBoard(@PathVariable("id")Long id){
        try{
            boardService.deleteBoard(id);
            return ResponseEntity
                    .ok()
                    .body(new BaseResponse<>(HttpStatus.OK.value(),"질문이 삭제되었습니다."));
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }



}
