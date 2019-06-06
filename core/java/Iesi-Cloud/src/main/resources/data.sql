INSERT INTO users (username,password,enabled) 
    VALUES ('admin','$2a$10$5Abn80LM.au6d7PWmy5KpuWgeTE7qhnWPbnsKLsfJxjEidmN4YfzO', TRUE);
--    e9:wVS;f*[]QFp42/3yA&'Q+73)/Gn4?

INSERT INTO groups (id, group_name) VALUES (1, 'USER_GROUP');
INSERT INTO groups (id, group_name) VALUES (2, 'ADMIN_GROUP');

INSERT INTO authorities (group_id, authority, username) VALUES (2, 'AUTHORIZED_ADMIN','admin');