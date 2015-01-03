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
userID SERIAL NOT NULL,
username varchar(50) unique NOT NULL,
password varchar(256) NOT NULL,
password_salt varchar(256) NOT NULL,
hash_iterations int NOT NULL,
PRIMARY KEY (userID)
);

CREATE TABLE tokens 
(
tokenID SERIAL,
userID bigint unsigned NOT NULL,
tokenValue varchar(100) unique NOT NULL,
refreshtokenValue varchar(100) unique  NOT NULL,
valid boolean NOT NULL,
issueDate timestamp NOT NULL default CURRENT_TIMESTAMP,
expirationDate timestamp NOT NULL,
PRIMARY KEY (tokenID),
FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE
);

CREATE TABLE permissions
(
permissionID SERIAL,
permissionValue varchar(50) NOT NULL,
PRIMARY KEY (permissionID)
);

CREATE TABLE roles 
(
roleID SERIAL,
roleName varchar(50) NOT NULL,
PRIMARY KEY (roleID)
);

CREATE TABLE users_roles 
(
userRoleID SERIAL,
userID bigint unsigned NOT NULL,
roleID bigint unsigned NOT NULL,
FOREIGN KEY (roleID) REFERENCES roles(roleID) ON DELETE CASCADE,
FOREIGN KEY (userID) REFERENCES users(userID) ON DELETE CASCADE,
PRIMARY KEY (userRoleID),
UNIQUE(userID, roleID)
);

CREATE TABLE tokens_roles 
(
tokenRoleID SERIAL,
tokenID bigint unsigned NOT NULL,
roleID bigint unsigned NOT NULL,
FOREIGN KEY (roleID) REFERENCES roles(roleID) ON DELETE CASCADE,
FOREIGN KEY (tokenID) REFERENCES tokens(tokenID) ON DELETE CASCADE,
PRIMARY KEY (tokenRoleID),
UNIQUE (tokenID, roleID)
);

CREATE TABLE roles_permissions 
(
rolePermissionID SERIAL,
permissionID bigint unsigned NOT NULL,
roleID bigint unsigned NOT NULL,
FOREIGN KEY (roleID) REFERENCES roles(roleID) ON DELETE CASCADE,
FOREIGN KEY (permissionID) REFERENCES permissions(permissionID) ON DELETE CASCADE,
PRIMARY KEY (rolePermissionID),
UNIQUE (permissionID, roleID) 
);

