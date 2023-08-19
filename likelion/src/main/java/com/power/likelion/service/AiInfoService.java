package com.power.likelion.service;

import com.power.likelion.domain.ai_info.AiInfo;
import com.power.likelion.domain.member.Member;

import com.power.likelion.dto.ai_info.*;
import com.power.likelion.dto.question.PageInfo;
import com.power.likelion.repository.AiInfoRepository;
import com.power.likelion.repository.MemberRepository;
import com.power.likelion.utils.s3.S3Uploader;
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

@RequiredArgsConstructor
@Service
public class AiInfoService {
    private final AiInfoRepository aiInfoRepository;
    private final MemberRepository memberRepository;
    private final S3Uploader s3Uploader;
    public AiInfoResDto createAiInfo(AiInfoReqDto aiInfoReqDto)throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        Member member = memberRepository.findByEmail(name)
                .orElseThrow(() -> new Exception("유저가 존재하지 않습니다."));

        AiInfo aiInfo = AiInfo.builder()
                .title(aiInfoReqDto.getTitle())
                .content(aiInfoReqDto.getContent())
                .member(member)
                .image(aiInfoReqDto.getUrl())
                .build();


        AiInfoResDto aiInfoResDto = AiInfoResDto.builder()
                .aiInfo(aiInfoRepository.save(aiInfo))
                .build();
        // 위에서 이미 게시글 정보는 다담았으므로 이미지 리스트만 설정해주고 Response를 return해주면 됨.


        return aiInfoResDto;
    }



    @Transactional
    public AiInfoPageResDto getAiInfo(int page, int size){
        Page<AiInfo> aiInfos = aiInfoRepository.findAll(PageRequest.of(page, size));

        PageInfo pageInfo= PageInfo.builder()
                .page(page)
                .pageSize(size)
                .totalPages(aiInfos.getTotalPages())
                .totalNumber(aiInfos.getTotalElements())
                .build();


        List<AiInfoAllResDto> results = aiInfos.getContent().stream().map(aiInfo -> new AiInfoAllResDto(aiInfo)
        ).collect(Collectors.toList());


        return AiInfoPageResDto.builder()
                .pageInfo(pageInfo)
                .aiInfoList(results)
                .build();

    }


    @Transactional
    public AiInfoPageResDto searchAiInfo(int page,int size ,String searchKeyword, String option) throws NullPointerException, NoSuchElementException {
        Page<AiInfo> aiInfos=null;


        if(option.equals("제목")){
            aiInfos=aiInfoRepository.findByTitleContaining(searchKeyword,PageRequest.of(page, size));
        }
        else if(option.equals("내용")){
            aiInfos=aiInfoRepository.findByContentContaining(searchKeyword,PageRequest.of(page,size));
        }
        else if(option.equals("제목 내용")){
            aiInfos=aiInfoRepository.findByTitleContainingOrContentContaining(searchKeyword,searchKeyword,PageRequest.of(page,size));
        }
        else{
            throw new NullPointerException("해당 검색 옵션은 존재하지 안습니다.");
        }

        if(aiInfos==null){
            throw new NoSuchElementException("해당 검색어에 해당하는 질문이 존재하지 않습니다.");
        }

        List<AiInfoAllResDto> results=aiInfos.getContent().stream()
                .map(o -> new AiInfoAllResDto(o)).collect(Collectors.toList());


        PageInfo pageInfo= PageInfo.builder()
                .page(page)
                .pageSize(size)
                .totalPages(aiInfos.getTotalPages())
                .totalNumber(aiInfos.getTotalElements())
                .build();


        return AiInfoPageResDto.builder()
                .pageInfo(pageInfo)
                .aiInfoList(results)
                .build();
    }

    @Transactional
    public void deleteAiInfo(Long id)throws Exception{
        AiInfo aiInfo=aiInfoRepository.findById(id)
                .orElseThrow(()->new Exception("해당 게시글이 존재하지 않습니다."));


        // 게시물에 이미지가 존재하면 S3 버킷에서 삭제를 먼저 진행
        if(aiInfo.getImage()!=null) {
            String url=aiInfo.getImage();
            s3Uploader.delete(url,"aiinfo");
        }


        aiInfoRepository.delete(aiInfo);

    }

    @Transactional
    public AiInfoResDto updateAiInfo(Long id, AiInfoReqDto aiInfoReqDto)throws Exception{

        AiInfo aiInfo=aiInfoRepository.findById(id)
                .orElseThrow(()->new Exception("해당 게시글이 존재하지 않습니다."));


        /** 게시글 업데이트 */
        aiInfo.updateAiInfo(aiInfoReqDto);


        AiInfoResDto aiInfoResDto= AiInfoResDto.builder()
                .aiInfo(aiInfo)
                .build();



        return aiInfoResDto;

    }

    @Transactional
    public AiInfoResDto getAiInfo(Long id)throws Exception{

        AiInfo aiInfo=aiInfoRepository.findById(id)
                .orElseThrow(()->new Exception("해당 게시글이 존재하지 않습니다."));

        aiInfo.updateView();

        AiInfoResDto aiInfoResDto=AiInfoResDto.builder()
                .aiInfo(aiInfo)
                .build();


        return aiInfoResDto;

    }

}
