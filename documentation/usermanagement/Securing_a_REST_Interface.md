# Securing a REST Interface

## Purpose of this document
This document shall show how a REST Interface can be secured with the RIOT subproject *usermanagement*.

## Securing
The securing works in two layers. First a user has to be authenticated. To get authenticated the user provides a user name and password and gets in return an access token and a refresh token. For any further authenticating the access token is used. This access token is only valid a limited time (currently 2h). After it is not valid anymore, the refresh token is used to get a new pair of access and refresh token.

The second layer contains the authorization process and requires that a user is authenticated correctly. The authorization process ensures, that a user gets only access to ressources intended for him. This is done by the usage of *roles* and *permissions*. If a ressource requires a specific role or permission, the user needs this role or permission in order to access the ressource.

### Usage of Authentication
To tell the application, that only authenticated users can access a resource, the annotation `@RequiresAuthentication` has to be used. Nothing more has to be done.

Example:

    @PUT
    @Path("/logout")
    @RequiresAuthentication
    public Response logout() throws LogoutException {
        // place code here
    }

### Usage of Authorization

If the access to a ressource should be restricted further, a *Role* and/or a *Permission* can be used.

#### Roles

To use roles the annotation `@RequiresRoles("PlaceRoleHere")` has to be used above a REST-Method.

Example (in order to access this ressource the user needs the role *Robot*):

    @GET
    @RequiresAuthentication
    @RequiresRoles("Robot")
    public Collection<UserResponse> getUsers() throws UserManagementException {
        // place code here
    }

#### Permissions

Permissions define an explicit behaviour or action and define *what* can be done. The *don't* define who can access a ressource. 

Examples of permissions:

* Open a file
* Write to a file
* Print a file

Permissions for the previous examples could look like this:
	
	file:open
	file:write
	file:print

If somebody should be able to open a file and write to it, but unable to print it, the permission could look like this:

	file:open,write

If a user should be able to perform any operation with a file, a wildcard can be used:

	file:*

To get a better understanding about permissions the Apache Shiro [documentation](http://shiro.apache.org/permissions.html) can be used.

To use a permission in the code, the annotation `@RequiresPermission` has to be used above a REST-Method.

Example (in order to access this ressource the user needs the permission to see the active access tokens):

    @GET
    @Path("/{userID}/tokens")
    @RequiresAuthentication
    @RequiresPermissions("users:viewTokens")
    public Collection<TokenResponse> getUserTokens(@PathParam("userID") Long userID) throws UserManagementException {
        // place code here
    }




















