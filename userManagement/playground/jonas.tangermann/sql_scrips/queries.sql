-- Getting the password of a given user
SELECT pword FROM users WHERE username = 'Username';

-- Getting the password and password salt of a given user
SELECT pword, pword_salt FROM users WHERE username = 'Yoda';

-- Getting all groups assigned to a user
SELECT groupname FROM groups 
	INNER JOIN users_groups on groups.groupID = users_groups.groupID 
	INNER JOIN users on users_groups.userID = users.userID 
		WHERE users.username = 'Username';

-- Getting all roles assigned to a user over groups
SELECT rolename FROM roles 
	INNER JOIN groups_roles ON groups_roles.roleID = roles.roleID
	INNER JOIN groups ON groups.groupID = groups_roles.groupID
	INNER JOIN users_groups on groups.groupID = users_groups.groupID
	INNER JOIN users ON users.userID = users_groups.userID
		where users.username = 'Username';

-- Getting all roles assigned directly to a user
SELECT rolename FROM roles
	INNER JOIN users_roles ON roles.roleID = users_roles.roleID
	INNER JOIN users ON users.userID = users_roles.userID
		where users.username = 'R2D2';

-- Getting all roles assigned to a user
SELECT rolename FROM 
(
SELECT users.username, roles.rolename FROM roles 
	INNER JOIN groups_roles ON groups_roles.roleID = roles.roleID
	INNER JOIN groups ON groups.groupID = groups_roles.groupID
	INNER JOIN users_groups on groups.groupID = users_groups.groupID
	INNER JOIN users ON users.userID = users_groups.userID
UNION
SELECT users.username, roles.rolename FROM roles
	INNER JOIN users_roles ON roles.roleID = users_roles.roleID
	INNER JOIN users ON users.userID = users_roles.userID
) AS allroles
	WHERE username = 'Yoda';

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
	INNER JOIN groups_roles ON groups_roles.roleID = roles.roleID
	INNER JOIN groups ON groups.groupID = groups_roles.groupID
	INNER JOIN users_groups on groups.groupID = users_groups.groupID
	INNER JOIN users ON users.userID = users_groups.userID
UNION
SELECT roles.roleID,users.username FROM roles
	INNER JOIN users_roles ON roles.roleID = users_roles.roleID
	INNER JOIN users ON users.userID = users_roles.userID
) AS allroles
	INNER JOIN roles_permissions ON roles_permissions.roleID = allroles.roleID
	INNER JOIN permissions ON permissions.permissionID = roles_permissions.permissionID
		where allroles.username = 'Yoda';


-- Getting all permissions of a given user + the information over which way did the user get the permission
SELECT groupname,rolename,permissionvalue FROM 
(
SELECT roles.roleID,groups.groupname,roles.rolename,users.username FROM roles 
	INNER JOIN groups_roles ON groups_roles.roleID = roles.roleID
	INNER JOIN groups ON groups.groupID = groups_roles.groupID
	INNER JOIN users_groups on groups.groupID = users_groups.groupID
	INNER JOIN users ON users.userID = users_groups.userID
UNION
SELECT roles.roleID,'DIRECT' as groupname, roles.rolename,users.username FROM roles
	INNER JOIN users_roles ON roles.roleID = users_roles.roleID
	INNER JOIN users ON users.userID = users_roles.userID
) AS allroles
	INNER JOIN roles_permissions ON roles_permissions.roleID = allroles.roleID
	INNER JOIN permissions ON permissions.permissionID = roles_permissions.permissionID
		where allroles.username = 'Username';


-- Test if a given Token is valid
SELECT true as Res FROM tokens
	WHERE tokenValue = 'token1' AND issuedate <= now() AND expirationdate >= now();

















