DROP TABLE IF EXISTS 
PropertyValue,
ActionDBObject,
EventDBObject,
PropertyDBObject,
Thing,
propertyvalues,
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