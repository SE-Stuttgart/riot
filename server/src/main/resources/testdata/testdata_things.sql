
INSERT INTO things (id, type, ownerID, name) VALUES
(1, 'de.uni_stuttgart.riot.thing.test.TestThing', 0, 'My Test Thing'),
(2, 'de.uni_stuttgart.riot.thing.test.TestThing', 1, 'My Test Thing2');


INSERT INTO propertyValues (thingID, name, val) VALUES
(1, 'int', '42'),
(1, 'readonlyString', 'String from Database'),
(1, 'ref', NULL);

INSERT INTO things_users (id, thingID, userID, permission) VALUES
(1, 1, 1, 'FULL'),
(2, 2, 2, 'FULL');