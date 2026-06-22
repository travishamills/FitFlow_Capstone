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
    private Label passwordValidationLabel;
    private Label confirmPasswordValidationLabel;

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
        passwordField.textProperty().addListener((obs, oldValue, newValue) -> validatePasswordLive());
        confirmPasswordField.textProperty().addListener((obs, oldValue, newValue) -> validatePasswordLive());

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
        
        // matching displayed password requirements to validation util
        // can change later if needed
        Label passwordRequirements =
                new Label(
                        """
                        Password Requirements:
                        • At least 12 characters
                        • At least 1 number
                        """
                );

        passwordRequirements.setWrapText(true);
        passwordRequirements.setMaxWidth(300);
        passwordValidationLabel = new Label();
        confirmPasswordValidationLabel = new Label();

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
            
            // temporary test email, unsure if we are keeping it
            final String TEST_EMAIL = "test@test.com";
            
            if (!password.equals("") && password.equals(confirmPassword)) {
            	stateManager.signUpAttempt(username, password, TEST_EMAIL);
            } else {
            	showError("Passwords do not match.");
            }

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
                passwordValidationLabel,
                confirmPasswordValidationLabel,
                createAccountButton,
                messageLabel,
                loginLink
        );
        
        card.getChildren().addAll(content);

        root.getChildren().add(card);

        scene = new Scene(root, 900, 800);
    }

    private void validatePasswordLive() {
        // Gives live feedback while the user types their password.
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        boolean validPassword =
                password.length() >= 12 && password.matches(".*\\d.*");

        if (password.isEmpty()) {
            passwordValidationLabel.setText("");
        } else if (validPassword) {
            passwordValidationLabel.setText("Password meets requirements.");
            passwordValidationLabel.setTextFill(Color.GREEN);
        } else {
            passwordValidationLabel.setText("Password must be 12+ characters and include 1 number.");
            passwordValidationLabel.setTextFill(Color.RED);
        }

        if (confirmPassword.isEmpty()) {
            confirmPasswordValidationLabel.setText("");
        } else if (confirmPassword.equals(password)) {
            confirmPasswordValidationLabel.setText("Passwords match.");
            confirmPasswordValidationLabel.setTextFill(Color.GREEN);
        } else {
            confirmPasswordValidationLabel.setText("Passwords do not match.");
            confirmPasswordValidationLabel.setTextFill(Color.RED);
        }
    }

    public void clearUsername() {
    	usernameField.setText("");
    }

    public void clearPasswords() {
        // Clears signup form fields when leaving and returning to the page.
        usernameField.setText("");
        passwordField.setText("");
        confirmPasswordField.setText("");
        passwordValidationLabel.setText("");
        confirmPasswordValidationLabel.setText("");
        clearNotification();
    }

    public Scene getScene() {
        return scene;
    }

}