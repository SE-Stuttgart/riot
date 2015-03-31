# The Rule Concept
Please first read the Thing concept. Generally speaking, rules are custom pieces of code that run on the server in order to control things automatically based on events.
A rule is only a "recipe" and needs to be instantiated. For their execution, most rules will require parameters (like the things they operate on and other values to modify their behavior).
These parameters need to be specified by the user before the rule can be instantiated. The rule then runs on the server and reacts to events received from the executing client things.

## Authorization
Each rule instance has an owner, which is the system user who created the rule instance. Only this user is permitted to edit or delete the rule.
If a rule accesses a thing, it's the rule owner who needs to have the required permissions on the thing.

# Implementation
## Relevant classes
The `Rule` class is the base class for all rule implementations. A rule is identified by the fully qualified name of its Java type.
However, it is possible to create deeper inheritance hierarchies between the `Rule` base class and the instantiable child classes.

The `RuleConfiguration` class contains the status and parameter values for a single instance of a rule.
In particular, there can be multiple instances of a rule type, for the same user or for different users.

## Rule Status
A rule can be activated or deactivated by the user. This is done by editing the rule and changing its status field.
In addition, the rule may itself switch to a failure state, if it failed during execution. The user then needs to fix the problem and reactivate the rule.

## Creating a new rule
To create a new rule, first look at the example rules.
Then create a new class that inherits from `Rule` and is in a subpackage of `de.uni_stuttgart.riot` (otherwise the rule won't be detected).
By inheriting from `Rule`, you automatically get methods for initializing and shutting down, which you will probably need to use.
It is important to shut down rules properly, i.e., to unregister all events and scheduled tasks that were registered!

### Rule Parameters
Most rules will need parameters. There are scalar parameters (class `RuleParameter`) and reference parameters (class `ReferenceParameter` or `ThingParameter`).
You can create a parameter by creating a field in your class with one of these three types. As the type argument, specify the value type of the parameter.
You do not need to initialize these fields! They must not be final and will be initialized automatically.

Suitable value types for `RuleParameter` are strings and numbers. Use the `@Parameter` annotation to specify the UI hint (type of UI control, min/max values, etc.).
From your rule implementation code, you can retrieve the parameter values with `someParameter.get()`.

To reference a `Thing` from your rule (in order to read or observe or control it), create a `ThingParameter`. Using the type argument, you can narrow down the kind of referenced thing.
For example, `ThingParameter<CoffeeMachine>` only references CoffeeMachines and subtypes thereof.
From your rule implementation code, you can retrieve the referenced thing with `someParameter.getTarget()`.
Note this method may throw a `ResolveReferenceException`, which can be handled by most methods you use in your rule.

You should annotate `ThingParameter`s with the `@Parameter` annotation to specify `ui=UIHint.ThingDropDown` as the UI hint and to possibly specify the required permissions (`requires` field).
The permissions default to `READ` and `CONTROL`.
This allows the UI to display correct candidates for use with newly created rules.

### Exception handling
A rule execution may fail for various reasons. You can call `errorOccured(Exception)` for a clean shutdown of your rule. It will then be displayed as failed to the user.
`ResolveReferenceException`s are pretty common in rules, because they reference things that might be gone in the meantime.
These exceptions are handled by the mandatory abstract methods and also by all lambda helpers. Other kinds of exceptions need to be handled manually and passed to `errorOccured(Exception)`.

### Event handling
Rules need to react to Thing events and property changes.
The `Rule` base class provides some helper methods that allow the use of Java 8 lambda expressions by converting a lambda expression to instances of `EventListener` or `PropertyListener`.
This is particularly useful in combination with method references (but can also be used with regular lambdas):
    private final PropertyListener<Integer> someListener = onPropertyChange(this::onChangeMethod);
    protected void initialize() ... {
	someThing.getTarget().getSomeEvent().register(someListener);
    }
    // unregister on shutdown
    private void onChangeMethod() {
    }

Similar helpers exist for lambdas that receive the new (and old) value of a property or for `EventListener`s (`onEvent` method).

### Scheduling
Rules may need to schedule tasks in the future. To do so, a rule should use the `ScheduledExecutorService` returned by `getScheduler()`.
For simple delays, there is the convenience method `delay(ms, runnable)` that works with lambda methods.

