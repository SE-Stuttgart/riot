
INSERT INTO rules (id, type, ownerID, name, status) VALUES
(1, 'de.uni_stuttgart.riot.rule.test.TestAdditionRule', 1, 'My Test Addition Rule', 'DEACTIVATED');

INSERT INTO ruleparameters (ruleID, name, val) VALUES
(1, 'inputThing', '1'),
(1, 'outputThing', '2'),
(1, 'intAdd', '42');