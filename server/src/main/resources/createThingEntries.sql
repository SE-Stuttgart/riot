DROP TABLE IF EXISTS 
PropertyDBObject,
RemoteThingAction,
ActionDBObject,
Thing;

CREATE TABLE Thing
(
id SERIAL ,
name varchar(256) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE ActionDBObject
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