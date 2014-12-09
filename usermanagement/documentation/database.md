# Usermanagement Database
This document describes the database relevant topics of the usermanagement.
 
## DB-Schema
The following figue shows the database schema that is used to store all information regarding the usermanagement. 

![Schema](image/usermanagement_db_erm.png
  "Schema")

* The users entity hold all basic attributes of a user, such as username, password and so on. 
* The roles entity holds the roles available in the system. 
* The permissions entity holds the permissions which could be assigned to roles.
* The tokens entity holds all token values, such as the access-token itself, refresh-token, issue date, and so on

All SQL-scripts for this schema are available at: 
`usermanagement/project/src/main/resources`

### Constrain
users.tokens.token_roles.roles is part of user.user_roles.roles 

## Data Access Objects

All data is accessible over DAO's. So for each entity there is a DAO which provides the following operations:

* insert
* update
* delete
* findByID
* findByValue

The DAO's are located in the following package: `de.uni_stuttgart.riot.usermanagement.data.sqlQueryDao` 