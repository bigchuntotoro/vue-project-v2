package com.example.demo.model;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import java.time.LocalDateTime;

@Getter
@Setter
@ToString
public class BoardFileDTO {
    private int fileId;
    private int boardId;
    private String originalName;
    private String saveName;
    private String filePath;
    private long fileSize;
    private LocalDateTime regDate;
}