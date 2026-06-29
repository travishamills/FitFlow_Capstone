/*
 * File: WorkoutHistoryScreen.java
 * Version: 0.7.1
 * Date last edited: 6/28/2026
 * Author: Alex Ronn
 * Update Notes:
 *   - Formatted the date column from raw LocalDateTime string to
 *     "MMM dd, yyyy  h:mm a" (e.g. "Jun 21, 2026  2:32 PM").
 *   - Added a Replay button to each history row. Exercise selections stored
 *     in the WorkoutHistory record are passed directly to the builder, so
 *     no routine name lookup is needed.
 */
package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import model.RoutineExerciseSelection;
import model.WorkoutHistory;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class WorkoutHistoryScreen extends BaseScreen {

    private Scene scene;

    // Formatter applied to dates parsed from history entry strings.
    private static final DateTimeFormatter DISPLAY_FORMAT =
            DateTimeFormatter.ofPattern("MMM dd, yyyy  h:mm a");

    public WorkoutHistoryScreen(AppStateManager stateManager) {
        super(stateManager);
        buildScreen();
    }

    private void buildScreen() {

        StackPane root = createRootLayout();

        StackPane card = createCard(1000, 650);

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

        // History list — loaded as WorkoutHistory objects so selections are available

        VBox historyList = new VBox(10);
        historyList.setPadding(new Insets(10));

        List<WorkoutHistory> entries = stateManager.getWorkoutHistoryObjects();

        if (entries == null || entries.isEmpty()) {

            Label emptyLabel = new Label("No workout history found.");
            emptyLabel.setFont(Font.font("Segoe UI", 14));
            emptyLabel.setTextFill(Color.GRAY);
            historyList.getChildren().add(emptyLabel);
            historyList.setAlignment(Pos.CENTER);

        } else {

            for (WorkoutHistory entry : entries) {
                historyList.getChildren().add(buildHistoryRow(entry));
            }
        }

        ScrollPane scrollPane = new ScrollPane(historyList);
        scrollPane.setFitToWidth(true);
        scrollPane.setPrefHeight(480);
        scrollPane.setStyle("-fx-background-color: transparent; -fx-background: transparent;");

        // Assemble

        content.getChildren().addAll(header, scrollPane);

        card.getChildren().add(content);
        root.getChildren().add(card);
        addNavigationMenu(root);

        scene = new Scene(root, 1250, 800);
    }

    /*
     * Builds one row card for a single WorkoutHistory record.
     *
     * Uses the WorkoutHistory object directly so the Replay button has access
     * to the stored exercise selections without a secondary lookup.
     */
    private HBox buildHistoryRow(WorkoutHistory entry) {

        HBox row = new HBox(20);
        row.setAlignment(Pos.CENTER_LEFT);
        row.setPadding(new Insets(14, 20, 14, 20));

        row.setBackground(new Background(new BackgroundFill(
                Color.web("#1E5AA8"),
                new CornerRadii(10),
                Insets.EMPTY
        )));

        String displayDate = formatDate(entry.getCompletedDate());
        String routine     = entry.getRoutineName();
        String duration    = entry.getDuration() + " seconds";
        String calories    = entry.getEstimatedCalories() + " calories";

        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        Button replayButton = buildReplayButton(entry.getExerciseSelections());

        row.getChildren().addAll(
                buildField("Date",     displayDate, 230),
                buildField("Routine",  routine,     220),
                buildField("Duration", duration,    160),
                buildField("Calories", calories,    120),
                spacer,
                replayButton
        );

        return row;
    }

    /*
     * Parses the raw LocalDateTime string stored in the history CSV and
     * returns a human-readable formatted string.
     * Falls back to the raw string if parsing fails (e.g. older format rows).
     */
    private String formatDate(String raw) {
        try {
            LocalDateTime dt = LocalDateTime.parse(raw);
            return dt.format(DISPLAY_FORMAT);
        } catch (DateTimeParseException e) {
            return raw;
        }
    }

    /*
     * Builds the Replay button for a history row.
     *
     * Passes the stored exercise selections directly to AppStateManager so
     * the builder opens pre-populated. If the selections list is empty
     * (e.g. a row saved before this feature was added), the builder opens
     * normally with no exercises pre-loaded.
     */
    private Button buildReplayButton(List<RoutineExerciseSelection> selections) {

        Button btn = new Button("▶");
        btn.setFont(Font.font("Segoe UI", FontWeight.BOLD, 13));
        btn.setStyle("""
            -fx-background-color: white;
            -fx-text-fill: #1E5AA8;
            -fx-background-radius: 8;
            -fx-cursor: hand;
            -fx-padding: 6 14 6 14;
            """);

        btn.setOnAction(event ->
                stateManager.showRoutineBuilderScreen(selections));

        return btn;
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
