package com.power.likelion.service;

import com.power.likelion.domain.ai_info.AiInfo;
import com.power.likelion.domain.image.Image;
import com.power.likelion.domain.member.Member;

import com.power.likelion.dto.ai_info.*;
import com.power.likelion.dto.question.PageInfo;
import com.power.likelion.repository.AiInfoRepository;
import com.power.likelion.repository.ImageRepository;
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
@Slf4j
public class AiInfoService {
    private final AiInfoRepository aiInfoRepository;
    private final MemberRepository memberRepository;
    private final ImageRepository imageRepository;
    private final S3Uploader s3Uploader;
    public AiInfoResDto createAiInfo(AiInfoReqDto aiInfoReqDto)throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        Member member=memberRepository.findByEmail(name)
                .orElseThrow(()->new Exception("유저가 존재하지 않습니다."));

        AiInfo aiInfo=AiInfo.builder()
                .title(aiInfoReqDto.getTitle())
                .content(aiInfoReqDto.getContent())
                .member(member)
                .build();

        // 이미지를 먼저 Image 엔티티 리스트로 만들고
        List<Image> imageList=aiInfoReqDto.getUrl().stream().map(o->new Image(o,aiInfo)).collect(Collectors.toList());


        // AiInfo게시물을 저장한다.
        AiInfoResDto aiInfoResDto= AiInfoResDto.builder()
                .aiInfo(aiInfoRepository.save(aiInfo))
                .build();

        // 이후 저장한 내용을 바탕으로 이미지도 해당 게시물 아이디로 저장한다.
        imageList.forEach(o->imageRepository.save(o));

        // 해당 이미지들을 다시 Response로 보내주기위해 ImageResDto도 따로 만든다.
        List<String> imageResDtos=imageList.stream().map(o->o.getUrl()).collect(Collectors.toList());

        //양방향 매핑을 위한 setImage
        aiInfo.setImage(imageList);


        // 위에서 이미 게시글 정보는 다담았으므로 이미지 리스트만 설정해주고 Response를 return해주면 됨.
        aiInfoResDto.setImageList(imageResDtos);

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

        List<ImageResDto> images=aiInfo.getImage()
                .stream().map(o->new ImageResDto(o.getUrl())).collect(Collectors.toList());

        /** object storage 이미지 삭제 */
        for(ImageResDto image:images){
            s3Uploader.delete(image.getImageUrl(),"aiinfo");
        }

        aiInfoRepository.delete(aiInfo);

    }

    @Transactional
    public AiInfoResDto updateAiInfo(Long id, AiInfoReqDto aiInfoReqDto)throws Exception{

        AiInfo aiInfo=aiInfoRepository.findById(id)
                .orElseThrow(()->new Exception("해당 게시글이 존재하지 않습니다."));


        aiInfo.updateAiInfo(aiInfoReqDto);

        List<Image> images=aiInfo.getImage();

        for(Image image:images){
            imageRepository.delete(image);
        }

        /** 받아온 이미지지를 Entity 직렬화 시켜서 저장 */


        if(aiInfoReqDto.getUrl()!=null) {

            List<Image> result = aiInfoReqDto.getUrl().stream().map(o -> new Image(o, aiInfo)).collect(Collectors.toList());

            result.forEach(o->imageRepository.save(o));
            aiInfo.setImage(result);
        }
        AiInfoResDto aiInfoResDto= AiInfoResDto.builder()
                .aiInfo(aiInfo)
                .build();

        List<String> imageList=aiInfoReqDto.getUrl();

        aiInfoResDto.setImageList(imageList);


        return aiInfoResDto;

    }

    @Transactional
    public AiInfoResDto getAiInfo(Long id)throws Exception{

        AiInfo aiInfo=aiInfoRepository.findById(id)
                .orElseThrow(()->new Exception("해당 게시글이 존재하지 않습니다."));

        aiInfo.updateView();

        AiInfoResDto aiInfoResDto=AiInfoResDto
                .builder()
                .aiInfo(aiInfo)
                .build();



        List<String> imageList=aiInfo.getImage().stream().map(o->o.getUrl()).collect(Collectors.toList());

        aiInfoResDto.setImageList(imageList);

        return aiInfoResDto;

    }

}
