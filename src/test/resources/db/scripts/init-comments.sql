INSERT INTO comments (comment_id, post_id, content, writer, deleted)
VALUES
    (1, 1, '<TEST COMMENT1>', 'test-user1', false),
    (2, 2, '<TEST COMMENT1>', 'test-user1', false),
    (3, 1, '<TEST COMMENT1>', 'test-user2', false),
    (4, 1, '<TEST COMMENT1>', 'admin1', false),
    (5, 3, '<TEST COMMENT1>', 'admin1', false);
