package com.example.demo.service;

import com.example.demo.mapper.BoardMapper;
import com.example.demo.model.BoardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public Map<String, Object> getBoardList(int page, int size, String searchType, String searchKeyword) {
        // 페이징 offset 계산 (예: 1페이지 = 0부터, 2페이지 = 10부터)
        int offset = (page - 1) * size;

        // Mapper를 통해 검색 조건이 반영된 목록 및 전체 개수 조회
        List<BoardDTO> list = boardMapper.selectBoardList(offset, size, searchType, searchKeyword);
        int total = boardMapper.selectBoardCount(searchType, searchKeyword);

        // Vue로 전달할 Map 구조 생성
        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);

        return response;
    }

    @Override
    public BoardDTO getBoardDetail(int boardId) {
        // 상세 조회 시 조회수 1 증가
        boardMapper.updateViewCount(boardId);
        return boardMapper.selectBoardById(boardId);
    }

    @Override
    public boolean registerBoard(BoardDTO boardDTO) {
        return boardMapper.insertBoard(boardDTO) > 0;
    }

    @Override
    public boolean modifyBoard(BoardDTO boardDTO) {
        return boardMapper.updateBoard(boardDTO) > 0;
    }

    @Override
    public boolean removeBoard(int boardId) {
        return boardMapper.deleteBoard(boardId) > 0;
    }
}