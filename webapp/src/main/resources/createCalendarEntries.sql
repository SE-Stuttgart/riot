--<ScriptOptions statementTerminator=";"/>

ALTER TABLE calendarEntries DROP PRIMARY KEY;

DROP TABLE calendarEntries;

CREATE TABLE calendarEntries (
	id INT NOT NULL,
	startTime DATETIME NOT NULL,
	endTime DATETIME,
	allDayEvent BIT NOT NULL,
	description TEXT,
	location VARCHAR(200),
	title VARCHAR(200) NOT NULL,
	PRIMARY KEY (id)
);