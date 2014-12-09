INSERT INTO users(username, password, password_salt)
    VALUES ('Yoda','YodaPW', 'YodaSalt'),
     ('R2D2','R2D2PW', 'R2D2Salt'),
     ('Vader','VaderPW', 'VaderSalt');

INSERT INTO tokens(userID, tokenvalue, refreshtokenValue, issuedate, expirationdate)
    VALUES ( 1, 'token1','token1R', TIMESTAMP '2004-10-19 10:23:54', TIMESTAMP '2024-10-19 10:23:54'),
     ( 2, 'token2','token2R', TIMESTAMP '2004-10-19 10:23:54', TIMESTAMP '2024-10-19 10:23:54'),
     ( 3, 'token3','token3R', TIMESTAMP '2004-10-19 10:23:54', TIMESTAMP '2024-10-19 10:23:54');
    
INSERT INTO roles(rolename)
    VALUES ( 'Master'),
     ('Robot'),
     ('Good'),
     ('Dark');

INSERT INTO permissions( permissionvalue)
    VALUES ('lightsaber:*'),
     ('darkstar:*'),
     ('x'),
     ('y');

INSERT INTO roles_permissions(permissionid, roleid)
    VALUES (1, 1),
     (4, 2),
     (3, 3),
     (2, 4);

INSERT INTO tokens_roles(tokenid, roleid)
    VALUES (1, 1),
     (2, 2),
     (3, 4),
     (1, 3);

INSERT INTO users_roles(userid, roleid)
    VALUES (1, 1),
     (3, 1),
     (2,2);



