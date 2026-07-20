-- MariaDB 게시판 스키마
CREATE DATABASE IF NOT EXISTS board_db DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

CREATE USER IF NOT EXISTS 'board_user'@'%' IDENTIFIED BY 'board_pass';
GRANT ALL PRIVILEGES ON board_db.* TO 'board_user'@'%';
FLUSH PRIVILEGES;

USE board_db;

CREATE TABLE `vueboard` (
                            `board_id` int(11) NOT NULL AUTO_INCREMENT,
                            `title` varchar(100) NOT NULL,
                            `content` text NOT NULL,
                            `writer` varchar(50) NOT NULL,
                            `reg_date` datetime DEFAULT current_timestamp(),
                            `view_cnt` int(11) DEFAULT 0,
                            PRIMARY KEY (`board_id`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;


CREATE TABLE board_file (
                            file_id INT AUTO_INCREMENT PRIMARY KEY,
                            board_id INT NOT NULL,                     -- 게시글 ID (FK)
                            original_name VARCHAR(255) NOT NULL,       -- 사용자가 올린 원본 파일명
                            save_name VARCHAR(255) NOT NULL,           -- 서버에 실제 저장된 파일명 (UUID 중복방지)
                            file_path VARCHAR(500) NOT NULL,           -- 저장 경로
                            file_size BIGINT NOT NULL,                 -- 파일 용량
                            reg_date DATETIME DEFAULT CURRENT_TIMESTAMP,
                            FOREIGN KEY (board_id) REFERENCES vueboard(board_id) ON DELETE CASCADE
);