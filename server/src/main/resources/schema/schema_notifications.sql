DROP TABLE IF EXISTS 
notification_arguments,
notifications;

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