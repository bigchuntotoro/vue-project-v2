package com.example.demo.mapper;

import com.example.demo.model.BoardDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 검색 조건과 페이징 조건이 반영된 목록 조회
    List<BoardDTO> selectBoardList(
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("searchType") String searchType,
            @Param("searchKeyword") String searchKeyword
    );

    // 검색 조건이 반영된 전체 데이터 개수 조회 (페이징 버튼 계산용)
    int selectBoardCount(
            @Param("searchType") String searchType,
            @Param("searchKeyword") String searchKeyword
    );

    BoardDTO selectBoardById(int boardId);

    void updateViewCount(int boardId);

    int insertBoard(BoardDTO boardDTO);

    int updateBoard(BoardDTO boardDTO);

    int deleteBoard(int boardId);
}