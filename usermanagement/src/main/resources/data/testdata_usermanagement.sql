-- Passwords:
-- 	Yoda: YodaPW
--	R2D2: R2D2PW
-- 	Vader: VaderPW
INSERT INTO users(username, email,hashedPassword, passwordSalt, hashIterations, loginAttemptCount)
    VALUES ('Yoda','yoda@force.org','yPYMjqXzWOPKaAKNJXfEw7Gu3EnckZmoWUuEhOqz/7IqGd4Ub+3/X3uANlO0mkIOqIMhxhUi/ieU1KZt2BK+eg==', '108bgacl42gihhrdlfcm8e6ls6gm2q45q00boauiv5kkduf1ainv', 200000, 0),
     ('R2D2','r2d2@rob.org','xGWcc3nOCCgWwtRoo/2pu2/nynq1wCGuDKz8SwDtbWI+Jmv4UHNEFOK0RFLxfWUEfCsRfgspbKKdfMxY6/Ndwg==', '3uuc05ili6e8j18lqehv1piugel2bteviv6m351q1rvnn7e49gk', 200000, 0), 
     ('Vader','vader@sith.org','catB93X7ygpJ1NjjFQlgXicAc1JUNGiZeZ0OOL95gsR8xZXLaer3EY/IXtDKFCL9Ye6RZfaILPF6FINQcgNpEg==', 're46m591d4el6t3d9ljq52itve8ml7jmf5c6i1pniuie6qqe0t', 200000, 0);

INSERT INTO tokens(userID, tokenvalue, refreshtokenValue,valid, expirationtime)
    VALUES ( 1, 'token1','token1R', true, TIMESTAMP '2024-10-19 10:23:54'),
     ( 2, 'token2','token2R', true, TIMESTAMP '2024-10-19 10:23:54'),
     ( 3, 'token3','token3R', false, TIMESTAMP '2024-10-19 10:23:54');
    
INSERT INTO roles(rolename)
    VALUES ( 'Master'),
     ('Robot'),
     ('Good'),
     ('Dark');

INSERT INTO permissions( permissionvalue)
    VALUES ('thing:1:*'),
     ('thing:create'),
     ('thing:3:read'),
     ('thing:*'),
     ('x'),
     ('y');

INSERT INTO roles_permissions(permissionid, roleid)
    VALUES (1, 1),
     (4, 2),
     (3, 3),
     (2, 4);

INSERT INTO users_permissions(permissionid, userid)
    VALUES (4, 1),
     (2, 1),
     (3, 3);

INSERT INTO tokens_roles(tokenid, roleid)
    VALUES (1, 1),
     (2, 2),
     (3, 4),
     (1, 3);

INSERT INTO users_roles(userid, roleid)
    VALUES (1, 1),
     (3, 1),
     (2,2);



