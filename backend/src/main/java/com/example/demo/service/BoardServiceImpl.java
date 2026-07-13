package com.example.demo.service;

import com.example.demo.mapper.BoardMapper;
import com.example.demo.model.BoardDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service // Spring Bean으로 등록합니다.
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    @Override
    public List<BoardDTO> getBoardList() {
        return boardMapper.selectBoardList();
    }

    @Override
    @Transactional // 조회수 증가와 상세 조회가 하나의 트랜잭션으로 묶이도록 처리합니다.
    public BoardDTO getBoardDetail(int boardId) {
        boardMapper.updateViewCount(boardId); // 조회수 1 증가
        return boardMapper.selectBoardDetail(boardId);
    }

    @Override
    public boolean registerBoard(BoardDTO board) {
        // 성공 시 영향받은 행의 수(1)가 반환되므로 true를 반환하게 설정합니다.
        return boardMapper.insertBoard(board) == 1;
    }

    @Override
    public boolean modifyBoard(BoardDTO board) {
        return boardMapper.updateBoard(board) == 1;
    }

    @Override
    public boolean removeBoard(int boardId) {
        return boardMapper.deleteBoard(boardId) == 1;
    }
}
