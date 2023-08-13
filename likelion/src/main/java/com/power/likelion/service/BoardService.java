package com.power.likelion.service;


import com.power.likelion.common.exception.AuthorMismatchException;
import com.power.likelion.domain.board.Board;
import com.power.likelion.domain.member.Member;
import com.power.likelion.dto.board.BoardPageResDto;
import com.power.likelion.dto.board.BoardReqDto;
import com.power.likelion.dto.board.BoardResDto;
import com.power.likelion.dto.board.BoardUpdateDto;
import com.power.likelion.dto.question.*;
import com.power.likelion.repository.BoardRepository;
import com.power.likelion.repository.MemberRepository;

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
public class BoardService {

    private final MemberRepository memberRepository;
    private final BoardRepository boardRepository;
    private final CommentService commentService;

    @Transactional
    public BoardResDto createBoard(BoardReqDto boardReqDto,String boardType)throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();


        Member member=memberRepository.findByEmail(name)
                .orElseThrow(()->new Exception("유저가 존재하지 않습니다."));



        Board board=Board.builder()
                .title(boardReqDto.getTitle())
                .content(boardReqDto.getContent())
                .boardType(boardType)
                .member(member)
                .build();


        Board result=boardRepository.save(board);

        /** 여기까지 문제 없음 */

        BoardResDto boardResDto=BoardResDto.builder()
                .board(result)
                .build();

        log.info("boardResDto{}",boardResDto.getBoardId());

        return boardResDto;

    }

    /** 페이징 처리로 10개의 게시글이 간다. 검색 조건 X */
    @Transactional
    public BoardPageResDto getBoards(int page, int size,String boardType){
        Page<Board> boards;
        if (boardType == null) {
            boards = boardRepository.findAll(PageRequest.of(page, size));
        }
        else{
            boards = boardRepository.findByBoardTypeContaining(boardType,PageRequest.of(page,size));
        }

        PageInfo pageInfo= PageInfo.builder()
                .page(page)
                .pageSize(size)
                .totalPages(boards.getTotalPages())
                .totalNumber(boards.getTotalElements())
                .build();

        List<BoardResDto> results = boards.getContent().stream().map(o -> new BoardResDto(o)
        ).collect(Collectors.toList());


        return BoardPageResDto.builder()
                .pageInfo(pageInfo)
                .boardList(results)
                .build();

    }



    @Transactional
    public BoardPageResDto searchBoards(int page,int size ,String searchKeyword, String option,String boardType) throws NullPointerException, NoSuchElementException {
        Page<Board> boards=null;


        if(option.equals("제목")){
            log.info("boardType:{}",boardType);
            if(boardType==null) {
                boards = boardRepository.findByTitleContaining(searchKeyword, PageRequest.of(page, size));
            }
            else{
                boards=boardRepository.findByTitleContainingAndBoardTypeContaining(searchKeyword,boardType,PageRequest.of(page,size));
            }
        }
        else if(option.equals("내용")){
            if(boardType==null){
                boards=boardRepository.findByContentContaining(searchKeyword,PageRequest.of(page,size));
            }
            else{
                boards=boardRepository.findByContentContainingAndBoardTypeContaining(searchKeyword,boardType,PageRequest.of(page,size));
            }
        }
        else if(option.equals("제목 내용")){
            if(boardType==null){
                boards=boardRepository.findByTitleContainingOrContentContaining(searchKeyword,searchKeyword,PageRequest.of(page,size));
            }
            else{
                boards=boardRepository.findByTitleContainingOrContentContainingAndBoardTypeContaining(searchKeyword,searchKeyword,boardType,PageRequest.of(page,size));
            }
        }
        else{
            throw new NullPointerException("해당 검색 옵션은 존재하지 안습니다.");
        }

        if(boards==null){
            throw new NoSuchElementException("해당 검색어에 해당하는 게시글이 존재하지 않습니다.");
        }

        List<BoardResDto> results=boards.getContent().stream()
                .map(o -> new BoardResDto(o)).collect(Collectors.toList());

        PageInfo pageInfo= PageInfo.builder()
                .page(page)
                .pageSize(size)
                .totalPages(boards.getTotalPages())
                .totalNumber(boards.getTotalElements())
                .build();


        return BoardPageResDto.builder()
                .pageInfo(pageInfo)
                .boardList(results)
                .build();
    }


    /** 게시글 하나를 읽어갈때 댓글과 함께 읽어감 */
    @Transactional
    public BoardResDto getBoard(Long id)throws Exception {


        Board board=boardRepository.findById(id)
                .orElseThrow(() -> new Exception("게시글이 존재하지 않습니다."));

        board.updateView();

        BoardResDto boardResDto = BoardResDto.builder()
                .board(board)
                .build();
        
        
        boardResDto.setComments(commentService.getComments(id));



        return boardResDto;
    }

    @Transactional
    public BoardResDto updateBoard(Long id, BoardUpdateDto boardUpdateDto,String boardType)throws NullPointerException, AuthorMismatchException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();

        Board board=boardRepository.findById(id)
                .orElseThrow(()->new NullPointerException("게시글이 존재하지 않습니다."));

        String writer = board.getMember().getEmail();


        if(!writer.equals(name)){
            throw new AuthorMismatchException("게시글 작성자가 아닙니다.");
        }

        board.update(boardUpdateDto,boardType);

        return BoardResDto.builder().board(board).build();
    }

    @Transactional
    public void deleteBoard(Long id)throws Exception{
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String name = authentication.getName();


        Board board=boardRepository.findById(id)
                .orElseThrow(()->new Exception("해당 게시글이 존재하지 않습니다."));

        if(!name.equals(board.getMember().getEmail())){
            throw new Exception("게시글 작성자가 아닙니다.");
        }

        boardRepository.delete(board);
    }
}
