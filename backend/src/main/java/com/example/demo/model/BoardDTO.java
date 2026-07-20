package com.example.demo.model;

import java.time.LocalDateTime;
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
}