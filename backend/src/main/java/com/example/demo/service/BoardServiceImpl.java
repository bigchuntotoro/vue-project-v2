package com.example.demo.service;

import com.example.demo.mapper.BoardMapper;
import com.example.demo.model.BoardDTO;
import com.example.demo.model.BoardFileDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
public class BoardServiceImpl implements BoardService {

    @Autowired
    private BoardMapper boardMapper;

    // 파일이 저장될 물리적 경로
    private final String uploadPath = "C:/upload/";

    /**
     * 1. 게시글 목록 조회
     */
    @Override
    public Map<String, Object> getBoardList(int page, int size, String searchType, String searchKeyword) {
        int offset = (page - 1) * size;

        List<BoardDTO> list = boardMapper.selectBoardList(offset, size, searchType, searchKeyword);
        int total = boardMapper.selectBoardCount(searchType, searchKeyword);

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);

        return response;
    }

    /**
     * 2. 게시글 상세 조회
     */
    @Override
    @Transactional
    public BoardDTO getBoardDetail(int boardId) {
        boardMapper.updateViewCount(boardId);

        BoardDTO board = boardMapper.selectBoardById(boardId);
        if (board != null) {
            List<BoardFileDTO> files = boardMapper.selectFilesByBoardId(boardId);
            board.setFileList(files);
        }
        return board;
    }

    /**
     * 3. 게시글 및 첨부파일 등록
     */
    @Override
    @Transactional
    public boolean registerBoardWithFiles(BoardDTO board, List<MultipartFile> files) {
        if (files != null && files.size() > 5) {
            throw new IllegalArgumentException("첨부파일은 최대 5개까지만 등록 가능합니다.");
        }

        int result = boardMapper.insertBoard(board);
        if (result <= 0) {
            return false;
        }

        saveFilesToDiskAndDB(board.getBoardId(), files);
        return true;
    }

    /**
     * 4. 게시글 수정 (본문 수정 + 삭제할 파일 제거 + 신규 파일 저장)
     */
    @Override
    @Transactional
    public boolean modifyBoardWithFiles(BoardDTO board, List<MultipartFile> files) {
        // 1) 본문 내용 수정 (제목, 내용 등)
        int updatedRows = boardMapper.updateBoard(board);
        if (updatedRows <= 0) {
            return false;
        }

        // 2) 삭제 요청된 기존 파일 처리
        if (board.getDeleteFileIds() != null && !board.getDeleteFileIds().isEmpty()) {
            for (Integer fileId : board.getDeleteFileIds()) {
                BoardFileDTO fileDTO = boardMapper.selectFileById(fileId);
                if (fileDTO != null) {
                    File file = new File(fileDTO.getFilePath());
                    if (file.exists()) {
                        file.delete(); // 디스크 실제 파일 삭제
                    }
                    boardMapper.deleteFileById(fileId); // DB 파일 정보 삭제
                }
            }
        }

        // 3) 새로 첨부된 파일 추가 저장
        saveFilesToDiskAndDB(board.getBoardId(), files);

        return true;
    }

    /**
     * 5. 게시글 삭제
     */
    @Override
    public boolean removeBoard(int boardId) {
        return boardMapper.deleteBoard(boardId) > 0;
    }

    /**
     * 💡 공통 파일 저장 메서드
     */
    private void saveFilesToDiskAndDB(int boardId, List<MultipartFile> files) {
        if (files != null && !files.isEmpty()) {
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }

            for (MultipartFile file : files) {
                if (file == null || file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
                    continue;
                }

                String originalName = file.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                String saveName = uuid + "_" + originalName;
                String filePath = uploadPath + saveName;

                try {
                    file.transferTo(new File(filePath));

                    BoardFileDTO fileDTO = new BoardFileDTO();
                    fileDTO.setBoardId(boardId);
                    fileDTO.setOriginalName(originalName);
                    fileDTO.setSaveName(saveName);
                    fileDTO.setFilePath(filePath);
                    fileDTO.setFileSize(file.getSize());

                    boardMapper.insertBoardFile(fileDTO);
                } catch (IOException e) {
                    throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
                }
            }
        }
    }
}