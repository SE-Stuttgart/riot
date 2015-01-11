INSERT INTO calendarEntries(startTime, endTime, allDayEvent, description, location, title)
    VALUES (TIMESTAMP '2015-01-19 10:00:00',TIMESTAMP '2015-01-19 11:00:00', 0, 'Description 1', 'Vaihingen','Standup Meeting 1'),
      (TIMESTAMP '2015-02-19 10:00:00',TIMESTAMP '2015-02-19 11:00:00', 0, 'Description 2', 'Vaihingen','Standup Meeting 2'),
      (TIMESTAMP '2015-03-19 10:00:00',TIMESTAMP '2015-03-19 11:00:00', 0, 'Description 3', 'Vaihingen','Standup Meeting 3');
    
INSERT INTO contacts(firstName, lastName, email, phoneNumber)
    VALUES ( 'John', 'Doe', 'john.doe@example.com','+4915177777777'),
     ('Mary','Doe', 'mary.doe@example.com','+4915188888888');