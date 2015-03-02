DROP TABLE IF EXISTS 
PropertyValue,
ActionDBObject,
EventDBObject,
PropertyDBObject,
Thing;

CREATE TABLE Thing
(
id SERIAL,
type varchar(256) NOT NULL,
ownerID bigint,
name varchar(256) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE PropertyValue
(
thingID bigint unsigned NOT NULL,
name varchar(128) NOT NULL,
val varchar(256) NOT NULL,
FOREIGN KEY (thingID) REFERENCES thing(id) ON DELETE CASCADE,
PRIMARY KEY (thingID, name)
);