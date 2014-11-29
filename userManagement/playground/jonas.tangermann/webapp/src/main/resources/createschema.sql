DROP TABLE IF EXISTS
roles_permissions,
tokens_roles,
users_roles,
users,
tokens,
permissions,
roles;

CREATE TABLE users 
(
userID bigint,
username varchar(50),
pword varchar(50),
pword_salt varchar(50),
PRIMARY KEY (userID)
);

CREATE TABLE tokens 
(
tokenID bigint,
userID bigint,
tokenValue char(100),
issueDate date,
expirationDate date,
PRIMARY KEY (tokenID),
FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE
);

CREATE TABLE permissions
(
permissionID bigint,
permissionValue varchar(50),
PRIMARY KEY (permissionID)
);

CREATE TABLE roles 
(
roleID bigint,
roleName varchar(50),
PRIMARY KEY (roleID)
);

CREATE TABLE users_roles 
(
userRoleID bigInt,
userID bigint,
roleID bigint,
FOREIGN KEY (roleID) REFERENCES roles(roleID) ON DELETE CASCADE,
FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE,
PRIMARY KEY (userRoleID)
);

CREATE TABLE tokens_roles 
(
tokenRoleID bigint,
tokenID bigint,
roleID bigint,
FOREIGN KEY (roleID) REFERENCES roles(roleID) ON DELETE CASCADE,
FOREIGN KEY (tokenID) REFERENCES tokens(tokenID) ON DELETE CASCADE,
PRIMARY KEY (tokenRoleID)
);

CREATE TABLE roles_permissions 
(
rolePermissionID bigint,
permissionID bigint,
roleID bigint,
FOREIGN KEY (roleID) REFERENCES roles(roleID) ON DELETE CASCADE,
FOREIGN KEY (permissionID) REFERENCES permissions(permissionID) ON DELETE CASCADE,
PRIMARY KEY (rolePermissionID)
);
