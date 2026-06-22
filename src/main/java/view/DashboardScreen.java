/*
 * File: DashboardScreen.java
 * Project: FitFlow - Interactive Workout Assistant
 * Course: UMGC CMSC 495
 * Week: 6
 * Version: v0.6.3
 * Author: Orange Snaer
 * Adapted and later redesigned by: Alex Ronn
 *
 * File Purpose: Main navigation hub shown after login. Provides styled
 *      buttons to reach each major screen in the application.
 */

package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

public class DashboardScreen extends BaseScreen {

    private Scene scene;

    public DashboardScreen(AppStateManager stateManager) {
        super(stateManager);
        buildScreen();
    }

    private void buildScreen() {

        StackPane root = createRootLayout();

        StackPane card = createCard(380, 620);

        VBox content = createCardContent();
        content.setSpacing(0);

        // Header

        Label logoPlaceholder = new Label("LOGO");
        logoPlaceholder.setMinSize(100, 100);
        logoPlaceholder.setMaxSize(100, 100);
        logoPlaceholder.setAlignment(Pos.CENTER);
        logoPlaceholder.setStyle("""
            -fx-background-color: lightgray;
            -fx-background-radius: 50;
            -fx-border-radius: 50;
            -fx-alignment: center;
            """);

        Label appName = new Label("FitFlow");
        appName.setFont(Font.font("Segoe UI", FontWeight.BOLD, 30));
        appName.setTextFill(Color.web("#1E5AA8"));

        Label appSubtitle = new Label("Interactive Workout Assistant");
        appSubtitle.setFont(Font.font("Segoe UI", 13));
        appSubtitle.setTextFill(Color.web("#666666"));

        VBox headerBlock = new VBox(8, logoPlaceholder, appName, appSubtitle);
        headerBlock.setAlignment(Pos.CENTER);
        headerBlock.setPadding(new Insets(10, 0, 28, 0));

        // Divider label 

        Label sectionLabel = new Label("NAVIGATE TO");
        sectionLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        sectionLabel.setTextFill(Color.web("#AAAAAA"));
        sectionLabel.setPadding(new Insets(0, 0, 8, 4));

        // Navigation buttons 

        Button profileButton      = createNavButton("Profile");
        Button builderButton      = createNavButton("Workout Builder");
        Button historyButton      = createNavButton("Workout History");
        Button timerButton        = createNavButton("Interval Timer");

        profileButton.setOnAction(event ->
                stateManager.showProfileScreen());

        builderButton.setOnAction(event ->
                stateManager.showRoutineBuilderScreen());

        historyButton.setOnAction(event ->
                stateManager.showWorkoutHistoryScreen());

        /*
        timerButton.setOnAction(event ->
                stateManager.showIntervalTimerScreen());
        */

        VBox navButtons = new VBox(10,
                profileButton,
                builderButton,
                historyButton,
                timerButton
        );
        navButtons.setPadding(new Insets(0, 0, 20, 0));

        // Separator + logout

        Separator separator = new Separator();
        separator.setPadding(new Insets(8, 0, 8, 0));

        Button logoutButton = createNavButton("Log Out");
        logoutButton.setStyle(logoutButton.getStyle() +
                "-fx-background-color: #B00020;");

        logoutButton.setOnAction(event ->
                stateManager.logOut());

        // Assemble 

        content.getChildren().addAll(
                headerBlock,
                sectionLabel,
                navButtons,
                separator,
                logoutButton
        );

        card.getChildren().add(content);
        root.getChildren().add(card);

        scene = new Scene(root, 1200, 800);
    }

    private Button createNavButton(String label) {

        Button button = new Button(label);

        button.setMaxWidth(Double.MAX_VALUE);
        button.setPrefHeight(46);

        button.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));

        button.setStyle("""
            -fx-background-color: #1E5AA8;
            -fx-text-fill: white;
            -fx-background-radius: 10;
            -fx-font-family: "Segoe UI";
            -fx-font-size: 14;
            -fx-font-weight: bold;
            -fx-padding: 10 20 10 20;
            -fx-cursor: hand;
            -fx-alignment: CENTER_LEFT;
            """);

        button.setOnMouseEntered(e -> button.setStyle(button.getStyle()
                .replace("#1E5AA8", "#2668C2")));

        button.setOnMouseExited(e -> button.setStyle(button.getStyle()
                .replace("#2668C2", "#1E5AA8")));

        return button;
    }

    public Scene getScene() {
        return scene;
    }
}