package com.example.demo.service;

import com.example.demo.mapper.BoardMapper;
import com.example.demo.model.BoardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    // 1. 목록 조회 및 페이징 데이터 가공
    @Override
    public Map<String, Object> getBoardList(int page, int size) {
        // Vue에서 받은 page(1, 2, 3...)를 DB의 offset(0, 10, 20...)으로 연산
        int offset = (page - 1) * size;

        // MyBatis Mapper 호출
        List<BoardDTO> list = boardMapper.selectBoardList(size, offset);
        int total = boardMapper.countBoard();

        // Vue의 response.data.list 및 response.data.total 구조와 매핑할 Map 생성
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        result.put("total", total);

        return result;
    }

    // 2. 상세 조회 (조회수 증가 로직 포함)
    @Override
    @Transactional // 조회수 증가와 상세조회가 하나의 트랜잭션으로 묶임
    public BoardDTO getBoardDetail(int boardId) {
        // 상세 조회를 할 때 조회수 1 증가
        boardMapper.updateViewCount(boardId);
        return boardMapper.selectBoardDetail(boardId);
    }

    // 3. 게시글 등록
    @Override
    public boolean registerBoard(BoardDTO board) {
        return boardMapper.insertBoard(board) > 0;
    }

    // 4. 게시글 수정
    @Override
    public boolean modifyBoard(BoardDTO board) {
        return boardMapper.updateBoard(board) > 0;
    }

    // 5. 게시글 삭제
    @Override
    public boolean removeBoard(int boardId) {
        return boardMapper.deleteBoard(boardId) > 0;
    }
}