
INSERT INTO Thing (name,ownerID) values('Android',1),('Haus',1),('Blah',1);

INSERT INTO ActionDBObject (thingid, factoryString) 
values (1,'["de.uni_stuttgart.riot.thing.commons.action.PropertySetAction",{"propertyName":"P1-Name"}]'),
(1,'["de.uni_stuttgart.riot.thing.commons.action.PropertySetAction",{"propertyName":"P2-Name"}]'),
(2,'["de.uni_stuttgart.riot.thing.commons.action.PropertySetAction",{"propertyName":"P3-Name"}]');

INSERT INTO EventDBObject (thingid, factoryString) 
values (1,'["de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEvent",{"propertyName":"P1-Name"}]'),
(1,'["de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEvent",{"propertyName":"P2-Name"}]'),
(2,'["de.uni_stuttgart.riot.thing.commons.event.PropertyChangeEvent",{"propertyName":"P3-Name"}]');

INSERT INTO PropertyDBObject (name,val,valType,thingID)
values ('P1','"Default"','java.lang.String',1),
('P2','"Value"','java.lang.String',1),
('PX','"ValueX"','java.lang.String',2);