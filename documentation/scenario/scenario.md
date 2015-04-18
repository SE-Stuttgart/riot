# How to build a new Internet of Things Scenario
This short tutorial shows how to create a new "Internet of Things Scenario" in RIOT. This includes the creation of new things as well as the implementation of new rules. A thing is everything that should be monitored and or controlled. In example a hi-fi equipment. Rules are used to define automated behavior between two or more things. In example to turn on a defined wake up song list on the hi-fi equipment when the alarm clock thing sends the alarm event.

## Implementing a new thing
In order to create a new thing you have to create a new class per thing that extends the base class **de.uni_stuttgart.riot.thing.Thing**.

	public class HiFi extends Thing {
		...
	}

### Defining the attributes
The next step is to define all of the attributes of the thing. There are to possible types of attributes, which are **de.uni_stuttgart.riot.thing.WritableProperty** and **de.uni_stuttgart.riot.thing.Property**. WritableProperties could be  changed directly through the generic UI (Web and Android). Properties could be changed only internally though the thing implementation, but may be changed indirectly through actions. 

    private final WritableProperty<Double> volume = newWritableProperty("volume", Double.class, 0, UIHint.fractionalSlider(0.0, 100.0));

    private final Property<Double> internalTemp = newProperty("temperature", Double.class, 0, UIHint.editNumber());
    
The parameters of the **newWritableProperty** and **newProperty** operations are:

* attribute name
* attribute type
* default/start value
* UI definition

The UI definition is made through the class **UIHint**, which offers the following possibilities:

* UIHint.FractionalSlider
* UIHint.PercentageSlider
* UIHint.ToggleButton
* UIHint.EditText
* UIHint.EditNumber
* UIHint.EnumDropDown
* UIHint.ReferenceDropDown
* UIHint.ThingDropDown

### Adding actions and events
Things communicate by sending/receiving actions and events. Events and actions without parameters could be defined as follows.

    private final Action<ActionInstance> turnOnAction = newAction("Turn on");

    private final Event<EventInstance> turnedOnEvetn = newEvent("turned on");

If parameters are necessary you have to implement either a class extending **de.uni_stuttgart.riot.thing.EventInstance** for Events or **de.uni_stuttgart.riot.thing.ActionInstance** for actions.

    private final Event<OverHeating> overHeating = newEvent("Over Heating", OverHeating.class);
    
	private final Action<PlayList> playList = newAction("Play List", PlayList.class);
    
    
The corresponding event and action classes could look like this:

    public class OverHeating extends EventInstance {

	    @Parameter(ui = UIHint.EditNumber.class)
	    private final double temp;
	
	    public OverHeating(Event<? extends EventInstance> event, double temp) {
	        super(event);
	        this.temp = temp;
	    }
	
	    @JsonCreator
	    public OutOfGasoline(JsonNode node) {
	        super(node);
	        this.temp = node.get("temp").asDouble();
	    }
		...
	}
	
	public class PlayList extends ActionInstance {

	    @Parameter(ui = UIHint.EditText.class)
	    private final String list;
	
	    public PlayList(Action<? extends ActionInstance> action, String list) {
	        super(action);
	        this.list = list;
	    }
	
	    @JsonCreator
	    public Refuel(JsonNode node) {
	        super(node);
	        this.list = node.get("list").asDouble();
	    }
	    ...
	}

### Implementing the simulation
Now that the static part of new thing is fully defined you can continue by implementing the behavior. Either by connecting the thing definition to a real thing (not done yet) or by implementing a simulation. A simulation can be implemented by extending **de.uni_stuttgart.riot.simulation_client.Simulator**.

	public class HiFiSimulator extends Simulator<HiFi> {

	    public RadiatorSimulator(Radiator thing, ScheduledThreadPoolExecutor scheduler) {
	        super(thing, scheduler);
	    }
	
	    @Override
	    protected <A extends ActionInstance> void executeAction(Action<A> action, A actionInstance) {
	    	/* Handle the incoming actioninstances.
	    	 * May send events and or actions.
	    	 */
	    }
		...
	}
	
Note that there are some useful operations in the super classes regarding simulation (eg. *linearChange(...)*).

## Defining rules
Now that you have implemented all things of your scenario you can go on by implementing the scenario rules. Rules can be created by extending **de.uni_stuttgart.riot.rule.Rule**. The following example shows how a rule could be implemented that starts playing music on the hifi if the alarm clock rings.

	public class MorningMusicRule extends Rule {

		...
	
	    @Parameter(ui = UIHint.ThingDropDown.class, requires = ThingPermission.READ)
	    private ThingParameter<AlarmClock> alarmClock;
	
	    @Parameter(ui = UIHint.ThingDropDown.class, requires = ThingPermission.CONTROL)
	    private ThingParameter<HiFi> hifi;
	
	    private final EventListener<EventInstance> listener = onEvent(() -> {
	            MorningMusikRule.this.doMorningMusicActions();
	    });
	
	    @Override
	    protected void initialize() throws ResolveReferenceException, IllegalRuleConfigurationException {
			AlarmClock clock = alarmClock.getTarget();
        	clock.getAlarmEvent().register(listener);
		}
	
	    @Override
	    protected void shutdown() throws ResolveReferenceException {
        	this.alarmClock.getTarget().getAlarmEvent().unregister(listener);
	    }
	
	    private void doMorningMusicActions() throws ResolveReferenceException {
	      this.startMusic();
	    }
		...
	}

Do not forget to unregister all registered listeners in the shutdown() operation.

# Read code
For more inspirations just look at the things and rules that are already implemented. Things could be found in the commons project, rules are contained in the server project.

