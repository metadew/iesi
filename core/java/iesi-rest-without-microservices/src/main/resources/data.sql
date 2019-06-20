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
    'https://localhost:8080',
    'ROLE_CLIENT',
    2500,
    250000
  );