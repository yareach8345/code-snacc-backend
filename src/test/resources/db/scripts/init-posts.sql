INSERT INTO posts(post_id, user_id, title, code, lang, content, written_at, deleted)
VALUES
    (1, 'test-user1', 'test-post1', '<CODE>', '<LANG>', '<CONTENT>', '2025-05-10 00:00:00', false),
    (2, 'test-user1', 'test-post2', '<CODE>', '<LANG>', '<CONTENT>', '2025-05-10 00:00:01', false),
    (3, 'test-user2', 'test-post3', '<CODE>', '<LANG>', '<CONTENT>', '2025-05-10 00:00:06', false),
    (4, 'admin1', 'test-post4', '<CODE>', '<LANG>', '<CONTENT>', '2025-05-11 00:00:00', false);


INSERT INTO tags
VALUES
    ('test-tag1'),
    ('test-tag2'),
    ('test-tag3'),
    ('test-tag4'),
    ('test-tag5');

INSERT INTO post_tags(post_id, tag)
VALUES
    (1, 'test-tag1'),
    (1, 'test-tag2'),
    (2, 'test-tag1');


INSERT INTO recommends(user_id, post_id)
VALUES
    ('test-user1', 1),
    ('test-user2', 1),
    ('admin1', 1),
    ('black-sheep', 1),
    ('test-user2', 2),
    ('black-sheep', 2),
    ('test-user2', 3),
    ('test-user1', 4),
    ('test-user2', 4),
    ('admin1', 4),
    ('black-sheep', 4);
