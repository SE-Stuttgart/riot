DROP TABLE IF EXISTS 
PropertyValue,
ActionDBObject,
EventDBObject,
PropertyDBObject,
Thing,
propertyvalues,
things_users,
things;

CREATE TABLE things
(
id SERIAL,
type varchar(256) NOT NULL,
ownerID bigint,
name varchar(256) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE propertyvalues
(
thingID bigint unsigned NOT NULL,
name varchar(128) NOT NULL,
val varchar(256) NOT NULL,
FOREIGN KEY (thingID) REFERENCES things(id) ON DELETE CASCADE,
PRIMARY KEY (thingID, name)
);

CREATE TABLE things_users
(
id SERIAL,
thingID bigint unsigned NOT NULL,
userID bigint unsigned NOT NULL,
permission varchar(10) NOT NULL,
FOREIGN KEY (thingID) REFERENCES things(id) ON DELETE CASCADE,
FOREIGN KEY (userID) REFERENCES users(id) ON DELETE CASCADE
);