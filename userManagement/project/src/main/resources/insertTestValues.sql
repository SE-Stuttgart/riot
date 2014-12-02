INSERT INTO users(userid, username, pword, pword_salt)
    VALUES (1,'Yoda','YodaPW', 'YodaSalt'),
     (2,'R2D2','R2D2PW', 'R2D2Salt'),
     (3,'Vader','VaderPW', 'VaderSalt');

INSERT INTO tokens(tokenid, userID, tokenvalue, issuedate, expirationdate)
    VALUES (1, 1, 'token1', TIMESTAMP '2004-10-19 10:23:54', TIMESTAMP '2024-10-19 10:23:54'),
     (2, 2, 'token2', TIMESTAMP '2004-10-19 10:23:54', TIMESTAMP '2024-10-19 10:23:54'),
     (3, 3, 'token3', TIMESTAMP '2004-10-19 10:23:54', TIMESTAMP '2024-10-19 10:23:54');
    
INSERT INTO roles(roleid, rolename)
    VALUES (1, 'Master'),
     (2, 'Robot'),
     (3, 'Good'),
     (4, 'Dark');

INSERT INTO permissions(permissionid, permissionvalue)
    VALUES (1, 'lightsaber:*'),
     (2, 'darkstar:*'),
     (3, 'x'),
     (4, 'y');

INSERT INTO roles_permissions(rolePermissionID,permissionid, roleid)
    VALUES (1,1, 1),
     (2,4, 2),
     (3,3, 3),
     (4,2, 4);

INSERT INTO tokens_roles(tokenRoleID,tokenid, roleid)
    VALUES (1,1, 1),
     (2,2, 2),
     (3,3, 4),
     (4,1, 3);

INSERT INTO users_roles(userRoleID,userid, roleid)
    VALUES (1,1, 1),
     (2,3, 1),
     (3,2,2);



