package com.example.demo.model;

import java.time.LocalDateTime;

public class BoardDTO {
    private int boardId;
    private String title;
    private String content;
    private String writer;
    private LocalDateTime regDate;
    private int viewCnt;

    // Getter, Setter (Lombok 라이브러리를 쓴다면 @Getter @Setter 어노테이션으로 대체 가능합니다)
    public int getBoardId() { return boardId; }
    public void setBoardId(int boardId) { this.boardId = boardId; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }
    public String getWriter() { return writer; }
    public void setWriter(String writer) { this.writer = writer; }
    public LocalDateTime getRegDate() { return regDate; }
    public void setRegDate(LocalDateTime regDate) { this.regDate = regDate; }
    public int getViewCnt() { return viewCnt; }
    public void setViewCnt(int viewCnt) { this.viewCnt = viewCnt; }
}
