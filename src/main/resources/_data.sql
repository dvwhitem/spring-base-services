INSERT INTO users (email, password) VALUES ('demo@localhost', '$2a$10$ebyC4Z5WtCXXc.HGDc1Yoe6CLFzcntFmfse6/pTj7CeDY5I05w16C');
INSERT INTO roles(name) VALUES('ROLE_ADMIN'); 
INSERT INTO user_roles(role_id, user_id) VALUES(1,1);
