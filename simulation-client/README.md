# Simulation Client
The simulation client is a JavaFX UI (requires Java 8) for executing things. That is, the thing is not mirrored but directly simulated by the client. There are two basic ways of simulation that can be used simultaneously:

* You can use the generated UI that will open when you run the application to simulate the behavior of the thing yourself, i.e., by observing the actions that were fired on the thing and reacting with appropriate events being fired and properties being changed.
* You can write and use a simulator that specifically handles the behavior of your thing. To do so, you need to extend the Simulator class and register it in your configuration file (see below).  

# Folders
The folder src/main/resources/exampleConfigurations contains example configurations for simulated things.
Please do not execute these configurations, use the folder testConfigurations for that instead.
The latter is in .gitignore, so you can do whatever you want in there.

# File Format
A configuration should contain the following properties:

* name: The name of the thing (choose anything you like, avoid duplicates if possible).
* type: The type of the thing, that is, its fully qualified class name.
* host: The host (including port number separated by a colon, which must be escaped in .properties files!) to connect to.
* simulator: Optionally, you can specify your simulator class here. It will be started automatically, in addition to the UI.

Additionally, the running simulation uses this file to store some state information. Usually, this does not include the actual business state of the thing, but only authentication and identification information:

* accessToken, refreshToken: The tokens obtained from the server. Note that passwords are never stored.
* thingId: The thingId that was assigned. Delete this to create a new thing, change it to pick up on an existing thing. 


# How to Launch
Just launch the SimulationApplication Java class and specify the file name of the .properties file as the first command line parameter.
In Eclipse, run it once (it will complain), then click on the arrow next to the run icon, click "Run Configurations" (same for Debug), switch to the "Arguments" tab and enter the file name in the field "Program arguments":
src/main/resources/testConfigurations/someFileOfYours.properties