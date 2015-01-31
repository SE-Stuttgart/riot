DROP TABLE IF EXISTS 
PropertyDBObject,
RemoteThingAction,
RemoteThingEvent,
ActionDBObject,
EventDBObject,
Thing;

CREATE TABLE Thing
(
id SERIAL ,
ownerID bigint NOT NULL,
name varchar(256) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE ActionDBObject
(
id SERIAL,
factoryString varchar(256) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE EventDBObject
(
id SERIAL,
factoryString varchar(256) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE RemoteThingAction
(
id SERIAL , 
thingID bigint unsigned NOT NULL,
FOREIGN KEY (thingID) REFERENCES thing(id) ON DELETE CASCADE,
actionID bigint unsigned NOT NULL,
FOREIGN KEY (actionID) REFERENCES ActionDBObject(id) ON DELETE CASCADE,
PRIMARY KEY (id)
);

CREATE TABLE RemoteThingEvent
(
id SERIAL , 
thingID bigint unsigned NOT NULL,
FOREIGN KEY (thingID) REFERENCES thing(id) ON DELETE CASCADE,
eventID bigint unsigned NOT NULL,
FOREIGN KEY (eventID) REFERENCES EventDBObject(id) ON DELETE CASCADE,
PRIMARY KEY (id)
);


CREATE TABLE PropertyDBObject
(
id SERIAL , 
name varchar(256) NOT NULL,
val varchar(256) NOT NULL,
valType varchar(256) NOT NULL,
thingID bigint unsigned NOT NULL,
FOREIGN KEY (thingID) REFERENCES thing(id) ON DELETE CASCADE,
PRIMARY KEY (id)
);