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
userID SERIAL,
username varchar(50) unique,
password varchar(50),
password_salt varchar(50),
PRIMARY KEY (userID)
);

CREATE TABLE tokens 
(
tokenID SERIAL,
userID bigint,
tokenValue varchar(100),
refreshtokenValue varchar(100),
issueDate timestamp,
expirationDate timestamp,
PRIMARY KEY (tokenID),
FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE
);

CREATE TABLE permissions
(
permissionID SERIAL,
permissionValue varchar(50),
PRIMARY KEY (permissionID)
);

CREATE TABLE roles 
(
roleID SERIAL,
roleName varchar(50),
PRIMARY KEY (roleID)
);

CREATE TABLE users_roles 
(
userRoleID SERIAL,
userID bigint,
roleID bigint,
FOREIGN KEY (roleID) REFERENCES roles(roleID) ON DELETE CASCADE,
FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE,
PRIMARY KEY (userRoleID)
);

CREATE TABLE tokens_roles 
(
tokenRoleID SERIAL,
tokenID bigint,
roleID bigint,
FOREIGN KEY (roleID) REFERENCES roles(roleID) ON DELETE CASCADE,
FOREIGN KEY (tokenID) REFERENCES tokens(tokenID) ON DELETE CASCADE,
PRIMARY KEY (tokenRoleID)
);

CREATE TABLE roles_permissions 
(
rolePermissionID SERIAL,
permissionID bigint,
roleID bigint,
FOREIGN KEY (roleID) REFERENCES roles(roleID) ON DELETE CASCADE,
FOREIGN KEY (permissionID) REFERENCES permissions(permissionID) ON DELETE CASCADE,
PRIMARY KEY (rolePermissionID)
);

