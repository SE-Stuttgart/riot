
INSERT INTO Thing (name,ownerID) values('Android',1);

INSERT INTO ActionDBObject (factoryString) 
values ('PropertySetAction(P1,String)'),
('PropertySetAction(P2,String)');

INSERT INTO RemoteThingAction (thingID,actionID)
values (1,1),(1,2);

INSERT INTO PropertyDBObject (name,val,valType,thingID)
values ('P1','Default','String',1),
('P2','Value','String',1);