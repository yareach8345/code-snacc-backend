INSERT INTO comments (comment_id, post_id, content, writer, deleted)
VALUES
    (0, 0, '<TEST COMMENT1>', 'test-user1', false),
    (1, 1, '<TEST COMMENT1>', 'test-user1', false),
    (2, 0, '<TEST COMMENT1>', 'test-user2', false),
    (3, 0, '<TEST COMMENT1>', 'admin1', false),
    (4, 2, '<TEST COMMENT1>', 'admin1', false);

ALTER TABLE posts ALTER COLUMN post_id RESTART WITH 5;
