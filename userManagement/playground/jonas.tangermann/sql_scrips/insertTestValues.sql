INSERT INTO users(userid, username, pword, pword_salt)
    VALUES (1,'Yoda','YodaPW', 'YodaSalt'),
     (2,'R2D2','R2D2PW', 'R2D2Salt'),
     (3,'Vader','VaderPW', 'VaderSalt');

INSERT INTO tokens(tokenid, userID, tokenvalue, issuedate, expirationdate)
    VALUES (1, 1, 'token1', '2014-11-26', '2014-11-26'),
     (2, 2, 'token2', '2014-11-26', '2020-11-26'),
     (3, 3, 'token3', '2014-11-26', '2020-11-26');
    
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


INSERT INTO groups(groupid, groupname)
    VALUES (1, 'Goodside'), 
     (2, 'Darkside');

INSERT INTO groups_roles(groupid, roleid)
    VALUES (1, 3),
     (2, 4);

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

INSERT INTO users_groups(userid, groupid)
    VALUES (1, 1),
     (2, 1),
     (3, 2);

INSERT INTO users_roles(userid, roleid)
    VALUES (1, 1),
     (3, 1),
     (2,2);



