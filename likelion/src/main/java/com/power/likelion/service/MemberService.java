package com.power.likelion.service;

import com.power.likelion.common.response.SignStatus;
import com.power.likelion.domain.board.Board;
import com.power.likelion.domain.board.Comment;
import com.power.likelion.domain.member.Authority;
import com.power.likelion.domain.member.Member;
import com.power.likelion.domain.question.Answer;
import com.power.likelion.domain.question.Question;
import com.power.likelion.dto.login.GetInfoRes;
import com.power.likelion.dto.login.LoginDto;
import com.power.likelion.dto.login.LoginResDto;
import com.power.likelion.dto.member.MemberActivityResDto;
import com.power.likelion.dto.member.MemberComActResDto;
import com.power.likelion.dto.member.MemberPageDto;
import com.power.likelion.dto.member.MemberUpdateReq;
import com.power.likelion.dto.question.PageInfo;
import com.power.likelion.dto.sign_up.SignUpReqDto;
import com.power.likelion.repository.*;
import com.power.likelion.utils.jwts.JwtProvider;
import com.power.likelion.utils.s3.S3Uploader;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MemberService {
    private final MemberRepository memberRepository;
    private final QuestionRepository questionRepository;
    private final AnswerRepository answerRepository;
    private final BoardRepository boardRepository;
    private final CommentRepository commentRepository;
    private final PasswordEncoder encoder;
    private final JwtProvider jwtProvider;
    private final S3Uploader s3Uploader;

    @Transactional
    public void createMember(SignUpReqDto signUpReqDto) throws IllegalAccessException{


        Optional<Member> optionalUser = memberRepository.findByEmail(signUpReqDto.getEmail());
        if(optionalUser.isPresent()){
            throw new IllegalAccessException("이미 존재하는 이메일 입니다.");
        }


        Member member = Member.builder()
                .email(signUpReqDto.getEmail())
                .password(encoder.encode(signUpReqDto.getPassword()))
                .nickname(signUpReqDto.getNickname())
                .age(signUpReqDto.getAge())
                .point(100)
                .url(signUpReqDto.getUrl())
                .build();

        member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_USER").build()));

        try{
            memberRepository.save(member);
        }
        catch(Exception e){
            throw new IllegalAccessException("데이터 베이스 삽입 오류 입니다");
        }
    }


    /** 먼저 db에서 해당 이메일을 찾는다. */
    @Transactional
    public LoginResDto login(LoginDto loginDto) throws Exception {
        Member member=memberRepository.findByEmail(loginDto.getEmail())
                .orElseThrow(()->new Exception("이메일이 존재하지 않습니다."));

        if(!encoder.matches(loginDto.getPassword(),member.getPassword())){
            throw new Exception("비밀번호가 틀립니다.");
        };

        /** jwt 토큰과 함께 loginResDto 전달하기 */
        return LoginResDto.builder()
                .message("로그인 성공")
                .status(SignStatus.OK)
                .id(member.getId())
                .nickname(member.getNickname())
                .email(member.getEmail())
                .age(member.getAge())
                .jwtToken(jwtProvider.createToken(member.getEmail(), member.getRoles()))
                .point(member.getPoint())
                .url(member.getUrl())
                .build();
    }

    /** 내정보 조회 */
    @Transactional
    public GetInfoRes getMember(String email) throws Exception {

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new Exception("계정을 찾을 수 없습니다."));



        return GetInfoRes.builder()
                .message("요청된 정보입니다.")
                .status(SignStatus.OK)
                .nickname(member.getNickname())
                .email(member.getEmail())
                .age(member.getAge())
                .point(member.getPoint())
                .url(member.getUrl())
                .viewCnt(member.getViewCount())
                .build();
    }

    /**내 활동 내역 조회*/

    @Transactional
    public MemberActivityResDto getActivity(Long id, String type,int page)throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Member member=memberRepository.findById(id).orElseThrow(()->new Exception("해당 유저가 존재하지 않습니다."));

        /** 본인 활동 내역 조회시 조회수 증가 X*/
        if(!name.equals(member.getEmail())){
            member.updateView();
        }


        PageInfo pageInfo;
        MemberActivityResDto memberActivityResDto=MemberActivityResDto.builder()
                .nickname(member.getNickname())
                .image(member.getUrl())
                .viewCnt(member.getViewCount())
                .build();

        if(type.equals("question")){
            Page<Question> questions = questionRepository.findAllByMemberId(id,PageRequest.of(page, 1000));

            pageInfo= PageInfo.builder()
                    .page(page)
                    .pageSize(1000)
                    .totalPages(questions.getTotalPages())
                    .totalNumber(questions.getTotalElements())
                    .build();

            /** Page 객체에 get Content를 하면 List로 가져온다. 해당 List를 람다식을 이용하여 Dto로 변환하는 과정 */
            List<MemberPageDto> result=questions.getContent().stream()
                    .map(o->new MemberPageDto(o.getTitle(),o.getCreatedAt(),o.getId(),"Q&A"))
                    .collect(Collectors.toList());




            memberActivityResDto.setListAndPageInfo(result,pageInfo);




        }
        else if(type.equals("board")){
            Page<Board> boards = boardRepository.findAllByMemberId(id,PageRequest.of(page, 1000));
            pageInfo= PageInfo.builder()
                    .page(page)
                    .pageSize(1000)
                    .totalPages(boards.getTotalPages())
                    .totalNumber(boards.getTotalElements())
                    .build();

            /** Page 객체에 get Content를 하면 List로 가져온다. 해당 List를 람다식을 이용하여 Dto로 변환하는 과정 */
            List<MemberPageDto> result=boards.getContent().stream()
                    .map(o->new MemberPageDto(o.getTitle(),o.getCreatedAt(),o.getId(),o.getBoardType()))
                    .collect(Collectors.toList());

            memberActivityResDto.setListAndPageInfo(result,pageInfo);

        }
        else{
            throw new Exception("올바른 type 을 전달해 주세요");
        }

        return memberActivityResDto;
    }
    /** 활동 내역 댓글, 답변 조회 */
    @Transactional
    public MemberComActResDto getActivityComment(Long id, int page) throws Exception {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();
        Member member = memberRepository.findById(id).orElseThrow(() -> new Exception("해당 유저가 존재하지 않습니다."));

        /** 본인 활동 내역 조회시 조회수 증가 X*/
        if (!name.equals(member.getEmail())) {
            member.updateView();
        }

        MemberComActResDto memberComActResDto=MemberComActResDto.builder()
                .nickname(member.getNickname())
                .image(member.getUrl())
                .viewCnt(member.getViewCount())
                .build();


        PageInfo commentInfo;
        PageInfo answerInfo;
        Page<Comment> comments = commentRepository.findAllByMemberId(id, PageRequest.of(page, 1000));
        Page<Answer> answers = answerRepository.findAllByMemberId(id, PageRequest.of(page, 1000));
        commentInfo = PageInfo.builder()
                .page(page)
                .pageSize(1000)
                .totalPages(comments.getTotalPages())
                .totalNumber(comments.getTotalElements())
                .build();
        answerInfo = PageInfo.builder()
                .page(page)
                .pageSize(1000)
                .totalPages(answers.getTotalPages())
                .totalNumber(answers.getTotalElements())
                .build();

        /** Page 객체에 get Content를 하면 List로 가져온다. 해당 List를 람다식을 이용하여 Dto로 변환하는 과정 */
        List<MemberPageDto> comment1 = comments.getContent().stream()
                .map(o -> new MemberPageDto(o.getContent(), o.getCreatedAt(), o.getBoard().getId(), "댓글"))
                .collect(Collectors.toList());

        List<MemberPageDto> answer1 = answers.getContent().stream()
                .map(o -> new MemberPageDto(o.getContent(), o.getCreatedAt(), o.getQuestion().getId(), "답변"))
                .collect(Collectors.toList());

        memberComActResDto.setListAndPageInfo(comment1, commentInfo,answer1,answerInfo);

        return memberComActResDto;

    }



    @Transactional
    public GetInfoRes updateUser(MemberUpdateReq memberUpdateReq)throws Exception{

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName(); /** 이메일이 들어감 */

        Member member=memberRepository.findByEmail(name)
                .orElseThrow(()-> new Exception("유저가 존재하지 않습니다."));



        member.updateMember(memberUpdateReq);



        /** null일 경우 */
        if(memberUpdateReq.getUrl()==null){

            if(member.getUrl()!=null){

                s3Uploader.delete(member.getUrl(),"profile");
                member.setUrl(null);
            }
        }
        /** 이미지가 없는데 이미지를 추가할 경우 */
        else if(member.getUrl()==null){

            member.setUrl(memberUpdateReq.getUrl());

        }
        /** 이미지가 있는데 이미지를 수정할 경우 */
        else if(!member.getUrl().equals(memberUpdateReq.getUrl())){

            s3Uploader.delete(member.getUrl(),"profile");
            member.setUrl(memberUpdateReq.getUrl());
        }



        GetInfoRes getInfoRes= GetInfoRes.builder()
                .message("요청된 정보입니다.")
                .status(SignStatus.OK)
                .nickname(member.getNickname())
                .email(member.getEmail())
                .age(member.getAge())
                .point(member.getPoint())
                .url(member.getUrl())
                .build();


        log.info("getInfoRes: {}",getInfoRes.toString());


        return getInfoRes;

    }

    @Transactional
    public void createAdmin(SignUpReqDto signUpReqDto) throws IllegalAccessException{


        Optional<Member> optionalUser = memberRepository.findByEmail(signUpReqDto.getEmail());
        if(optionalUser.isPresent()){
            throw new IllegalAccessException("이미 존재하는 admin 입니다.");
        }



        Member member = Member.builder()
                .email(signUpReqDto.getEmail())
                .password(encoder.encode(signUpReqDto.getPassword()))
                .nickname(signUpReqDto.getNickname())
                .age(signUpReqDto.getAge())
                .point(1000000)
                .build();
        member.setRoles(Collections.singletonList(Authority.builder().name("ROLE_ADMIN").build()));

        try{
            memberRepository.save(member);
        }
        catch(Exception e){
            throw new IllegalAccessException("데이터 베이스 삽입 오류 입니다");
        }
    }
}
