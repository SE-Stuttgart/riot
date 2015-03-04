package de.uni_stuttgart.riot.simulation_client;

import java.util.Objects;

import de.uni_stuttgart.riot.thing.Property;
import de.uni_stuttgart.riot.thing.Thing;
import javafx.application.Platform;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
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
        setTitle(thing.getName() + " - Simulation");
    }

    /**
     * Produces the entire content for the window.
     * 
     * @return The window content.
     */
    private Parent produceContent() {
        TitledPane statePane = new TitledPane("Current state", produceStatePane());
        statePane.setCollapsible(false);

        VBox container = new VBox(INNER_SPACING, statePane);
        container.setPadding(new Insets(OUTER_PADDING));
        return container;
    }

    /**
     * PRoduces the content for the state pane, which will display the current value of all properties of the thing.
     * 
     * @return The state pane.
     */
    private Node produceStatePane() {
        GridPane container = new GridPane();
        int i = 0;
        for (Property<?> property : behavior.getProperties().values()) {
            Label label = new Label(property.getName());
            Label valueDisplay = new Label(Objects.toString(property.getValue()));
            property.getChangeEvent().register((event, changeEvent) -> {
                Platform.runLater(() -> {
                    valueDisplay.setText(Objects.toString(changeEvent.getNewValue()));
                });
            });
            container.addRow(i, label, valueDisplay);
            i++;
        }
        return container;
    }

}
