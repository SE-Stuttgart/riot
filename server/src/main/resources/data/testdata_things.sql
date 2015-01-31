
INSERT INTO Thing (name,ownerID) values('Android',1),('Haus',1),('Blah',1);

INSERT INTO ActionDBObject (thingid, factoryString) 
values (1,'PropertySetAction(P1,String)'),
(1,'PropertySetAction(P2,String)'),
(2,'PropertySetAction(PX,String)');

INSERT INTO EventDBObject (thingid, factoryString) 
values (1,'PropertyChangeEvent(P1,String)'),
(1,'PropertyChangeEvent(P2,String)'),
(2,'PropertyChangeEvent(PX,String)');

INSERT INTO PropertyDBObject (name,val,valType,thingID)
values ('P1','Default','String',1),
('P2','Value','String',1),
('PX','ValueX','String',2);