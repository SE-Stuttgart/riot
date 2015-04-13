package de.uni_stuttgart.riot.simulation_client;

import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Pair;

;

/**
 * Displays a login dialog.
 * 
 * @author Philipp Keck
 */
public class LoginDialog extends Stage {

    private static final int PADDINGS = 5;
    private static final int WIDTH = 240;
    private static final int HEIGHT = 105;

    private final TextField userNameFld = new TextField();
    private final PasswordField passwordFld = new PasswordField();

    /**
     * Creates the dialog content.
     * 
     * @param owner
     *            The owner window (will be blocked while the dialog is visible).
     */
    public LoginDialog(Stage owner) {
        super();
        initOwner(owner);
        setTitle("title");
        Group root = new Group();
        Scene scene = new Scene(root, WIDTH, HEIGHT, Color.WHITE);
        setScene(scene);
        this.setTitle("Login");

        GridPane gridpane = new GridPane();
        gridpane.setPadding(new Insets(PADDINGS));
        gridpane.setHgap(PADDINGS);
        gridpane.setVgap(PADDINGS);

        Label userNameLbl = new Label("User Name: ");
        gridpane.add(userNameLbl, 0, 1);

        Label passwordLbl = new Label("Password: ");
        gridpane.add(passwordLbl, 0, 2);
        gridpane.add(userNameFld, 1, 1);
        gridpane.add(passwordFld, 1, 2);

        Button login = new Button("Change");
        login.setOnAction((event) -> close());
        gridpane.add(login, 1, 3);
        GridPane.setHalignment(login, HPos.RIGHT);
        root.getChildren().add(gridpane);
    }

    /**
     * Shows the dialog and returns the specified values.
     * 
     * @param owner
     *            The owner window (will be blocked while the dialog is visible).
     * @return The specified values (username is the key, password is the value) or <tt>null</tt> if the dialog was canceled.
     */
    public static Pair<String, String> showDialog(Stage owner) {
        // TODO Das hier muss auf den UI-THread!!
        LoginDialog dialog = new LoginDialog(owner);
        dialog.showAndWait();

        String username = dialog.userNameFld.getText();
        if (username == null || username.trim().isEmpty()) {
            return null;
        }

        String password = dialog.passwordFld.getText();
        if (password == null || password.trim().isEmpty()) {
            return null;
        }

        return new Pair<String, String>(username.trim(), password);
    }

}
