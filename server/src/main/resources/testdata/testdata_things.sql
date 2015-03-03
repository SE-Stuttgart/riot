
INSERT INTO things (id, type, ownerID, name) VALUES
(1, 'de.uni_stuttgart.riot.thing.test.TestThing', 0, 'My Test Thing');

INSERT INTO propertyValues (thingID, name, val) VALUES
(1, 'int', '42'),
(1, 'readonlyString', 'String from Database');
