package com.example.demo.controller;

import com.example.demo.model.BoardDTO;
import com.example.demo.service.BoardService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController // 결과값을 JSON 문자열로 바로 응답합니다.
@RequestMapping("/api/board") // 공통 URL 프리픽스
public class BoardController {

    @Autowired
    private BoardService boardService;

    // 1. 목록 조회
    @GetMapping("/list")
    public ResponseEntity<List<BoardDTO>> getBoardList() {
        List<BoardDTO> list = boardService.getBoardList();
        return ResponseEntity.ok(list);
    }

    // 2. 상세 조회
    @GetMapping("/{boardId}")
    public ResponseEntity<BoardDTO> getBoardDetail(@PathVariable int boardId) {
        BoardDTO board = boardService.getBoardDetail(boardId);
        return ResponseEntity.ok(board);
    }

    // 3. 게시글 작성
    @PostMapping
    public ResponseEntity<String> registerBoard(@RequestBody BoardDTO boardDTO) {
        boolean isSuccess = boardService.registerBoard(boardDTO);
        return isSuccess ? ResponseEntity.ok("Success") : ResponseEntity.internalServerError().body("Fail");
    }

    // 4. 게시글 수정
    @PutMapping("/{boardId}")
    public ResponseEntity<String> modifyBoard(@PathVariable int boardId, @RequestBody BoardDTO boardDTO) {
        boardDTO.setBoardId(boardId); // URL의 boardId를 객체에 주입
        boolean isSuccess = boardService.modifyBoard(boardDTO);
        return isSuccess ? ResponseEntity.ok("Success") : ResponseEntity.internalServerError().body("Fail");
    }

    // 5. 게시글 삭제
    @DeleteMapping("/{boardId}")
    public ResponseEntity<String> removeBoard(@PathVariable int boardId) {
        boolean isSuccess = boardService.removeBoard(boardId);
        return isSuccess ? ResponseEntity.ok("Success") : ResponseEntity.internalServerError().body("Fail");
    }
}
