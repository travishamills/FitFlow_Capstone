/*
 * File: ProfileScreen.java
 * Version: 0.6.1
 * Date last edited: 6/20/2026
 * Author: Alex Ronn
 * Modified by: David Lewis
 * File Purpose: This class builds the profile screen
 * 		and allows the user to edit values.
 * Update Notes: Connected the Save button to the AppStateManager profile-save
 * bridge and added clear status feedback so profile updates no longer use the
 * placeholder save path.
 */

package view;

import java.text.DecimalFormat;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.UserProfile;
import service.ServiceResponse;
import util.Calculator;

public class ProfileScreen extends BaseScreen {

    private Scene scene;
    
    private UserProfile profile;

    private TextField firstNameField;
    private TextField lastNameField;
    private TextField ageField;
    private TextField weightField;
    private TextField heightField;

    private RadioButton maleButton;
    private RadioButton femaleButton;
    private RadioButton otherButton;

    private Label bmiLabel;
    private Label saveStatusLabel;

    public ProfileScreen(
            AppStateManager stateManager,
            UserProfile profile) {

        super(stateManager);

        this.profile = profile;

        buildScreen();

        loadProfileData();
    }

    private void buildScreen() {

        StackPane root = createRootLayout();
        
        StackPane card = createCard(750, 650);

        VBox content = createCardContent();

        card.setAlignment(Pos.TOP_CENTER);

        // Header

        Label logoPlaceholder = new Label("LOGO");

        logoPlaceholder.setMinSize(100, 100);

        logoPlaceholder.setStyle("""
            -fx-background-color: lightgray;
            -fx-background-radius: 50;
            -fx-alignment: center;
            """);

        Label title = new Label("FitFlow");

        title.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        28
                )
        );

        Label subtitle =
                new Label("Interactive Workout Assistant");

        VBox headerBox = new VBox(
                5,
                logoPlaceholder,
                title,
                subtitle
        );

        headerBox.setAlignment(Pos.CENTER);

        // Main Content

        GridPane grid = new GridPane();

        grid.setHgap(40);
        grid.setVgap(12);

        grid.setPadding(new Insets(20));

        // Profile Picture

        Label profilePicture =
                new Label("Profile\nPicture");

        profilePicture.setMinSize(100, 100);

        profilePicture.setStyle("""
            -fx-border-color: black;
            -fx-alignment: center;
            """);

        // Labels / Fields

        firstNameField = new TextField();
        lastNameField = new TextField();
        ageField = new TextField();
        weightField = new TextField();
        heightField = new TextField();

        bmiLabel = new Label("---");

        grid.add(profilePicture, 0, 0, 1, 2);

        grid.add(new Label("First Name:"), 0, 2);
        grid.add(firstNameField, 0, 3);

        grid.add(new Label("Last Name:"), 0, 4);
        grid.add(lastNameField, 0, 5);

        grid.add(new Label("Age:"), 0, 6);
        grid.add(ageField, 0, 7);

        grid.add(new Label("Current Weight (lbs):"), 0, 8);
        grid.add(weightField, 0, 9);

        grid.add(new Label("Height (inches):"), 0, 10);
        grid.add(heightField, 0, 11);

        // BMI Section

        Label bmiTitle = new Label("BMI:");

        bmiTitle.setFont(
                Font.font(
                        "Segoe UI",
                        FontWeight.BOLD,
                        14
                )
        );

        VBox bmiBox = new VBox(
                5,
                bmiTitle,
                bmiLabel
        );

        grid.add(bmiBox, 1, 3);

        // Gender

        Label genderLabel =
                new Label("Gender:");

        ToggleGroup genderGroup =
                new ToggleGroup();

        maleButton = new RadioButton("Male");
        femaleButton = new RadioButton("Female");
        otherButton = new RadioButton("Other");

        maleButton.setToggleGroup(genderGroup);
        femaleButton.setToggleGroup(genderGroup);
        otherButton.setToggleGroup(genderGroup);

        HBox genderBox = new HBox(
                10,
                maleButton,
                femaleButton,
                otherButton
        );

        grid.add(genderLabel, 0, 12);
        grid.add(genderBox, 0, 13);

        // Save status label gives the user feedback after AppStateManager
        // returns the real ServiceResponse from the service layer.
        saveStatusLabel = new Label("");
        saveStatusLabel.setWrapText(true);
        grid.add(saveStatusLabel, 0, 14);

        // Buttons

        Button saveButton =
                new Button("Save");

        saveButton.setStyle("""
            -fx-background-color: #1E5AA8;
            -fx-text-fill: white;
            -fx-background-radius: 10;
            -fx-font-weight: bold;
            """);
        
        saveButton.setOnAction(event -> {
            saveProfileChanges();
        });

        Button logoutButton =
                new Button("Log Out");

        logoutButton.setStyle("""
            -fx-background-color: #1E5AA8;
            -fx-text-fill: white;
            -fx-background-radius: 10;
            -fx-font-weight: bold;
            """);

        logoutButton.setOnAction(event -> {

        	stateManager.logOut();

        });

        HBox buttonBox = new HBox(
                15,
                saveButton,
                logoutButton
        );

        buttonBox.setAlignment(Pos.CENTER);

        // Assemble
        
        HBox topSection = new HBox(100);

        topSection.setAlignment(Pos.TOP_LEFT);
        topSection.setPadding(new Insets(5, 40, 5, 40));
        
        VBox profileSection = new VBox(10);

        Label profileTitle = new Label("Your Profile:");

        profileSection.getChildren().addAll(
                profileTitle,
                profilePicture
        );
        
        VBox logoSection = new VBox(5);

        logoSection.setAlignment(Pos.TOP_CENTER);

        logoSection.getChildren().addAll(
                logoPlaceholder,
                title,
                subtitle
        );
        
        topSection.getChildren().addAll(
                profileSection,
                logoSection
        );
        
        content.getChildren().addAll(
                topSection,
                grid,
                buttonBox
        );
        
        card.getChildren().add(content);

        root.getChildren().add(card);
        addNavigationMenu(root);

        scene = new Scene(root, 1200, 800);
    }

    /*
     * Saves the values currently entered on the profile screen.
     *
     * Builds a UserProfile object from the visible form fields.
     * The screen needs to send real profile data to AppStateManager
     * instead of printing the old placeholder message.
     * This method parses number fields safely, sends the profile through
     * stateManager.saveProfile, updates the local profile only on success, and
     * displays the ServiceResponse message for the user.
     */
    private void saveProfileChanges() {
        try {
            UserProfile dataToSave = new UserProfile(
                    profile.getUserId(),
                    profile.getUsername(),
                    firstNameField.getText(),
                    lastNameField.getText(),
                    Integer.parseInt(ageField.getText()),
                    Double.parseDouble(heightField.getText()),
                    Double.parseDouble(weightField.getText()),
                    getSelectedGender());

            ServiceResponse<Boolean> attempt =
                    stateManager.saveProfile(dataToSave);

            if (attempt.isSuccess()) {
                profile = dataToSave;
                updateBMILabel();
                showSaveSuccess(attempt.getMessage());
            } else {
                showSaveError(attempt.getMessage());
            }
        } catch (NumberFormatException exception) {
            showSaveError("Age, height, and weight must be valid numbers.");
        }
    }

    // Shows a success message below the form controls.
    private void showSaveSuccess(String message) {
        saveStatusLabel.setText(message);
        saveStatusLabel.setStyle("-fx-text-fill: #1E5AA8; -fx-font-weight: bold;");
    }

    // Shows an error message below the form controls.
    private void showSaveError(String message) {
        saveStatusLabel.setText(message);
        saveStatusLabel.setStyle("-fx-text-fill: #B00020; -fx-font-weight: bold;");
    }

    private void loadProfileData() {

        if (profile == null) {
            return;
        }

        firstNameField.setText(
                profile.getFirstName()
        );

        lastNameField.setText(
                profile.getLastName()
        );

        ageField.setText(
                String.valueOf(
                        profile.getAge()
                )
        );

        heightField.setText(
                String.valueOf(
                        profile.getHeight()
                )
        );

        weightField.setText(
                String.valueOf(
                        profile.getWeight()
                )
        );

        switch (profile.getGender()) {

            case "Male":
                maleButton.setSelected(true);
                break;

            case "Female":
                femaleButton.setSelected(true);
                break;

            default:
                otherButton.setSelected(true);
        }

        updateBMILabel();
    }
    
    private String getSelectedGender() {
    	if (maleButton.isSelected())
    		return "Male";
    	else if (femaleButton.isSelected())
    		return "Female";
    	return "Other";
    }
    
    public void updateBMILabel() {
    	DecimalFormat df = new DecimalFormat("#.##");
    	
    	double weight = Double.parseDouble(weightField.getText());
    	double height = Double.parseDouble(heightField.getText());
    	
    	double bmi = Calculator.calculateBMI(weight, height);
    	String formatted = df.format(bmi);
    	bmiLabel.setText(formatted);
    }
    
    public Scene getScene() {
        return scene;
    }
}
