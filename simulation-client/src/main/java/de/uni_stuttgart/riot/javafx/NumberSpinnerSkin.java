/**
 * Copyright 2013-2014 Universitaet Stuttgart FMI SchulScheduler Team
 * Team members: Mark Aukschlat, Philipp Keck, Mathias Landwehr, Dominik Lekar,
 * Dennis Maseluk, Alexander Miller, Sebastian Pirk, Sven Schnaible
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package de.uni_stuttgart.riot.javafx;

import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.DoubleExpression;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.IndexRange;
import javafx.scene.control.Skin;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.math.BigDecimal;

/**
 * Der Skin für die {@link NumberSpinner}-Komponente.<br>
 * Quelle: http://myjavafx.blogspot.de/2013/05/developing-numberspinner-control.html (mit Genehmigung per ICQ)
 * https://bitbucket.org/sco0ter/extfx/src (Original ist unter MIT Lizenz)
 * 
 * @author Christian Schudt
 * @author Philipp Keck
 */
public class NumberSpinnerSkin extends StackPane implements Skin<NumberSpinner> {

    /**
     * Diese Eigenschaft kann leider nicht per CSS gesetzt werden, daher wird sie über Java definiert.
     */
    private static final int MAX_ARROW_SIZE = 8;

    /**
     * Die äußere Komponente, die dargestellt wird.
     */
    private final NumberSpinner numberSpinner;

    /**
     * Das Textfeld, das zur Darstellung und Eingabe der Zahl verwendet wird.
     */
    private final TextField textField = new TextField();

    /**
     * Der Button zum Erhöhen des Wertes.
     */
    private final Button btnIncrement = new Button();

    /**
     * Der Button zum Verringern des Wertes.
     */
    private final Button btnDecrement = new Button();

    /**
     * Die Box, die die beiden Buttons enthält.
     */
    private final VBox buttonBox = new VBox();

    /**
     * Der Listener wird zwischengespeichert, damit er beim Aufruf von {@link #dispose()} wieder entfernt werden kann.
     */
    private ChangeListener<IndexRange> changeListenerSelection;

    /**
     * Der Listener wird zwischengespeichert, damit er beim Aufruf von {@link #dispose()} wieder entfernt werden kann.
     */
    private ChangeListener<Number> changeListenerCaretPosition;

    /**
     * Der Listener wird zwischengespeichert, damit er beim Aufruf von {@link #dispose()} wieder entfernt werden kann.
     */
    private ChangeListener<Number> changeListenerValue;

    /**
     * Erstellt eine neue Skin-Instanz.
     * 
     * @param numberSpinner
     *            Die Komponente.
     */
    public NumberSpinnerSkin(final NumberSpinner numberSpinner) {

        // Die eigene äußere Komponente anbinden
        this.numberSpinner = numberSpinner;
        minHeightProperty().bind(numberSpinner.minHeightProperty());

        // Die inneren Komponenten anbinden
        initTextField();
        initButtons();

        // Die inneren Komponenten in der äußeren anzeigen
        HBox hBox = new HBox();
        hBox.getChildren().addAll(textField, buttonBox);
        this.getChildren().add(hBox);
        hBox.setAlignment(Pos.CENTER);
    }

    /**
     * Bindet Ereignisse des Textfeldes an, setzt Layout-Optionen und initialisiert den angezeigten Wert.
     */
    private void initTextField() { // NOCS

        textField.focusedProperty().addListener(new ChangeListener<Boolean>() {
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean wasFocused, Boolean isFocused) {
                if (textField.isEditable() && isFocused) {
                    // Beim Reinklicken ins Textfeld den gesamten Inhalt markieren.
                    Platform.runLater(new Runnable() {
                        @Override
                        public void run() {
                            textField.selectAll();
                        }
                    });
                } else if (!isFocused) {
                    // Beim Verlassen des Felds den Wert validieren. Das muss im richtigen Kontext durchgeführt werden,
                    // weil der Fokus dadurch verloren gehen kann, dass das gesamte Fenster defokussiert wurde, dann
                    // wäre der Kontext nicht aktiv.
                    parseText();
                    displayCurrentValue();
                }
                // Den aktuellen Fokus-Zustand an die äußere Komponente weiterleiten.
                numberSpinner.setFocusedInternal(isFocused);
            }
        });

