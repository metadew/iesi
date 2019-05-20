
INSERT INTO
  oauth_client_details (
    client_id,
    client_secret,
    scope,
    authorized_grant_types,
    web_server_redirect_uri,
    authorities,
    access_token_validity,
    refresh_token_validity
  )
VALUES
  (
    'client-id',
    '$2a$10$vCXMWCn7fDZWOcLnIEhmK.74dvK1Eh8ae2WrWlhr2ETPLoxQctN4.',
    'openid',
    'authorization_code,check_token,refresh_token,password',
    'http://localhost:8081/login',
    'ROLE_CLIENT',
    2500,
    250000
  );


INSERT INTO users (username,password,enabled) 
    VALUES ('john', '$2a$10$vCXMWCn7fDZWOcLnIEhmK.74dvK1Eh8ae2WrWlhr2ETPLoxQctN4.', TRUE);
   INSERT INTO users (username,password,enabled) 
    VALUES ('admin','$2a$10$dJYXnn2ztBU72ooXSyl9.u/xF29ADNp3mbQYy/6cvkiGLskNG1UQi', TRUE);
  
INSERT INTO groups (id, group_name) VALUES (2, 'ADMIN_GROUP');
INSERT INTO groups (id, group_name) VALUES (1, 'USER_GROUP');

INSERT INTO authorities (group_id, authority, username) VALUES (1, 'AUTHORIZED_USER','john');
INSERT INTO authorities (group_id, authority, username) VALUES (2, 'AUTHORIZED_ADMIN','admin');