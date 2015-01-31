
INSERT INTO Thing (name,ownerID) values('Android',1),('Haus',1),('Blah',1);

INSERT INTO ActionDBObject (factoryString) 
values ('PropertySetAction(P1,String)'),
('PropertySetAction(P2,String)'),
('PropertySetAction(PX,String)');

INSERT INTO EventDBObject (factoryString) 
values ('PropertyChangeEvent(P1,String)'),
('PropertyChangeEvent(P2,String)'),
('PropertyChangeEvent(PX,String)');

INSERT INTO RemoteThingAction (thingID,actionID)
values (1,1),(1,2),(2,3);

INSERT INTO RemoteThingEvent (thingID,eventID)
values (1,1),(1,2),(2,3);

INSERT INTO PropertyDBObject (name,val,valType,thingID)
values ('P1','Default','String',1),
('P2','Value','String',1),
('PX','ValueX','String',2);