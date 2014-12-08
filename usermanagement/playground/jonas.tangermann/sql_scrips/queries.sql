-- Getting the password of a given user
SELECT pword FROM users WHERE username = 'Username';

-- Getting the password and password salt of a given user
SELECT pword, pword_salt FROM users WHERE username = 'Yoda';

-- Getting all roles assigned to a user
SELECT rolename FROM roles
	INNER JOIN users_roles ON roles.roleID = users_roles.roleID
	INNER JOIN users ON users.userID = users_roles.userID
		where users.username = 'R2D2';

-- Getting all permissions of a role
SELECT permissionvalue FROM permissions
	INNER JOIN roles_permissions ON roles_permissions.permissionID = permissions.permissionID
	INNER JOIN roles ON roles.roleID = roles_permissions.roleID
		where roles.rolename = 'Master';

-- Getting all tokens ever generated for a user
SELECT tokens.* FROM tokens 
	INNER JOIN users ON users.userID = tokens.userID
		WHERE users.username = 'Yoda';

-- Getting all roles associated with a token
SELECT roles.rolename FROM roles
	INNER JOIN tokens_roles ON tokens_roles.roleID = roles.roleID
	INNER JOIN tokens ON tokens.tokenID = tokens_roles.tokenID
		WHERE tokens.tokenvalue = 'token1';

-- Getting all permissions of a given user
SELECT permissionvalue FROM 
(
SELECT roles.roleID,users.username FROM roles
	INNER JOIN users_roles ON roles.roleID = users_roles.roleID
	INNER JOIN users ON users.userID = users_roles.userID
) AS allroles
	INNER JOIN roles_permissions ON roles_permissions.roleID = allroles.roleID
	INNER JOIN permissions ON permissions.permissionID = roles_permissions.permissionID
		where allroles.username = 'Yoda';


-- Getting all permissions of a given user + the information over which way did the user get the permission
SELECT rolename,permissionvalue FROM 
(
SELECT roles.roleID, roles.rolename,users.username FROM roles
	INNER JOIN users_roles ON roles.roleID = users_roles.roleID
	INNER JOIN users ON users.userID = users_roles.userID
) AS allroles
	INNER JOIN roles_permissions ON roles_permissions.roleID = allroles.roleID
	INNER JOIN permissions ON permissions.permissionID = roles_permissions.permissionID
		where allroles.username = 'Vader';

-- Test if a given Token is valid
SELECT true as Res FROM tokens
	WHERE tokenValue = 'token1' AND issuedate <= now() AND expirationdate >= now();

-- Deleting a User
DELETE FROM users WHERE users.userID = 1;

-- Inserting a  new user

INSERT INTO users(
            userid, username, pword, pword_salt)
    VALUES (?, ?, ?, ?);

-- Update a User
UPDATE users
   SET userid=?, username=?, pword=?, pword_salt=?
 WHERE userID = ?;

-- FIND User by search param
SELECT userid, username, pword, pword_salt FROM users 
	WHERE username in ('Yoda','Vader'); 














