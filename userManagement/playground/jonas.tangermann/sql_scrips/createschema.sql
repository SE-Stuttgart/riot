DROP TABLE IF EXISTS 
users_groups,
groups_roles,
roles_permissions,
tokens_roles,
users_roles,
users,
tokens,
permissions,
groups,
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
FOREIGN KEY (userID) REFERENCES users(userID)
);

CREATE TABLE permissions
(
permissionID bigint,
permissionValue varchar(50),
PRIMARY KEY (permissionID)
);

CREATE TABLE groups 
(
groupID bigint,
groupName varchar(50),
PRIMARY KEY (groupID)
);



CREATE TABLE roles 
(
roleID bigint,
roleName varchar(50),
PRIMARY KEY (roleID)
);

CREATE TABLE users_roles 
(
userID bigint,
roleID bigint,
FOREIGN KEY (roleID) REFERENCES roles(roleID),
FOREIGN KEY (userID) REFERENCES users(userID),
PRIMARY KEY (userID,roleID)
);

CREATE TABLE tokens_roles 
(
tokenID bigint,
roleID bigint,
FOREIGN KEY (roleID) REFERENCES roles(roleID),
FOREIGN KEY (tokenID) REFERENCES tokens(tokenID),
PRIMARY KEY (tokenID,roleID)
);

CREATE TABLE roles_permissions 
(
permissionID bigint,
roleID bigint,
FOREIGN KEY (roleID) REFERENCES roles(roleID),
FOREIGN KEY (permissionID) REFERENCES permissions(permissionID),
PRIMARY KEY (permissionID,roleID)
);

CREATE TABLE groups_roles 
(
groupID bigint,
roleID bigint,
FOREIGN KEY (roleID) REFERENCES roles(roleID),
FOREIGN KEY (groupID) REFERENCES groups(groupID),
PRIMARY KEY (roleID,groupID)
);

CREATE TABLE users_groups 
(
userID bigint,
groupID bigint,
FOREIGN KEY (userID) REFERENCES users(userID),
FOREIGN KEY (groupID) REFERENCES groups(groupID),
PRIMARY KEY (userID,groupID)
);
