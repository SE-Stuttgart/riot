package de.uni_stuttgart.riot.simulation_client;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Map;

import de.uni_stuttgart.riot.javafx.UIProducer;
import de.uni_stuttgart.riot.thing.ActionInstance;
import de.uni_stuttgart.riot.thing.BaseInstance;
import de.uni_stuttgart.riot.thing.BaseInstanceDescription;
import de.uni_stuttgart.riot.thing.Event;
import de.uni_stuttgart.riot.thing.EventInstance;
import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.PropertyChangeEvent;
import de.uni_stuttgart.riot.thing.Thing;
import javafx.application.Platform;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * A JavaFX window for displaying the simulation of a thing.
 * 
 * @author Philipp Keck
 */
public class SimulationWindow extends Stage {

    private static final int OUTER_PADDING = 10;
    private static final int INNER_SPACING = 5;
    private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    /**
     * The behavior of the thing.
     */
    private final SimulatedThingBehavior behavior;

    /**
     * The displalyed thing.
     */
    private final Thing thing;

    /**
     * Creates a new window.
     * 
     * @param behavior
     *            The behavior of the thing.
     */
    public SimulationWindow(SimulatedThingBehavior behavior) {
        this.behavior = behavior;
        this.thing = behavior.getThing();
        setScene(new Scene(produceContent()));
        getScene().getStylesheets().add("styles.css");
        setTitle(thing.getName() + " - Simulation");
    }

    /**
     * Produces the entire content for the window.
     * 
     * @return The window content.
     */
    private Parent produceContent() {
        TitledPane statePane = new TitledPane("Current state", produceStatePane());
        TitledPane actionsHistoryPane = new TitledPane("Actions History", produceActionsHistoryPane());
        TitledPane fireEventsPane = new TitledPane("Fire events", produceFireEventsPane());
        statePane.setCollapsible(false);
        actionsHistoryPane.setCollapsible(false);
        fireEventsPane.setCollapsible(false);

        HBox container = new HBox(INNER_SPACING, statePane, actionsHistoryPane, fireEventsPane);
        container.setPadding(new Insets(OUTER_PADDING));
        HBox.setHgrow(statePane, Priority.ALWAYS);
        HBox.setHgrow(actionsHistoryPane, Priority.ALWAYS);
        HBox.setHgrow(fireEventsPane, Priority.SOMETIMES);
        statePane.setMaxWidth(Double.MAX_VALUE);
        actionsHistoryPane.setMaxWidth(Double.MAX_VALUE);
        actionsHistoryPane.setMaxHeight(Double.MAX_VALUE);
        fireEventsPane.setMaxHeight(Double.MAX_VALUE);
        actionsHistoryPane.setPrefHeight(0);
        fireEventsPane.setPrefHeight(0);
        return container;
    }

    /**
     * PRoduces the content for the state pane, which will display the current value of all properties of the thing.
     * 
     * @return The state pane.
     */
    private Node produceStatePane() {
        GridPane container = new GridPane();
        container.setHgap(INNER_SPACING);
        container.setVgap(INNER_SPACING);
        int i = 0;
        for (Property<?> property : thing.getProperties()) {
            Label label = new Label(property.getName());
            Control valueControl = UIProducer.produceInternalControl(property, behavior);
            container.addRow(i, label, valueControl);
            GridPane.setHalignment(label, HPos.RIGHT);
            GridPane.setHgrow(valueControl, Priority.ALWAYS);
            i++;
        }
        return container;
    }

    /**
     * Creates a pane that contains all raised actions, from new to old.
     * 
     * @return The actions history pane.
     */
    private Node produceActionsHistoryPane() {
        ListView<ActionInstance> actionList = new ListView<>();
        behavior.actionInterceptors.add((actionInstance) -> {
            Platform.runLater(() -> {
                actionList.getItems().add(0, actionInstance);
            });
            return true;
        });
        actionList.setCellFactory((listView) -> {
            return new ListCell<ActionInstance>() {
                @Override
                protected void updateItem(ActionInstance item, boolean empty) {
                    super.updateItem(item, empty);
                    if (item != null) {
                        setText(instanceToString(item));
                    }
                }
            };
        });
        actionList.setOnMouseClicked((event) -> {
            if (event.getClickCount() == 2) {
                ActionInstance instance = actionList.getSelectionModel().getSelectedItem();
                if (instance != null) {
                    BaseInstanceWindow.displayInstance(instance);
                }
            }
        });
        return actionList;
    }

    /**
     * Creates a pane that contains a button for each action that the thing has. When the button is clicked, the action is fired, possibly
     * asking the user to enter the parameters first.
     * 
     * @return The fire events pane.
     */
    private Node produceFireEventsPane() {
        VBox container = new VBox(INNER_SPACING);
        for (Event<?> event : behavior.getEvents().values()) {
            if (event instanceof PropertyChangeEvent) {
                continue;
            }

            Button button = new Button();
            button.setText(event.getName());
            button.setOnAction((actionEvent) -> {
                EventInstance eventInstance = BaseInstanceWindow.enterNewInstance(event.getInstanceType(), thing.getId(), event.getName(), behavior.getClient().getJsonMapper());
                if (eventInstance != null) {
                    behavior.executeEvent(eventInstance);
                }
            });
            container.getChildren().add(button);
        }
        return container;
    }

    /**
     * Creates a String for displaying the instance. It consists of the time at which the action/event occurred, the name of the
     * action/instance and, if applicable, a list of all the parameter values. On the other hand, the type of the instance and the names of
     * the parameters are missing to save space.
     * 
     * @param instance
     *            The instance to be printed.
     * @return A String representation of the instance and its contents.
     */
    private static String instanceToString(BaseInstance instance) {
        StringBuilder builder = new StringBuilder();
        builder.append(DATE_FORMAT.format(instance.getTime()));
        builder.append(" - ");
        builder.append(instance.getName());

        if (instance.getClass() != ActionInstance.class && instance.getClass() != EventInstance.class) {
            Map<String, Object> parameters = BaseInstanceDescription.getParameterValues(instance);
            if (!parameters.isEmpty()) {
                builder.append(" (");
                boolean isFirst = true;
                for (Object parameterValue : parameters.values()) {
                    if (isFirst) {
                        isFirst = false;
                    } else {
                        builder.append(", ");
                    }
                    builder.append(parameterValue);
                }
                builder.append(")");
            }
        }
        return builder.toString();
    }

}
