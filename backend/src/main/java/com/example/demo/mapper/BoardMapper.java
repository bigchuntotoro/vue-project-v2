package com.example.demo.mapper;

import com.example.demo.model.BoardDTO;
import com.example.demo.model.BoardFileDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface BoardMapper {

    // 1. 검색 조건과 페이징 조건이 반영된 목록 조회
    List<BoardDTO> selectBoardList(
            @Param("offset") int offset,
            @Param("size") int size,
            @Param("searchType") String searchType,
            @Param("searchKeyword") String searchKeyword
    );

    // 2. 검색 조건이 반영된 전체 데이터 개수 조회
    int selectBoardCount(
            @Param("searchType") String searchType,
            @Param("searchKeyword") String searchKeyword
    );

    // 3. 게시글 기본 CRUD & 조회수
    BoardDTO selectBoardById(int boardId);
    void updateViewCount(int boardId);
    int insertBoard(BoardDTO boardDTO);
    int updateBoard(BoardDTO boardDTO);
    int deleteBoard(int boardId);

    // 4. 첨부파일 등록 및 게시글별 파일 목록 조회
    int insertBoardFile(BoardFileDTO fileDTO);
    List<BoardFileDTO> selectFilesByBoardId(int boardId);

    // 💡 [추가] 파일 수정/삭제를 위한 단건 조회 및 단건 삭제
    BoardFileDTO selectFileById(int fileId);
    int deleteFileById(int fileId);
}