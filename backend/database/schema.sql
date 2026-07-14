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