DROP TABLE IF EXISTS
roles_permissions,
tokens_roles,
users_roles,
roles,
permissions,
tokens,
users;

CREATE TABLE users 
(
id SERIAL NOT NULL,
username varchar(50) unique NOT NULL,
hashedPassword varchar(256) NOT NULL,
passwordSalt varchar(256) NOT NULL,
hashIterations int NOT NULL,
loginAttemptCount int NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE tokens 
(
id SERIAL,
userID bigint unsigned NOT NULL,
tokenValue varchar(100) unique NOT NULL,
refreshtokenValue varchar(100) unique  NOT NULL,
valid boolean NOT NULL,
issueTime timestamp NOT NULL default CURRENT_TIMESTAMP,
expirationTime timestamp NOT NULL,
PRIMARY KEY (id),
FOREIGN KEY (userID) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE permissions
(
id SERIAL,
permissionValue varchar(50) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE roles 
(
id SERIAL,
roleName varchar(50) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE users_roles 
(
id SERIAL,
userID bigint unsigned NOT NULL,
roleID bigint unsigned NOT NULL,
FOREIGN KEY (roleID) REFERENCES roles(id) ON DELETE CASCADE,
FOREIGN KEY (userID) REFERENCES users(id) ON DELETE CASCADE,
PRIMARY KEY (id),
UNIQUE(userID, roleID)
);

CREATE TABLE tokens_roles 
(
id SERIAL,
tokenID bigint unsigned NOT NULL,
roleID bigint unsigned NOT NULL,
FOREIGN KEY (roleID) REFERENCES roles(id) ON DELETE CASCADE,
FOREIGN KEY (tokenID) REFERENCES tokens(id) ON DELETE CASCADE,
PRIMARY KEY (id),
UNIQUE (tokenID, roleID)
);

CREATE TABLE roles_permissions 
(
id SERIAL,
permissionID bigint unsigned NOT NULL,
roleID bigint unsigned NOT NULL,
FOREIGN KEY (roleID) REFERENCES roles(id) ON DELETE CASCADE,
FOREIGN KEY (permissionID) REFERENCES permissions(id) ON DELETE CASCADE,
PRIMARY KEY (id),
UNIQUE (permissionID, roleID) 
);

