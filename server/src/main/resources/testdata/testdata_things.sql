
INSERT INTO things (id, type, ownerID, name) VALUES
(1, 'de.uni_stuttgart.riot.thing.test.TestThing', 0, 'My Test Thing'),
(2, 'de.uni_stuttgart.riot.thing.test.TestThing', 1, 'My Test Thing2');


INSERT INTO propertyValues (thingID, name, val) VALUES
(1, 'int', '42'),
(1, 'readonlyString', 'String from Database');
