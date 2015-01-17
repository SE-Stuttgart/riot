DROP TABLE IF EXISTS 
Property,
RemoteThingAction,
Action,
Thing;

CREATE TABLE Thing
(
id SERIAL ,
name varchar(256) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE Action
(
id SERIAL,
className varchar(256) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE RemoteThingAction
(
id SERIAL , 
thingID bigint unsigned NOT NULL,
FOREIGN KEY (thingID) REFERENCES thing(id) ON DELETE CASCADE,
actionID bigint unsigned NOT NULL,
FOREIGN KEY (actionID) REFERENCES action(id) ON DELETE CASCADE,
PRIMARY KEY (id)
);


CREATE TABLE Property
(
id SERIAL , 
name varchar(256) NOT NULL,
val varchar(256) NOT NULL,
valType varchar(256) NOT NULL,
thingID bigint unsigned NOT NULL,
FOREIGN KEY (thingID) REFERENCES thing(id) ON DELETE CASCADE,
PRIMARY KEY (id)
);