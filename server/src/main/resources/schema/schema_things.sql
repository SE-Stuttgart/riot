DROP TABLE IF EXISTS 
PropertyValue,
ActionDBObject,
EventDBObject,
PropertyDBObject,
Thing,
notification_arguments,
notifications,
propertyvalues,
things_users,
things;

CREATE TABLE things
(
id SERIAL,
type varchar(256) NOT NULL,
ownerID bigint,
name varchar(256),
parentID bigint unsigned,
FOREIGN KEY (parentID) REFERENCES things(id) ON DELETE SET NULL,
PRIMARY KEY (id)
);

CREATE TABLE propertyvalues
(
thingID bigint unsigned NOT NULL,
name varchar(128) NOT NULL,
val varchar(256),
FOREIGN KEY (thingID) REFERENCES things(id) ON DELETE CASCADE,
PRIMARY KEY (thingID, name)
);

CREATE TABLE things_users
(
id SERIAL,
thingID bigint unsigned NOT NULL,
userID bigint unsigned NOT NULL,
canRead boolean NOT NULL DEFAULT FALSE,
canControl boolean NOT NULL DEFAULT FALSE,
canExecute boolean NOT NULL DEFAULT FALSE,
canDelete boolean NOT NULL DEFAULT FALSE,
canShare boolean NOT NULL DEFAULT FALSE,
FOREIGN KEY (thingID) REFERENCES things(id) ON DELETE CASCADE,
FOREIGN KEY (userID) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE notifications
(
id SERIAL,
userID bigint unsigned NOT NULL,
thingID bigint unsigned,
name varchar(30) NOT NULL,
severity varchar(30) NOT NULL,
titleKey varchar(128) NOT NULL,
messageKey varchar(128) NOT NULL,
time datetime NOT NULL,
dismissed boolean NOT NULL,
FOREIGN KEY (thingID) REFERENCES things(id) ON DELETE CASCADE,
FOREIGN KEY (userID) REFERENCES users(id) ON DELETE CASCADE,
PRIMARY KEY (id)
);

CREATE TABLE notification_arguments
(
notificationID bigint unsigned NOT NULL,
name varchar(128) NOT NULL,
val varchar(512) NOT NULL,
valType varchar(64) NOT NULL,
FOREIGN KEY (notificationID) REFERENCES notifications(id) ON DELETE CASCADE,
PRIMARY KEY (notificationID, name)
);
