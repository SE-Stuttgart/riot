DROP TABLE IF EXISTS
ruleparameters,
rules;

CREATE TABLE rules
(
id SERIAL,
type varchar(256) NOT NULL,
ownerID bigint,
name varchar(256) NOT NULL,
status varchar(20) NOT NULL,
PRIMARY KEY (id)
);

CREATE TABLE ruleparameters
(
ruleID bigint unsigned NOT NULL,
name varchar(128) NOT NULL,
val varchar(256),
FOREIGN KEY (ruleID) REFERENCES rules(id) ON DELETE CASCADE,
PRIMARY KEY (ruleID, name)
);