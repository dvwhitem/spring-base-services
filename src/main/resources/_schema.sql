create table oauth_client_details (
  client_id VARCHAR(256) PRIMARY KEY,
  resource_ids VARCHAR(256),
  client_secret VARCHAR(256),
  scope VARCHAR(256),
  authorized_grant_types VARCHAR(256),
  web_server_redirect_uri VARCHAR(256),
  authorities VARCHAR(256),
  access_token_validity INTEGER,
  refresh_token_validity INTEGER,
  additional_information VARCHAR(4096),
  autoapprove VARCHAR(256)
);

create table oauth_client_token (
  token_id VARCHAR(256),
  token bytea,
  authentication_id VARCHAR(256),
  user_name VARCHAR(256),
  client_id VARCHAR(256)
);

create table oauth_access_token (
  token_id VARCHAR(256),
  token bytea,
  authentication_id VARCHAR(256),
  user_name VARCHAR(256),
  client_id VARCHAR(256),
  authentication bytea,
  refresh_token VARCHAR(256)
);

create table oauth_refresh_token (
  token_id VARCHAR(256),
  token bytea,
  authentication bytea
);

create table oauth_code (
  code VARCHAR(256), authentication bytea
);

create table oauth_approvals (
  userId VARCHAR(256),
  clientId VARCHAR(256),
  scope VARCHAR(256),
  status VARCHAR(10),
  expiresAt TIMESTAMP,
  lastModifiedAt TIMESTAMP
);

-------------------------------------------------------


CREATE TABLE roles
(
   id int PRIMARY KEY NOT NULL,
   name varchar(255)
)
;
CREATE TABLE user_roles
(
   role_id int NOT NULL,
   user_id bigint NOT NULL,
   CONSTRAINT user_roles_pkey PRIMARY KEY (role_id,user_id)
)
;
CREATE TABLE users
(
   id bigint PRIMARY KEY NOT NULL,
   email varchar(255) NOT NULL,
   password varchar(255) NOT NULL
)
;
CREATE UNIQUE INDEX roles_pkey ON roles(id)
;
ALTER TABLE user_roles
ADD CONSTRAINT fkh8ciramu9cc9q3qcqiv4ue8a6
FOREIGN KEY (role_id)
REFERENCES roles(id)
;
ALTER TABLE user_roles
ADD CONSTRAINT fkhfh9dx7w3ubf1co1vdev94g3f
FOREIGN KEY (user_id)
REFERENCES users(id)
;
ALTER TABLE user_roles
ADD CONSTRAINT fk_g1uebn6mqk9qiaw45vnacmyo2
FOREIGN KEY (user_id)
REFERENCES users(id)
;
ALTER TABLE user_roles
ADD CONSTRAINT fk_5q4rc4fh1on6567qk69uesvyf
FOREIGN KEY (role_id)
REFERENCES roles(id)
;
CREATE UNIQUE INDEX user_roles_pkey ON user_roles
(
  role_id,
  user_id
)
;
CREATE UNIQUE INDEX uk_g1uebn6mqk9qiaw45vnacmyo2 ON user_roles(user_id)
;
CREATE UNIQUE INDEX users_pkey ON users(id)
;