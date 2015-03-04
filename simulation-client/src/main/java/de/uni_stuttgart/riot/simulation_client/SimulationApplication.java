package de.uni_stuttgart.riot.simulation_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.PropertyConfigurator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.riot.clientlibrary.LoginClient;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.RequestException;
import de.uni_stuttgart.riot.clientlibrary.usermanagement.client.TokenManager;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import javafx.application.Application;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * A JavaFX application for startup.
 * 
 * @author Philipp Keck
 */
public class SimulationApplication extends Application {

    private static final int THREAD_POOL_SIZE = 5;

    private static final long POLLING_INTERVAL = 100;

    private final Logger logger = LoggerFactory.getLogger(SimulationApplication.class);

    private final Properties settings = new Properties();

    private final ScheduledThreadPoolExecutor scheduler = new ScheduledThreadPoolExecutor(THREAD_POOL_SIZE);

    /**
     * Launches the JavaFX application.
     * 
     * @param args
     *            Command line arguments.
     */
    public static void main(String[] args) {
        PropertyConfigurator.configure(SimulationApplication.class.getResourceAsStream("log4j.properties"));
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception { // NOCS (Ignore path complexity, it is irrelevant here)

        // Check and load the settings.
        if (getParameters().getRaw().isEmpty()) {
            logger.error("Please specify the configuration file name as the first parameter!");
            System.exit(1);
        }
        File configurationFile = new File(getParameters().getRaw().get(0));
        if (!configurationFile.exists() || !configurationFile.isFile() || !configurationFile.canWrite()) {
            logger.error("The specified configuration file {} does not exist or is not writable!", configurationFile);
            logger.info("Path is {}", new File(".").getAbsolutePath());
            System.exit(1);
        }
        settings.load(new FileInputStream(configurationFile));

        // Determine the Thing subclass to be simulated.
        String className = settings.getProperty("type");
        if (className == null) {
            logger.error("Please specify an entry with key 'type' in the configuration file that specifies the fully qualified class name of the Thing to be simulated.");
            System.exit(1);
        }
        Class<? extends Thing> thingType;
        try {
            Class<?> thingClass = Class.forName(className);
            if (!Thing.class.isAssignableFrom(thingClass)) {
                throw new IllegalArgumentException("The class " + className + " is not a subclass of Thing!");
            }

            // Now that we checked it above, we can safely cast.
            @SuppressWarnings("unchecked")
            Class<? extends Thing> casted = (Class<? extends Thing>) thingClass;
            thingType = casted;
        } catch (IllegalArgumentException | ClassNotFoundException e) {
            logger.error("Invalid thing type {}", className, e);
            System.exit(1);
            return;
        }

        // Get the thing name and host address
        final String thingName = settings.getProperty("name");
        if (thingName == null || thingName.isEmpty()) {
            logger.error("Please specify the thing name as a property 'name' in the configuration file.");
            System.exit(1);
        }
        final String protocol = settings.getProperty("protocol", "https");
        final String host = settings.getProperty("host");
        if (host == null || host.isEmpty()) {
            logger.error("Please specify the host address (IP or domain name including port) as a property 'host' in the configuration file.");
            System.exit(1);
        }

        // Initialize the client library.
        final LoginClient loginClient = new LoginClient(protocol + "://" + host + "/riot", thingName, new TokenManager() {
            public void setRefreshToken(String refreshToken) {
                settings.setProperty("refreshToken", refreshToken);
                try {
                    settings.store(new FileOutputStream(configurationFile), null);
                } catch (IOException e) {
                    logger.error("Failed to save new refreshToken {}", refreshToken, e);
                }
            }

            public void setAccessToken(String accessToken) {
                settings.setProperty("accessToken", accessToken);
                try {
                    settings.store(new FileOutputStream(configurationFile), null);
                } catch (IOException e) {
                    logger.error("Failed to save new accessToken {}", accessToken, e);
                }
            }

            public String getRefreshToken() {
                return settings.getProperty("refreshToken");
            }

            public String getAccessToken() {
                return settings.getProperty("accessToken");
            }
        });

        // Login if necessary.
        if (!settings.containsKey("accessToken")) {
            Pair<String, String> credentials = LoginDialog.showDialog(primaryStage);
            if (credentials == null) {
                logger.info("Aborting due to missing credentials");
                System.exit(0);
            }

            try {
                loginClient.login(credentials.getKey(), credentials.getValue());
            } catch (Exception e) {
                logger.info("Failed to login using the specified credentials: " + e.getMessage());
                System.exit(1);
            }
        }

        // Create the ThingClient and the Thing that we will work with.
        final ThingClient thingClient = new ThingClient(loginClient);
        final ThingBehaviorFactory<SimulatedThingBehavior> simulatedBehaviorFactory = new ThingBehaviorFactory<SimulatedThingBehavior>() {
            @Override
            public SimulatedThingBehavior newBehavior(long thingID, String thingName, Class<? extends Thing> thingType) {
                return new SimulatedThingBehavior(thingClient);
            }

            @Override
            public void onThingCreated(Thing thing, SimulatedThingBehavior behavior) {
                // This is called when the thing has successfully been created. We ignore it.
            }
        };

        // Check if we have an ID already, otherwise retrieve one. The behavior will retrieve the thing.
        SimulatedThingBehavior behavior;
        try {
            if (settings.containsKey("thingId")) {
                int thingId = Integer.parseInt(settings.getProperty("thingId"));
                behavior = ExecutingThingBehavior.launchExistingThing(thingType, thingClient, thingId, simulatedBehaviorFactory);
            } else {
                behavior = ExecutingThingBehavior.launchNewThing(thingType, thingClient, thingName, simulatedBehaviorFactory);
                settings.setProperty("thingId", behavior.getThing().getId().toString());
                settings.store(new FileOutputStream(configurationFile), null);
            }
        } catch (RequestException e) {
            logger.error("Error when launching the thing", e);
            System.exit(1);
            return;
        }

        // Check if the created thing has the right type.
        if (!thingType.isInstance(behavior.getThing())) {
            logger.error("Expected thing of type {} but got {}", thingType, behavior.getThing());
            System.exit(1);
            return;
        }
        logger.info("Thing ID is {}", behavior.getThing().getId());

        // Initialize simulator, if specified.
        Simulator<?> simulator = null;
        if (settings.containsKey("simulator")) {
            String simulatorClassName = settings.getProperty("simulator");
            Class<? extends Simulator<?>> simulatorType;
            try {
                Class<?> simulatorClass = Class.forName(simulatorClassName);
                if (!Simulator.class.isAssignableFrom(simulatorClass)) {
                    throw new IllegalArgumentException("The simulator class " + simulatorClassName + " is not a subclass of Simulator!");
                }

                // Now that we checked it above, we can safely cast.
                @SuppressWarnings("unchecked")
                Class<? extends Simulator<?>> casted = (Class<? extends Simulator<?>>) simulatorClass;
                simulatorType = casted;
            } catch (IllegalArgumentException | ClassNotFoundException e) {
                logger.error("Invalid simulator type {}", simulatorClassName, e);
                System.exit(1);
                return;
            }

            try {
                Constructor<? extends Simulator<?>> constructor = simulatorType.getConstructor(thingType, ScheduledThreadPoolExecutor.class);
                simulator = constructor.newInstance(thingType.cast(behavior.getThing()), scheduler);
            } catch (NoSuchMethodException e) {
                logger.error("{} must have a constructor with argument types {} and ScheduledThreadPoolExecutor", simulatorType, thingType, e);
                System.exit(1);
                return;
            }
        }

        // Launch the UI
        // Start the simulation, if present.
        if (simulator != null) {
            final Simulator<?> fsimulator = simulator;
            fsimulator.startSimulation();
            window.setOnHidden((event) -> {
                fsimulator.shutdown();
                scheduler.shutdown();
            });
        }

        // Start regular polling for events.
        scheduler.scheduleWithFixedDelay(() -> {
            try {
                behavior.fetchUpdates();
            } catch (Exception e) {
                logger.error("Error during polling", e);
            }
        }, 0, POLLING_INTERVAL, TimeUnit.MILLISECONDS);
    }

}