        // Pfeil-Hoch und Pfeil-Runter abfangen, um den Wert entsprechend zu verändern.
        textField.addEventFilter(KeyEvent.KEY_PRESSED, new EventHandler<KeyEvent>() {
            public void handle(KeyEvent keyEvent) {
                if (!keyEvent.isConsumed()) {
                    if (keyEvent.getCode() == KeyCode.UP) {
                        btnIncrement.fire();
                        keyEvent.consume();
                    } else if (keyEvent.getCode() == KeyCode.DOWN) {
                        btnDecrement.fire();
                        keyEvent.consume();
                    } else if (keyEvent.getCode() == KeyCode.ENTER) {
                        parseText();
                        displayCurrentValue();
                        textField.selectAll();
                        keyEvent.consume();
                    }
                }
            }
        });

        /*
         * Da für die SelectionRange und CaretPosition keine bidirektionale Bindung möglich ist, werden auf beiden Seiten Listener
         * installiert, die alle Änderungen an die andere Seite weiterleiten. Ziel ist, dass diese Eigenschaften bei der äußeren Komponente
         * (NumberSpinner) und dem inneren Textfeld synchron gehalten werden.
         */
        changeListenerSelection = new ChangeListener<IndexRange>() {
            public void changed(ObservableValue<? extends IndexRange> observableValue, IndexRange indexRange, IndexRange indexRange2) {
                textField.selectRange(indexRange2.getStart(), indexRange2.getEnd());
            }
        };
        numberSpinner.selectionProperty().addListener(changeListenerSelection);
        textField.selectionProperty().addListener(new ChangeListener<IndexRange>() {
            public void changed(ObservableValue<? extends IndexRange> observableValue, IndexRange indexRange, IndexRange indexRange1) {
                numberSpinner.selectRange(indexRange1.getStart(), indexRange1.getEnd());
            }
        });
        changeListenerCaretPosition = new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number1) {
                textField.positionCaret(number1.intValue());
            }
        };
        numberSpinner.caretPositionProperty().addListener(changeListenerCaretPosition);
        textField.caretPositionProperty().addListener(new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number1) {
                numberSpinner.positionCaret(number1.intValue());
            }
        });

        // Alle übrigen Eigenschaften können ganz normal angebunden werden.
        textField.minHeightProperty().bind(numberSpinner.minHeightProperty());
        textField.maxHeightProperty().bind(numberSpinner.maxHeightProperty());
        textField.textProperty().bindBidirectional(numberSpinner.textProperty());
        textField.alignmentProperty().bind(numberSpinner.alignmentProperty());
        textField.editableProperty().bind(numberSpinner.editableProperty());
        textField.prefColumnCountProperty().bind(numberSpinner.prefColumnCountProperty());
        textField.promptTextProperty().bind(numberSpinner.promptTextProperty());
        textField.onActionProperty().bind(numberSpinner.onActionProperty());

        // Layout-Optionen
        HBox.setHgrow(textField, Priority.ALWAYS);
        textField.getStyleClass().add("spinner-field");

        // Den jeweils aktuellen Wert im Textfeld anzeigen.
        displayCurrentValue();
        changeListenerValue = new ChangeListener<Number>() {
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number number2) {
                displayCurrentValue();
            }
        };
        numberSpinner.valueProperty().addListener(changeListenerValue);

    }

    /**
     * Bindet Ereignisse der beiden Buttons an und setzt Layout-Optionen.
     */
    private void initButtons() {

        // CSS-Klassen
        btnIncrement.getStyleClass().add("btn-increment");
        btnDecrement.getStyleClass().add("btn-decrement");

        // Icons hinzufügen
        Region arrowIncrement = new Region();
        arrowIncrement.setMaxSize(MAX_ARROW_SIZE, MAX_ARROW_SIZE);
        arrowIncrement.getStyleClass().add("arrow-up");
        btnIncrement.setGraphic(arrowIncrement);

        Region arrowDecrement = new Region();
        arrowDecrement.setMaxSize(MAX_ARROW_SIZE, MAX_ARROW_SIZE);
        arrowDecrement.getStyleClass().add("arrow-down");
        btnDecrement.setGraphic(arrowDecrement);

        // Verhindern, dass die Buttons den Fokus bekommen
        btnIncrement.setFocusTraversable(false);
        btnDecrement.setFocusTraversable(false);

        // Die Buttons disablen, wenn die Grenze erreicht ist
        btnIncrement.disableProperty().bind(new BooleanBinding() {
            {
                super.bind(numberSpinner.valueProperty(), numberSpinner.maxValueProperty());
            }

            protected boolean computeValue() {
                return numberSpinner.valueProperty().get() != null && numberSpinner.maxValueProperty().get() != null && numberSpinner.valueProperty().get().doubleValue() >= numberSpinner.maxValueProperty().get().doubleValue();
            }
        });
        btnDecrement.disableProperty().bind(new BooleanBinding() {
            {
                super.bind(numberSpinner.valueProperty(), numberSpinner.minValueProperty());
            }

            protected boolean computeValue() {
                return numberSpinner.valueProperty().get() != null && numberSpinner.minValueProperty().get() != null && numberSpinner.valueProperty().get().doubleValue() <= numberSpinner.minValueProperty().get().doubleValue();
            }
        });

        // Beim Klick der Buttons den Wert verändern.
        btnIncrement.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                parseText();
                numberSpinner.increment();
            }
        });
        btnDecrement.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                parseText();
                numberSpinner.decrement();
            }
        });

        // Buttons vertikal anordnen und zum Layout hinzufügen
        btnIncrement.setMinHeight(0);
        btnDecrement.setMinHeight(0);
        VBox.setVgrow(btnIncrement, Priority.ALWAYS);
        VBox.setVgrow(btnDecrement, Priority.ALWAYS);
        DoubleExpression halfHeight = textField.heightProperty().divide(2.0).subtract(0.5); // NOCS
        btnIncrement.maxHeightProperty().bind(halfHeight);
        btnDecrement.maxHeightProperty().bind(halfHeight);
        btnIncrement.prefHeightProperty().bind(btnIncrement.maxHeightProperty());
        btnDecrement.prefHeightProperty().bind(btnDecrement.maxHeightProperty());
        buttonBox.getChildren().addAll(btnIncrement, btnDecrement);
        buttonBox.setAlignment(Pos.CENTER);

    }

    /**
     * Liest den Text aus dem Textfeld und versucht, ihn in eine Zahl zu konvertieren. Wenn das gelingt, wird
     * {@linkplain NumberSpinner#valueProperty() value} auf den entsprechenden Wert gesetzt, ansonsten auf null.
     */
    private void parseText() {
        if (textField.getText() != null) {
            try {
                numberSpinner.setValue(BigDecimal.valueOf(numberSpinner.getNumberStringConverter().fromString(textField.getText()).doubleValue()));
            } catch (Exception e) {
                numberSpinner.setValue(0);
            }
        } else {
            numberSpinner.setValue(0);
        }
    }

    /**
     * Zeigt den aktuellen Wert der Komponente im Textfeld an.
     */
    private void displayCurrentValue() {
        if (numberSpinner.getValue() != null && !Double.isInfinite((numberSpinner.getValue().doubleValue())) && !Double.isNaN(numberSpinner.getValue().doubleValue())) {
            textField.setText(numberSpinner.getNumberStringConverter().toString(numberSpinner.getValue()));
        } else {
            textField.setText(null);
        }
    }

    @Override
    public NumberSpinner getSkinnable() {
        return numberSpinner;
    }

    @Override
    public Node getNode() {
        return this;
    }

    @Override
    public void dispose() {

        // Alle Bindungen lösen, um Memory-Leaks zu verhindern.
        minHeightProperty().unbind();

        textField.minHeightProperty().unbind();
        textField.maxHeightProperty().unbind();
        textField.textProperty().unbindBidirectional(numberSpinner.textProperty());
        textField.alignmentProperty().unbind();
        textField.editableProperty().unbind();
        textField.prefColumnCountProperty().unbind();
        textField.promptTextProperty().unbind();
        textField.onActionProperty().unbind();

        numberSpinner.selectionProperty().removeListener(changeListenerSelection);
        numberSpinner.caretPositionProperty().removeListener(changeListenerCaretPosition);
        numberSpinner.valueProperty().removeListener(changeListenerValue);
        btnIncrement.disableProperty().unbind();
        btnDecrement.disableProperty().unbind();

    }
}
