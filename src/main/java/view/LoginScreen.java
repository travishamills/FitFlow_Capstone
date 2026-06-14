/*
 * File: LoginScreen.java
 * Version: 0.5.2
 * Date last edited: 6/14/2026
 * Author: Alex Ronn
 * File Purpose: This class builds the login screen and 
 * 		provides functionality to its buttons.
 */

package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class LoginScreen extends BaseScreen {

    private Scene scene;

    private TextField usernameField;
    private PasswordField passwordField;
    private Label messageLabel;
    
    protected static final Color PRIMARY_BLUE =
            Color.web("#1E5AA8");

    protected static final Color ACCENT_BLUE =
            Color.web("#002254");

    protected static final Color ERROR_RED =
            Color.web("#F44336");

    public LoginScreen(AppStateManager stateManager) {

        super(stateManager);

        buildScreen();
    }

    private void buildScreen() {

        // Main blue background
        StackPane root = createRootLayout();

        // White rounded card
        StackPane card = createCard(450, 600);

        VBox content = createCardContent();

        // Logo Placeholder

        Label logoPlaceholder = new Label("LOGO");

        logoPlaceholder.setMinSize(120, 120);
        logoPlaceholder.setAlignment(Pos.CENTER);

        logoPlaceholder.setStyle("""
            -fx-background-color: lightgray;
            -fx-background-radius: 60;
            -fx-border-radius: 60;
            """);

        // Title

        Label title = new Label("FitFlow");

        title.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        30
                )
        );

        // Subtitle

        Label subtitle =
                new Label("Sign in to continue");

        subtitle.setFont(
                Font.font(
                        "Segoe UI",
                        14
                )
        );

        // Username Field

        usernameField = new TextField();

        usernameField.setPromptText("Username");

        usernameField.setMaxWidth(300);

        // Password Field

        passwordField = new PasswordField();

        passwordField.setPromptText("Password");

        passwordField.setMaxWidth(300);

        // Login Button

        Button loginButton =
                new Button("Login");

        loginButton.setPrefWidth(300);

        loginButton.setStyle("""
            -fx-background-color: #1E5AA8;
            -fx-text-fill: white;
            -fx-background-radius: 10;
            -fx-font-family: "Segoe UI";
            -fx-font-weight: bold;
            """);
        
        // added so I can press enter to login lol
        loginButton.setDefaultButton(true);

        // sends login validation attempt
        loginButton.setOnAction(event -> {

            clearNotification();

            String username =
                    usernameField.getText();

            String password =
                    passwordField.getText();

            stateManager.signInAttempt(username,  password);

        });

        // Message Label

        messageLabel = createNotificationLabel();

        // Register Link

        Label registerLink =
                new Label("Create Account");

        registerLink.setTextFill(PRIMARY_BLUE);

        registerLink.setOnMouseClicked(event -> {

            stateManager.showSignupScreen();

        });

        // Assemble Card

        
        content.getChildren().addAll(
                logoPlaceholder,
                title,
                subtitle,
                usernameField,
                passwordField,
                loginButton,
                messageLabel,
                registerLink
        );
        
        card.getChildren().add(content);

        root.getChildren().add(card);

        scene = new Scene(root, 900, 800);
    }
    
    public void clearPassword() {
    	passwordField.setText("");
    }
    
    public void fillUsername(String user) {
    	usernameField.setText(user);
    }

    public Scene getScene() {
        return scene;
    }
}