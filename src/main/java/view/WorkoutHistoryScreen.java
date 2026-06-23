/*
 * File: WorkoutHistoryScreen.java
 * Version: 0.6.3
 * Date last edited: 6/21/2026
 * Author: Alex Ronn
 * File Purpose: Creates the page to display a history
 * 		of workouts the user has completed.
 */
package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import java.util.List;

public class WorkoutHistoryScreen extends BaseScreen {

    private Scene scene;

    public WorkoutHistoryScreen(AppStateManager stateManager) {
        super(stateManager);
        buildScreen();
    }

    private void buildScreen() {

        StackPane root = createRootLayout();

        StackPane card = createCard(920, 650);

        VBox content = createCardContent();

        // Header

        Label logoPlaceholder = new Label("LOGO");
        logoPlaceholder.setMinSize(80, 80);
        logoPlaceholder.setAlignment(Pos.CENTER);
        logoPlaceholder.setStyle("""
            -fx-background-color: lightgray;
            -fx-background-radius: 40;
            -fx-border-radius: 40;
            -fx-alignment: center;
            """);

        Label title = new Label("FitFlow");
        title.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));

        Label subtitle = new Label("Workout History");
        subtitle.setFont(Font.font("Segoe UI", 14));

        VBox header = new VBox(5, logoPlaceholder, title, subtitle);
        header.setAlignment(Pos.CENTER);

        // History list

        VBox historyList = new VBox(10);
        historyList.setPadding(new Insets(10));

        List<String> entries = stateManager.getWorkoutHistory();

        if (entries == null || entries.isEmpty()) {

            Label emptyLabel = new Label("No workout history found.");
            emptyLabel.setFont(Font.font("Segoe UI", 14));
            emptyLabel.setTextFill(Color.GRAY);
            historyList.getChildren().add(emptyLabel);
            historyList.setAlignment(Pos.CENTER);

        } else {

            for (String entry : entries) {
                historyList.getChildren().add(buildHistoryRow(entry));
            }
        }

        ScrollPane scrollPane = new ScrollPane(historyList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(450);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Assemble 

        content.getChildren().addAll(header, scrollPane);

        card.getChildren().add(content);
        root.getChildren().add(card);
        addNavigationMenu(root);

        scene = new Scene(root, 1250, 800);
    }

    /*
     * Builds one row card for a single history entry string.
     *
     * FitFlowFacade formats history entries as:
     *   completedDate | routineName | duration seconds | calories calories
     * This method splits on " | " and lays the parts out in a labeled row
     * so the screen shows structured data rather than a raw string.
     */
    private HBox buildHistoryRow(String entry) {

        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 20, 14, 20));

        row.setBackground(new Background(new BackgroundFill(
                Color.web("#1E5AA8"),
                new CornerRadii(10),
                Insets.EMPTY
        )));

        String[] parts = entry.split(" \\| ");

        // If the string format ever changes, show it raw
        if (parts.length < 4) {
            Label raw = new Label(entry);
            raw.setFont(Font.font("Segoe UI", 13));
            row.getChildren().add(raw);
            return row;
        }

        String date     = parts[0].trim();
        String routine  = parts[1].trim();
        String duration = parts[2].trim();
        String calories = parts[3].trim();

        row.getChildren().addAll(
                buildField("Date",     date,     220),
                buildField("Routine",  routine,  220),
                buildField("Duration", duration, 160),
                buildField("Calories", calories, 100)
        );

        return row;
    }

    /*
     * Creates a small labeled value block used inside each history row.
     */
    private VBox buildField(String labelText, String valueText, double width) {

        Label label = new Label(labelText);
        label.setFont(Font.font("Segoe UI", FontWeight.BOLD, 11));
        label.setTextFill(Color.web("#BBBBBB"));

        Label value = new Label(valueText);
        value.setFont(Font.font("Segoe UI", 13));
        value.setTextFill(Color.web("#FFFFFF"));
        value.setWrapText(true);
        value.setMaxWidth(width);

        VBox box = new VBox(2, label, value);
        box.setPrefWidth(width);
        box.setMinWidth(width);

        return box;
    }

    public Scene getScene() {
        return scene;
    }
}