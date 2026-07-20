package com.example.demo.service;

import com.example.demo.model.BoardDTO;
import java.util.List;
import java.util.Map;

public interface BoardService {

    // 1. 페이징 처리된 목록과 전체 개수를 함께 조회 (Vue 반환용)
    Map<String, Object> getBoardList(int page, int size);

    // 2. 게시글 상세 조회 (조회수 증가 포함)
    BoardDTO getBoardDetail(int boardId);

    // 3. 게시글 등록
    boolean registerBoard(BoardDTO board);

    // 4. 게시글 수정
    boolean modifyBoard(BoardDTO board);

    // 5. 게시글 삭제
    boolean removeBoard(int boardId);
}