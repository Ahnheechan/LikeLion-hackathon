package com.power.likelion.controller;

import com.power.likelion.common.response.BaseResponse;
import com.power.likelion.dto.ai_info.AiInfoReqDto;
import com.power.likelion.service.AiInfoService;
import com.power.likelion.utils.swagger.AiInfo.CreateAiInfoApiReq;
import com.power.likelion.utils.swagger.AiInfo.DeleteAiApiReq;
import com.power.likelion.utils.swagger.AiInfo.GetInfosApiReq;
import com.power.likelion.utils.swagger.AiInfo.UpdateAiApiReq;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.Min;
import java.util.NoSuchElementException;

@RestController
@RequiredArgsConstructor
@Tag(name = "오픈 AI 소개 페이지", description = "AI를 소개하는 글을 admin 계정을 가진 회원만 작성할 수있다.")
public class AiInfoController {

    private final AiInfoService aiInfoService;
    /** 게시글 작성란 에서 작성을 누르면 오는 것 이미지는 게시글 작성란에서 업로드 버튼으로 미리 업로드 구현 */
    @CreateAiInfoApiReq
    @PostMapping("/admin/ai-info/create")
    public ResponseEntity<?> createInfo(@RequestBody AiInfoReqDto aiInfoReqDto){
        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(aiInfoService.createAiInfo(aiInfoReqDto))
                            .build());

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(new BaseResponse<>(HttpStatus.BAD_REQUEST.value(),e.getMessage()));
        }
    }

    /** 전체 조회 및 검색 */
    @GetInfosApiReq
    @GetMapping("/ai-info")
    public ResponseEntity<?> searchQuestion(@RequestParam(name = "page") @Min(0) Integer page,
                                            @RequestParam(name= "size") @Min(0) Integer size,
                                            @RequestParam(required = false,name = "option") String option,
                                            @RequestParam(required = false,name = "searchKeyword") String searchKeyword)
    {
        if(searchKeyword==null){
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(aiInfoService.getAiInfo(page,size))
                            .build()
                    );
        }

        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(aiInfoService.searchAiInfo(page,size,searchKeyword,option))
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

    /** AI 단일 게시글 조회 */
    @GetMapping("/ai-info/{id}")
    public ResponseEntity<?> getAiInfo(@PathVariable("id") Long id){
        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(aiInfoService.getAiInfo(id))
                            .build());

        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));

        }

    }

    /** AI 게시글 삭제 */
    @DeleteAiApiReq
    @DeleteMapping("/admin/ai-info/delete/{id}")
    public ResponseEntity<?> deleteAiInfo(@PathVariable("id") Long id){
        try{
            aiInfoService.deleteAiInfo(id);
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(null)
                            .build());
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(),e.getMessage()));
        }
    }

    @UpdateAiApiReq
    @PatchMapping("/admin/ai-info/update/{id}")
    public ResponseEntity<?> updateAiInfo(@PathVariable("id") Long id,@RequestBody AiInfoReqDto aiInfoReqDto){
        try{
            return ResponseEntity.status(HttpStatus.OK)
                    .body(BaseResponse.builder()
                            .result(aiInfoService.updateAiInfo(id,aiInfoReqDto))
                            .build());
        }
        catch(Exception e){
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(new BaseResponse<>(HttpStatus.INTERNAL_SERVER_ERROR.value(), e.getMessage()));
        }
    }



}
