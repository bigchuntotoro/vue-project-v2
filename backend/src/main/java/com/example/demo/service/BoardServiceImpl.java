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
     * 1. 게시글 목록 조회 (페이징 + 검색 + 파일 개수 매핑)
     */
    @Override
    public Map<String, Object> getBoardList(int page, int size, String searchType, String searchKeyword) {
        int offset = (page - 1) * size;

        // DB에서 목록(fileCount 포함) 및 전체 개수 조회
        List<BoardDTO> list = boardMapper.selectBoardList(offset, size, searchType, searchKeyword);
        int total = boardMapper.selectBoardCount(searchType, searchKeyword);

        Map<String, Object> response = new HashMap<>();
        response.put("list", list);
        response.put("total", total);

        return response;
    }

    /**
     * 2. 게시글 상세 조회 (조회수 증가 + 첨부파일 목록 주입)
     */
    @Override
    @Transactional
    public BoardDTO getBoardDetail(int boardId) {
        // 조회수 증가
        boardMapper.updateViewCount(boardId);

        // 게시글 정보 조회
        BoardDTO board = boardMapper.selectBoardById(boardId);

        // 첨부파일 목록 조회 후 바인딩
        if (board != null) {
            List<BoardFileDTO> files = boardMapper.selectFilesByBoardId(boardId);
            board.setFileList(files);
        }
        return board;
    }

    /**
     * 3. 게시글 및 첨부파일 등록 (트랜잭션 처리)
     */
    @Override
    @Transactional
    public boolean registerBoardWithFiles(BoardDTO board, List<MultipartFile> files) {
        // 1) 첨부파일 개수 검증 (최대 5개)
        if (files != null && files.size() > 5) {
            throw new IllegalArgumentException("첨부파일은 최대 5개까지만 등록 가능합니다.");
        }

        // 2) 게시글 DB 저장 (useGeneratedKeys에 의해 board.getBoardId()에 PK 주입됨)
        int result = boardMapper.insertBoard(board);
        if (result <= 0) {
            return false;
        }

        // 3) 첨부파일 처리
        if (files != null && !files.isEmpty()) {
            File dir = new File(uploadPath);
            if (!dir.exists()) {
                dir.mkdirs(); // 저장 디렉터리가 없으면 생성
            }

            for (MultipartFile file : files) {
                // 비어있는 파일 요청 방지
                if (file == null || file.isEmpty() || file.getOriginalFilename() == null || file.getOriginalFilename().isEmpty()) {
                    continue;
                }

                String originalName = file.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                String saveName = uuid + "_" + originalName;
                String filePath = uploadPath + saveName;

                try {
                    // 실제 디스크에 파일 저장
                    file.transferTo(new File(filePath));

                    // DB에 파일 정보 저장
                    BoardFileDTO fileDTO = new BoardFileDTO();
                    fileDTO.setBoardId(board.getBoardId()); // 생성된 게시글 PK 연결
                    fileDTO.setOriginalName(originalName);
                    fileDTO.setSaveName(saveName);
                    fileDTO.setFilePath(filePath);
                    fileDTO.setFileSize(file.getSize());

                    boardMapper.insertBoardFile(fileDTO);
                } catch (IOException e) {
                    // 파일 저장 실패 시 예외를 던져 @Transactional에 의해 DB 게시글 저장건도 자동 롤백
                    throw new RuntimeException("파일 저장 중 오류가 발생했습니다.", e);
                }
            }
        }
        return true;
    }

    /**
     * 4. 게시글 수정
     */
    @Override
    public boolean modifyBoard(BoardDTO board) {
        return boardMapper.updateBoard(board) > 0;
    }

    /**
     * 5. 게시글 삭제
     */
    @Override
    public boolean removeBoard(int boardId) {
        return boardMapper.deleteBoard(boardId) > 0;
    }
}