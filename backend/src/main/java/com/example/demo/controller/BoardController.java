package com.example.demo.controller;

import com.example.demo.model.BoardDTO;
import com.example.demo.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController // 결과값을 JSON 문자열로 바로 응답합니다.
@RequestMapping("/api/board") // 공통 URL 프리픽스
public class BoardController {

    @Autowired
    private BoardService boardService;

    // 1. 목록 조회 (페이징 + 검색)
    @GetMapping("/list")
    public ResponseEntity<Map<String, Object>> getBoardList(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "searchType", defaultValue = "all") String searchType,
            @RequestParam(value = "searchKeyword", required = false) String searchKeyword) {

        // 검색 조건(searchType, searchKeyword)을 포함해서 서비스에 전달합니다.
        Map<String, Object> response = boardService.getBoardList(page, size, searchType, searchKeyword);

        return ResponseEntity.ok(response);
    }

    // 2. 상세 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDTO> getBoardDetail(@PathVariable int boardId) {
        BoardDTO board = boardService.getBoardDetail(boardId);
        return ResponseEntity.ok(board);
    }

    // 3. 게시글 작성
// [수정] 게시글 작성 (파일 업로드 지원 - multipart/form-data)
    @PostMapping(consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> registerBoard(
            @RequestPart("board") BoardDTO boardDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        if (files != null && files.size() > 5) {
            return ResponseEntity.badRequest().body("파일은 최대 5개까지만 업로드 가능합니다.");
        }

        boolean isSuccess = boardService.registerBoardWithFiles(boardDTO, files);
        return isSuccess ? ResponseEntity.ok("Success") : ResponseEntity.internalServerError().body("Fail");
    }

    // 💡 4. 게시글 수정 (Multipart/Form-Data 수신 지원 수정)
    @PutMapping(value = "/{boardId}", consumes = {MediaType.APPLICATION_JSON_VALUE, MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> modifyBoard(
            @PathVariable("boardId") int boardId,
            @RequestPart("board") BoardDTO boardDTO,
            @RequestPart(value = "files", required = false) List<MultipartFile> files) {

        boardDTO.setBoardId(boardId); // URL의 boardId를 객체에 주입
        boolean isSuccess = boardService.modifyBoardWithFiles(boardDTO, files);
        return isSuccess ? ResponseEntity.ok("Success") : ResponseEntity.internalServerError().body("Fail");
    }

    // 5. 게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> removeBoard(@PathVariable int boardId) {
        boolean isSuccess = boardService.removeBoard(boardId);
        return isSuccess ? ResponseEntity.ok("Success") : ResponseEntity.internalServerError().body("Fail");
    }
}