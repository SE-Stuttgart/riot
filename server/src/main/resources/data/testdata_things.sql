
INSERT INTO things (id, type, ownerID, name) VALUES
(1, 'de.uni_stuttgart.riot.thing.house.coffeemachine.CoffeeMachine', 1, 'Coffemachine');

INSERT INTO things_users (id, thingID, userID, canRead, canControl, canExecute, canDelete, canShare) VALUES
(1, 1, 1, true, true, true, true, true);
