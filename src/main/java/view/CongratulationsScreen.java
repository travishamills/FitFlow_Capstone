/*
 * File: CongratulationsScreen.java
 * Project: FitFlow - Interactive Workout Assistant
 * Version: 6.5
 * Author: Alex Ronn
 * Date: June 28, 2026
 * File Purpose: Displays a post-workout summary after a guided workout
 *      completes. Shows duration, exercise count, and estimated calories,
 *      with buttons to view history or return to the routine builder.
 */

package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import util.Calculator;

public class CongratulationsScreen extends BaseScreen {

    private Scene scene;
    private final int durationSeconds;
    private final int exerciseCount;

    public CongratulationsScreen(AppStateManager stateManager,
                                  int durationSeconds,
                                  int exerciseCount) {
        super(stateManager);
        this.durationSeconds = durationSeconds;
        this.exerciseCount   = exerciseCount;
        buildScreen();
    }

    private void buildScreen() {

        StackPane root = createRootLayout();

        // Central white card
        StackPane card = createCard(820, 560);

        VBox content = new VBox(0);
        content.setAlignment(Pos.TOP_CENTER);

        // Blue banner across the top of the card
        VBox banner = new VBox(12);
        banner.setAlignment(Pos.CENTER);
        banner.setPadding(new Insets(36, 40, 32, 40));
        banner.setBackground(new Background(new BackgroundFill(
                Color.web("#1E5AA8"), new CornerRadii(15, 15, 0, 0, false), Insets.EMPTY)));

        Label congratsLabel = new Label("Congratulations!");
        congratsLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 32));
        congratsLabel.setTextFill(Color.WHITE);

        // Trophy circle
        Label trophyCircle = new Label("🏆");
        trophyCircle.setPrefSize(110, 110);
        trophyCircle.setMinSize(110, 110);
        trophyCircle.setAlignment(Pos.CENTER);
        trophyCircle.setStyle("-fx-font-size: 52;");
        trophyCircle.setBackground(new Background(new BackgroundFill(
                Color.rgb(255, 255, 255, 0.18), new CornerRadii(55), Insets.EMPTY)));

        Label completeLabel = new Label("Workout Complete");
        completeLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 22));
        completeLabel.setTextFill(Color.web("#D0E4FF"));

        banner.getChildren().addAll(congratsLabel, trophyCircle, completeLabel);

        // White lower section with stats grid + buttons
        VBox lower = new VBox(30);
        lower.setAlignment(Pos.CENTER);
        lower.setPadding(new Insets(36, 60, 36, 60));
        lower.setBackground(new Background(new BackgroundFill(
                Color.WHITE, new CornerRadii(0, 0, 15, 15, false), Insets.EMPTY)));

        // Stats
        int durationMinutes = durationSeconds / 60;
        int calories        = (int) Calculator.calculateCaloriesBurned(durationMinutes, 8.0);
        String durationText = String.format("%d:%02d", durationMinutes, durationSeconds % 60);

        GridPane statsGrid = new GridPane();
        statsGrid.setHgap(24);
        statsGrid.setVgap(16);
        statsGrid.setAlignment(Pos.CENTER);

        statsGrid.add(statCard(durationText,                  "Duration"),   0, 0);
        statsGrid.add(statCard(String.valueOf(exerciseCount), "Exercises"),  1, 0);
        statsGrid.add(statCard(calories + " cal",             "Calories"),   2, 0);

        // Buttons
        Button showHistoryBtn = buildButton("View History", "#1E5AA8");
        showHistoryBtn.setOnAction(e -> stateManager.showWorkoutHistoryScreen());

        Button doneBtn = buildButton("Back to Builder", "#1E5AA8");
        doneBtn.setOnAction(e -> stateManager.showRoutineBuilderScreen());

        HBox buttonRow = new HBox(20, showHistoryBtn, doneBtn);
        buttonRow.setAlignment(Pos.CENTER);

        lower.getChildren().addAll(statsGrid, buttonRow);

        content.getChildren().addAll(banner, lower);
        card.getChildren().add(content);
        root.getChildren().add(card);

        scene = new Scene(root, 1200, 800);
    }

    /*
     * Stat card matching the app's existing card visual language.
     * Uses the same light background tone used elsewhere in the app.
     */
    private VBox statCard(String value, String label) {

        Label valueLabel = new Label(value);
        valueLabel.setFont(Font.font("Segoe UI", FontWeight.BOLD, 28));
        valueLabel.setTextFill(Color.web("#111111"));

        Label nameLabel = new Label(label);
        nameLabel.setFont(Font.font("Segoe UI", 13));
        nameLabel.setTextFill(Color.web("#888888"));

        VBox card = new VBox(6, valueLabel, nameLabel);
        card.setPrefSize(180, 90);
        card.setMinSize(180, 90);
        card.setAlignment(Pos.CENTER);
        card.setBackground(new Background(new BackgroundFill(
                Color.web("#F0F4FA"), new CornerRadii(10), Insets.EMPTY)));

        return card;
    }

    private Button buildButton(String text, String hex) {

        Button btn = new Button(text);
        btn.setPrefSize(200, 48);
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 14));
        btn.setStyle(
                "-fx-background-color: " + hex + ";" +
                "-fx-text-fill: white;" +
                "-fx-background-radius: 10;" +
                "-fx-cursor: hand;"
        );
        return btn;
    }

    public Scene getScene() {
        return scene;
    }
}
