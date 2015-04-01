package de.uni_stuttgart.riot.simulation_client;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.util.Properties;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.reflect.ConstructorUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import de.uni_stuttgart.riot.clientlibrary.ConnectionInformation;
import de.uni_stuttgart.riot.clientlibrary.ConnectionInformationProvider;
import de.uni_stuttgart.riot.clientlibrary.RequestException;
import de.uni_stuttgart.riot.clientlibrary.ServerConnector;
import de.uni_stuttgart.riot.thing.SingleUseThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.Thing;
import de.uni_stuttgart.riot.thing.ThingBehaviorFactory;
import de.uni_stuttgart.riot.thing.client.ExecutingThingBehavior;
import de.uni_stuttgart.riot.thing.client.ThingClient;
import javafx.application.Application;
import javafx.stage.FileChooser;
import javafx.stage.FileChooser.ExtensionFilter;
import javafx.stage.Stage;
import javafx.util.Pair;

/**
 * A JavaFX application for startup.
 * 
 * @author Philipp Keck
 */
public class SimulationApplication extends Application {

    private static final String REST_ROOT_PATH = "/riot/api/v1/";
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
        launch(args);
    }

    public void start(Stage primaryStage) throws Exception { // NOCS (Ignore path complexity, it is irrelevant here)

        // Check and load the settings.
        File configurationFile;
        if (getParameters().getRaw().isEmpty()) {
            FileChooser fileChooser = new FileChooser();
            fileChooser.getExtensionFilters().add(new ExtensionFilter("Simulation Properties File", "*.properties"));
            fileChooser.setTitle("Please choose a settings file");
            File resourcesFolder = new File("src/main/resources");
            if (resourcesFolder.exists()) {
                fileChooser.setInitialDirectory(resourcesFolder);
            } else {
                fileChooser.setInitialDirectory(new File("."));
            }
            configurationFile = fileChooser.showOpenDialog(primaryStage);
            if (configurationFile == null) {
                logger.info("No file selected, aborting");
                System.exit(0);
                return;
            }
        } else {
            configurationFile = new File(getParameters().getRaw().get(0));
        }
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
            thingType = Class.forName(className).asSubclass(Thing.class);
        } catch (ClassCastException | ClassNotFoundException e) {
            logger.error("Invalid thing type {}", className, e);
            System.exit(1);
            return;
        }

        // Get the thing name, host address and port.
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
        final int port = Integer.parseInt(settings.getProperty("port", "8181"));

        // Initialize the client library.
        final ServerConnector connector = new ServerConnector(new ConnectionInformationProvider() {
            public void setRefreshToken(String refreshToken) {
                if (refreshToken == null) {
                    settings.remove("refreshToken");
                } else {
                    settings.setProperty("refreshToken", refreshToken);
                }
                try {
                    settings.store(new FileOutputStream(configurationFile), null);
                } catch (IOException e) {
                    logger.error("Failed to save new refreshToken {}", refreshToken, e);
                }
            }

            public void setAccessToken(String accessToken) {
                if (accessToken == null) {
                    settings.remove("accessToken");
                } else {
                    settings.setProperty("accessToken", accessToken);
                }
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

            public boolean relogin(ServerConnector serverConnector) {
                Pair<String, String> credentials = LoginDialog.showDialog(primaryStage);
                if (credentials == null) {
                    logger.info("Aborting due to missing credentials");
                    System.exit(0);
                    return false;
                }

                try {
                    serverConnector.login(credentials.getKey(), credentials.getValue());
                    return true;
                } catch (Exception e) {
                    logger.info("Failed to login using the specified credentials: " + e.getMessage());
                    System.exit(1);
                    return false;
                }
            }

            public ConnectionInformation getNewInformation(ConnectionInformation oldInformation) {
                return null; // Not supported!
            }

            public ConnectionInformation getInformation() {
                return new ConnectionInformation(protocol, host, port, REST_ROOT_PATH);
            }

            public void invalidateAccessToken() {
                setAccessToken(null);
            }

            @Override
            public boolean handlesTokenRefresh() {
                return false;
            }
        });

        // Create the ThingClient and the Thing that we will work with.
        final ThingClient thingClient = new ThingClient(connector);
        final ThingBehaviorFactory<SimulatedThingBehavior> simulatedBehaviorFactory = new SingleUseThingBehaviorFactory<SimulatedThingBehavior>(new SimulatedThingBehavior(thingClient));

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
            try {
                @SuppressWarnings("unchecked")
                Class<? extends Simulator<?>> simulatorType = (Class<? extends Simulator<?>>) Class.forName(simulatorClassName).asSubclass(Simulator.class);
                Constructor<? extends Simulator<?>> constructor = ConstructorUtils.getMatchingAccessibleConstructor(simulatorType, thingType, ScheduledThreadPoolExecutor.class);
                if (constructor == null) {
                    logger.error("{} must have a constructor with argument types {} and ScheduledThreadPoolExecutor", simulatorClassName, thingType);
                    System.exit(1);
                    return;
                }
                simulator = constructor.newInstance(thingType.cast(behavior.getThing()), scheduler);
            } catch (ClassCastException | ClassNotFoundException e) {
                logger.error("Invalid simulator type {}", simulatorClassName, e);
                System.exit(1);
                return;
            }
        }

        // Launch the UI
        SimulationWindow window = new SimulationWindow(behavior);
        window.show();

        // Start the simulation, if present.
        if (simulator != null) {
            final Simulator<?> fsimulator = simulator;
            fsimulator.startSimulation();
            window.setOnHidden((event) -> {
                fsimulator.shutdown();
                scheduler.shutdown();
                behavior.shutdown();
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
