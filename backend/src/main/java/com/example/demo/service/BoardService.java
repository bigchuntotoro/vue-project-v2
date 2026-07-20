package com.example.demo.service;

import com.example.demo.model.BoardDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

public interface BoardService {

    // 1. 게시글 목록 조회 (페이징 + 검색)
    Map<String, Object> getBoardList(int page, int size, String searchType, String searchKeyword);

    // 2. 게시글 상세 조회 (조회수 증가 + 첨부파일 목록 포함)
    BoardDTO getBoardDetail(int boardId);

    // 3. 게시글 및 첨부파일 등록 (최대 5개 처리)
    boolean registerBoardWithFiles(BoardDTO board, List<MultipartFile> files);

    // 4. 게시글 수정 (본문 수정 + 삭제할 파일 제거 + 신규 파일 저장)
    boolean modifyBoardWithFiles(BoardDTO board, List<MultipartFile> files);

    // 5. 게시글 삭제
    boolean removeBoard(int boardId);
}