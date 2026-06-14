/*
 * File: SignupScreen.java
 * Version: 0.5.2
 * Date last edited: 6/14/2026
 * Original Author: Orange Snaer
 * Adapted by: Alex Ronn
 * File Purpose: Launches the application, starting with the login screen.
 */

package view;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class SignupScreen extends BaseScreen {

    private Scene scene;

    private TextField usernameField;

    private PasswordField passwordField;
    private PasswordField confirmPasswordField;

    private TextField passwordVisibleField;
    private TextField confirmPasswordVisibleField;

    public SignupScreen(AppStateManager stateManager) {

        super(stateManager);

        buildScreen();
    }

    private void buildScreen() {

        StackPane root = createRootLayout();

        StackPane card = createCard(450, 700);

        VBox content = createCardContent();

        // Logo Placeholder

        Label logoPlaceholder = new Label("LOGO");

        logoPlaceholder.setMinSize(100, 100);
        logoPlaceholder.setAlignment(Pos.CENTER);

        logoPlaceholder.setStyle("""
            -fx-background-color: lightgray;
            -fx-background-radius: 50;
            -fx-border-radius: 50;
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

        Label subtitle = new Label("Create Your Account");

        subtitle.setFont(
                Font.font(
                        "Segoe UI",
                        14
                )
        );

        // Username

        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);

        // Password Fields

        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);

        passwordVisibleField = new TextField();
        passwordVisibleField.setPromptText("Password");
        passwordVisibleField.setMaxWidth(300);
        passwordVisibleField.setVisible(false);
        passwordVisibleField.setManaged(false);

        // Keep text synchronized

        passwordVisibleField.textProperty().bindBidirectional(
                passwordField.textProperty()
        );

        // Confirm Password

        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(300);

        confirmPasswordVisibleField = new TextField();
        confirmPasswordVisibleField.setPromptText("Confirm Password");
        confirmPasswordVisibleField.setMaxWidth(300);
        confirmPasswordVisibleField.setVisible(false);
        confirmPasswordVisibleField.setManaged(false);

        confirmPasswordVisibleField.textProperty().bindBidirectional(
                confirmPasswordField.textProperty()
        );

        // Show Password Checkbox

        CheckBox showPasswordCheckBox =
                new CheckBox("Show Password");

        showPasswordCheckBox.setOnAction(event -> {

            boolean showPasswords =
                    showPasswordCheckBox.isSelected();

            passwordField.setVisible(!showPasswords);
            passwordField.setManaged(!showPasswords);

            confirmPasswordField.setVisible(!showPasswords);
            confirmPasswordField.setManaged(!showPasswords);

            passwordVisibleField.setVisible(showPasswords);
            passwordVisibleField.setManaged(showPasswords);

            confirmPasswordVisibleField.setVisible(showPasswords);
            confirmPasswordVisibleField.setManaged(showPasswords);
        });

        // Password Requirements

        Label passwordRequirements =
                new Label(
                        """
                        Password Requirements:
                        • At least 8 characters
                        • One uppercase letter
                        • One lowercase letter
                        • One number
                        """
                );

        passwordRequirements.setWrapText(true);
        passwordRequirements.setMaxWidth(300);

        // Create Account Button

        Button createAccountButton =
                new Button("Create Account");

        createAccountButton.setPrefWidth(300);

        createAccountButton.setStyle("""
            -fx-background-color: #1E5AA8;
            -fx-text-fill: white;
            -fx-background-radius: 10;
            -fx-font-family: "Segoe UI";
            -fx-font-weight: bold;
            """);
        
        createAccountButton.setDefaultButton(true);

        createAccountButton.setOnAction(event -> {

            clearNotification();

            String username =
                    usernameField.getText().trim();

            String password =
                    passwordField.getText();

            String confirmPassword =
                    confirmPasswordField.getText();

            if (username.isBlank()) {

                showError(
                        "Please enter a username."
                );

                return;
            }

            if (password.isBlank()) {

                showError(
                        "Please enter a password."
                );

                return;
            }

            if (!isValidPassword(password)) {

                showError(
                		getPasswordValidationMessage(password)
                );

                return;
            }

            if (!password.equals(confirmPassword)) {

                showError(
                        "Passwords do not match."
                );

                return;
            }

            showSuccess(
                    "Account validation passed."
            );

            // TODO:
            // actually create an account

        });
        // Status Label

        messageLabel = createNotificationLabel();

        // Back To Login Link

        Label loginLink =
                new Label("Already have an account? Log In");

        loginLink.setTextFill(
                Color.web("#1E5AA8")
        );

        loginLink.setOnMouseClicked(event -> {

            stateManager.showLoginScreen();

        });

        // Assemble Card

        content.getChildren().addAll(
                logoPlaceholder,
                title,
                subtitle,
                usernameField,
                passwordField,
                passwordVisibleField,
                confirmPasswordField,
                confirmPasswordVisibleField,
                showPasswordCheckBox,
                passwordRequirements,
                createAccountButton,
                messageLabel,
                loginLink
        );
        
        card.getChildren().addAll(content);

        root.getChildren().add(card);

        scene = new Scene(root, 900, 800);
    }

    public Scene getScene() {
        return scene;
    }

    // used to get true/false for valid password
    private boolean isValidPassword(String password) {

        return (getPasswordValidationMessage(password) == "");
    }
    
    
    private String getPasswordValidationMessage(
            String password) {

        if (password.length() < 8) {
            return "Password must be at least 8 characters.";
        }

        boolean hasUpper = false;
        boolean hasLower = false;
        boolean hasNumber = false;

        for (char c : password.toCharArray()) {

            if (Character.isUpperCase(c)) {
                hasUpper = true;
            }

            if (Character.isLowerCase(c)) {
                hasLower = true;
            }

            if (Character.isDigit(c)) {
                hasNumber = true;
            }
        }

        if (!hasUpper) {
            return "Password must contain an uppercase letter.";
        }

        if (!hasLower) {
            return "Password must contain a lowercase letter.";
        }

        if (!hasNumber) {
            return "Password must contain a number.";
        }

        return "";
    }
}