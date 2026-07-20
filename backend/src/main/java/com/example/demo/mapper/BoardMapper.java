package com.example.demo.mapper;

import com.example.demo.model.BoardDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface BoardMapper {
    // [수정] 페이징 처리를 위해 size와 offset을 파라미터로 받습니다.
    List<BoardDTO> selectBoardList(@Param("size") int size, @Param("offset") int offset);

    // [추가] 페이징 버튼 계산을 위한 전체 게시글 개수 조회
    int countBoard();

    BoardDTO selectBoardDetail(int boardId);   // 상세 조회
    int insertBoard(BoardDTO board);           // 글 등록
    int updateBoard(BoardDTO board);           // 글 수정
    int deleteBoard(int boardId);              // 글 삭제
    void updateViewCount(int boardId);         // 조회수 증가
}