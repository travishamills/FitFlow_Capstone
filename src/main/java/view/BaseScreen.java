/*
 * File: BaseScreen.java
 * Version: 0.5.2
 * Date last edited: 6/14/2026
 * Author: Alex Ronn
 * File Purpose: Abstract class that provides methods to set up the consistent interfaces.
 */

package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;

public abstract class BaseScreen {

    protected AppStateManager stateManager;
    protected VBox navigationMenu;
    protected Label messageLabel;

    public BaseScreen(AppStateManager stateManager) {
        this.stateManager = stateManager;
    }
    
    protected StackPane createCard(int width, int height) {

        StackPane card = new StackPane();

        card.setPrefWidth(width);
        card.setMaxWidth(width);

        card.setPrefHeight(height);
        card.setMaxHeight(height);

        card.setBackground(
                new Background(
                        new BackgroundFill(
                                Color.WHITE,
                                new CornerRadii(15),
                                Insets.EMPTY
                        )
                )
        );

        return card;
    }
    
    protected VBox createCardContent() {

        VBox content = new VBox();

        content.setAlignment(Pos.CENTER);

        content.setSpacing(15);

        content.setPadding(
                new Insets(30)
        );

        return content;
    }
    
    protected StackPane createRootLayout() {

        StackPane root = new StackPane();

        root.setAlignment(Pos.CENTER);

        root.setBackground(
                new Background(
                        new BackgroundFill(
                                Color.web("#1E5AA8"),
                                CornerRadii.EMPTY,
                                Insets.EMPTY
                        )
                )
        );

        return root;
    }
    
    protected void addNavigationMenu(StackPane card) {

        navigationMenu = createNavigationMenu();

        Button hamburgerButton =
                createHamburgerButton();

        hamburgerButton.setOnAction(event -> {

            boolean isVisible =
                    navigationMenu.isVisible();

            navigationMenu.setVisible(!isVisible);

        });

        card.getChildren().addAll(
                navigationMenu,
                hamburgerButton
        );

        StackPane.setAlignment(
                hamburgerButton,
                Pos.TOP_RIGHT
        );
        

        StackPane.setMargin(
                hamburgerButton,
                new Insets(15)
        );

        StackPane.setAlignment(
                navigationMenu,
                Pos.TOP_RIGHT
        );

        StackPane.setMargin(
                navigationMenu,
                new Insets(60, 15, 15, 15)
        );
    }
    
    protected Button createHamburgerButton() {

        Rectangle line1 = new Rectangle(18, 2);
        Rectangle line2 = new Rectangle(18, 2);
        Rectangle line3 = new Rectangle(18, 2);

        line1.setFill(Color.WHITE);
        line2.setFill(Color.WHITE);
        line3.setFill(Color.WHITE);

        VBox icon = new VBox(4,
                line1,
                line2,
                line3);

        icon.setAlignment(Pos.CENTER);

        Button button = new Button();

        button.setGraphic(icon);

        button.setPrefSize(40, 40);

        button.setStyle("""
            -fx-background-color: #002254;
            -fx-background-radius: 10;
            -fx-cursor: hand;
            """);

        return button;
    }
    
    
    protected VBox createNavigationMenu() {

        VBox menu = new VBox(10);

        menu.setPadding(
                new Insets(15)
        );

        menu.setPrefWidth(220);
        menu.setMaxWidth(220);
        
        menu.setPrefHeight(400);
        menu.setMaxHeight(400);

        menu.setAlignment(
                Pos.TOP_CENTER
        );
        

        menu.setStyle("""
        	    -fx-background-color: white;
        	    -fx-background-radius: 15;
        	    -fx-border-color: #D9D9D9;
        	    -fx-border-radius: 15;
        	    -fx-border-width: 1;
        	    """);

        Button dashboardButton =
                new Button("Dashboard");
        
        Button profileButton =
                new Button("Profile");

        Button builderButton =
                new Button("Workout Builder");

        Button historyButton =
                new Button("Workout History");

        Button timerButton =
                new Button("Interval Timer");

        Button logoutButton =
                new Button("Log Out");
        
        // Style buttons

        styleNavigationButton(dashboardButton);
        styleNavigationButton(profileButton);
        styleNavigationButton(builderButton);
        styleNavigationButton(historyButton);
        styleNavigationButton(timerButton);
        styleNavigationButton(logoutButton);
        
        // Bind button actions, some disabled until implemented

        profileButton.setOnAction(event ->
                stateManager.showProfileScreen());
        
        dashboardButton.setOnAction(event ->
        		stateManager.showDashboardScreen());

        builderButton.setOnAction(event ->
                stateManager.showRoutineBuilderScreen());
        
        /*
        historyButton.setOnAction(event ->
                stateManager.showWorkoutHistoryScreen());
		*/
        
        /*
        timerButton.setOnAction(event ->
                stateManager.showIntervalTimerScreen());
        */

        logoutButton.setOnAction(event ->
                stateManager.logOut()
        );
        
        // Assemble

        menu.getChildren().addAll(
        		dashboardButton,
                profileButton,
                builderButton,
                historyButton,
                timerButton,
                new Separator(),
                logoutButton
        );

        menu.setVisible(false);

        return menu;
    }
    
    /*
     * Provides the style details for each button.
     */
    private void styleNavigationButton(Button button) {

        button.setMaxWidth(Double.MAX_VALUE);

        button.setStyle("""
            -fx-background-color: #1E5AA8;
            -fx-text-fill: white;
            -fx-font-family: "Segoe UI";
            -fx-font-size: 14;
            -fx-font-weight: bold;
            -fx-background-radius: 10;
            -fx-padding: 8 15 8 15;
            -fx-cursor: hand;
            """);
    }
    
    // Creates a notification label
    protected Label createNotificationLabel() {

        Label label = new Label();

        label.setVisible(false);

        label.setWrapText(true);

        label.setStyle("""
            -fx-font-family: "Segoe UI";
            -fx-font-size: 13;
            -fx-font-weight: bold;
            """);

        messageLabel = label;
        return label;
    }
    
    protected void showError(String message) {

        if (messageLabel == null) {
            return;
        }

        messageLabel.setText(message);

        messageLabel.setStyle("""
            -fx-text-fill: #F44336;
            -fx-font-family: "Segoe UI";
            -fx-font-size: 13;
            -fx-font-weight: bold;
            """);

        messageLabel.setVisible(true);
    }
    
    protected void showSuccess(String message) {

        if (messageLabel == null) {
            return;
        }

        messageLabel.setText(message);

        messageLabel.setStyle("""
            -fx-text-fill: #4CAF50;
            -fx-font-family: "Segoe UI";
            -fx-font-size: 13;
            -fx-font-weight: bold;
            """);

        messageLabel.setVisible(true);
    }
    
    protected void clearNotification() {

        if (messageLabel == null) {
            return;
        }

        messageLabel.setText("");

        messageLabel.setVisible(false);
    }
}