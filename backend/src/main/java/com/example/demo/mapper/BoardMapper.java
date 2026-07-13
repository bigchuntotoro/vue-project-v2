package com.example.demo.mapper;

import com.example.demo.model.BoardDTO;
import org.apache.ibatis.annotations.Mapper;
import java.util.List;

@Mapper // Spring이 MyBatis 매퍼로 인식하도록 등록합니다.
public interface BoardMapper {
    List<BoardDTO> selectBoardList();          // 목록 조회
    BoardDTO selectBoardDetail(int boardId);   // 상세 조회
    int insertBoard(BoardDTO board);           // 글 등록
    int updateBoard(BoardDTO board);           // 글 수정
    int deleteBoard(int boardId);              // 글 삭제
    void updateViewCount(int boardId);         // 조회수 증가
}