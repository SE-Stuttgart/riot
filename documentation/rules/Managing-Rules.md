# The Rule Concept
Please first read the Thing concept. Generally speaking, rules are custom pieces of code that run on the server in order to control things automatically based on events.
A rule is only a "recipe" and needs to be instantiated. For their execution, most rules will require parameters (like the things they operate on and other values to modify their behavior).
These parameters need to be specified by the user before the rule can be instantiated. The rule then runs on the server and reacts to events received from the executing client things.

# Server classes and interface
Rules are implemented as subclasses of `Rule` on the server (see Creating-Rules.md).
The `RuleConfigurationDAO` and `RuleLogic` manage these rules (or rather the configuration objects of the rule instances).
The `RuleLogic` takes care of starting and stopping rules.

Rules can be created and managed through the REST interface (see the REST documentation).
Only `RuleConfiguration` instances are passed over the REST interface. Rule types, which would normally be `Class` instances, are passed as Strings, since they don't exist on the client side.
For the creation of new rules, `RuleDescription`s can be retrieved from the server. They describe the structure of a `Rule` and especially its parameters.

# User interfaces
Both the web application and the Android application provide a User interface for creating, updating and deleting rules.
Rule parameter values can be entered through dynamically generated UI controls.
This means that it is sufficient for a rule implementor to implement a subclass of `Rule` and use the `@Parameter` annotation to provide UI hints.
In particular, it is not necessary to create a specific UI for a new rule type.

# Rule Status
There are the following statuses for rules:
* ACTIVE: The rule is activated and will be run by the server as soon as the server knows about it.
* DEACTIVATED: The rule has been deactivated on purpose and will not be run (or stopped if it is currently running).
* FAILED_REFERENCES: This status may not be set manually from the outside. The rule execution has failed because there is a reference parameter that points to a non-existing target.
* FAILED_PERMISSIONS: This status may not be set manually from the outside. The rule execution has failed because the rule owner does not have sufficient permissions on the referenced Things.
* FAILED: This status may not be set manually from the outside. The rule execution has failed for other reasons.