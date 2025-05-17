INSERT INTO users (user_id, password, nickname, role, banned, quit, warn_cnt, user_icon)
VALUES
    ('test-user1', '<PASSWORD>', 'the tester', 'USER', false, false, 0, 'test-user-icon'),
    ('test-user2', '<PASSWORD>', null, 'USER', false, true, 0, 'test-user-icon'),
    ('admin1', '<PASSWORD>', 'the admin', 'ADMIN', false, false, 0, 'test-user-icon'),
    ('black-sheep', '<PASSWORD>', 'the black sheep', 'USER', false, false, 2, 'test-user-icon'),
    ('blocked-user', '<PASSWORD>', null, 'USER', true, false, 2, 'test-user-icon'),
    ('only-view-user', '<PASSWORD>', null, 'USER', false, false, 0, 'test-user-icon');

