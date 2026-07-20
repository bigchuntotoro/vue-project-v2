package com.example.demo.service;

import com.example.demo.model.BoardDTO;
import java.util.List;
import java.util.Map;

public interface BoardService {

    // 1. 목록 조회 (페이징 + 검색)
    Map<String, Object> getBoardList(int page, int size, String searchType, String searchKeyword);

    // 2. 게시글 상세 조회 (조회수 증가 포함)
    BoardDTO getBoardDetail(int boardId);

    // 3. 게시글 등록
    boolean registerBoard(BoardDTO board);

    // 4. 게시글 수정
    boolean modifyBoard(BoardDTO board);

    // 5. 게시글 삭제
    boolean removeBoard(int boardId);
}