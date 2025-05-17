CREATE TABLE users (
                       user_id varchar(255) PRIMARY KEY,
                       password varchar(255) NOT NULL,
                       nickname varchar(255),
                       role ENUM('USER', 'ADMIN') NOT NULL DEFAULT 'USER',
                       banned bool NOT NULL DEFAULT false,
                       quit bool NOT NULL DEFAULT false,
                       warn_cnt tinyint NOT NULL DEFAULT 0,
                       user_icon varchar(50) NOT NULL DEFAULT 'mdi-account-circle'
);

CREATE TABLE posts (
                       post_id int PRIMARY KEY AUTO_INCREMENT,
                       user_id VARCHAR(255) NOT NULL,
                       title TINYTEXT NOT NULL,
                       code TEXT NOT NULL,
                       lang varchar(50) NOT NULL,
                       content TEXT NOT NULL,
                       written_at datetime NOT NULL DEFAULT CURRENT_TIMESTAMP,
                       deleted BOOL NOT NULL DEFAULT false,
                       FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE recommends (
                            user_id VARCHAR(255) NOT NULL,
                            post_id INT NOT NULL,
                            PRIMARY KEY (user_id, post_id),
                            FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
                            FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE comments (
                          comment_id INT PRIMARY KEY AUTO_INCREMENT,
                          post_id INT NOT NULL,
                          content TEXT NOT NULL,
                          writer VARCHAR(255) NOT NULL,
                          written_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                          deleted BOOL NOT NULL DEFAULT false,
                          FOREIGN KEY (writer) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
                          FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE bookmarks (
                           user_id VARCHAR(255),
                           post_id INT,
                           marked_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                           PRIMARY KEY (user_id, post_id),
                           FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE CASCADE ON UPDATE CASCADE,
                           FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE tags (
                      tag VARCHAR(255) PRIMARY KEY
);

CREATE TABLE post_tags (
                           post_id INT,
                           tag VARCHAR(256),
                           PRIMARY KEY (post_id, tag),
                           FOREIGN KEY (post_id) REFERENCES posts(post_id) ON DELETE CASCADE ON UPDATE CASCADE,
                           FOREIGN KEY (tag) REFERENCES tags(tag) ON DELETE CASCADE ON UPDATE CASCADE
);