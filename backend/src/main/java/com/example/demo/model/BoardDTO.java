package com.example.demo.model;

import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString // 디버그 시 로그창에 객체 안의 값을 예쁘게(boardId=1, title=...) 찍어주는 편리한 기능입니다.
public class BoardDTO {
    private int boardId;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime regDate;
    private int viewCnt;
    // [추가] 조회 시 첨부파일 목록을 담아 보낼 리스트
    private List<BoardFileDTO> fileList;
    // 🔥 목록 조회용 파일 개수 필드 추가
    private int fileCount;
}