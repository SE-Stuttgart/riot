
INSERT INTO Thing (id, type, ownerID, name) VALUES
(1, 'de.uni_stuttgart.riot.thing.test.TestThing', 0, 'My Test Thing');

INSERT INTO PropertyValue (thingID, name, val) VALUES
(1, 'int', '42'),
(1, 'readonlyString', 'String from Database');
