package com.example.demo.service;

import com.example.demo.model.BoardDTO;
import java.util.List;

public interface BoardService {
    List<BoardDTO> getBoardList();
    BoardDTO getBoardDetail(int boardId);
    boolean registerBoard(BoardDTO board);
    boolean modifyBoard(BoardDTO board);
    boolean removeBoard(int boardId);
}
