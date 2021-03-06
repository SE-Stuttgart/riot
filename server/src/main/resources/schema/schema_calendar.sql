DROP TABLE IF EXISTS calendarentries;
DROP TABLE IF EXISTS calendarEntries;

CREATE TABLE calendarentries (
	id INT NOT NULL AUTO_INCREMENT,
	startTime DATETIME NOT NULL,
	endTime DATETIME,
	allDayEvent BIT NOT NULL,
	description TEXT,
	location VARCHAR(200),
	title VARCHAR(200) NOT NULL,
	PRIMARY KEY (id)
);