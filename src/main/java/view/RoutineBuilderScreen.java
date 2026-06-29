/*
 * File: RoutineBuilderScreen.java
 * Version: 0.7.1
 * Date last edited: 6/28/2026
 * Original Author: Orange Snaer
 * Adapted by: Alex Ronn
 * Modified by: David Lewis
 * File Purpose: This class builds the workout routine builder screen.
 */

package view;

import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import model.RoutineExerciseSelection;
import service.ServiceResponse;

public class RoutineBuilderScreen extends BaseScreen {

    private final String IMAGES = "/Images";
    private final String ICONS  = "/Icons";

    private BorderPane primaryPane;
    private StackPane root;
    private VBox addedRoutineListPanel;
    private boolean firstExerciseAdded = false;
    private VBox exercisePanel;
    private ScrollPane routinePanel;
    private HBox       centerExerciseList;
    private HBox       bottomButtons;

    public RoutineBuilderScreen(AppStateManager stateManager) {
        super(stateManager);
    }

    public void show(Stage stage) {
        show(stage, List.of());
    }

    /**
     * Builds and displays the routine builder screen.
     *
     * @param stage            The primary application stage.
     * @param preloadExercises Exercise selections to pre-populate the builder
     *                         panel. Pass an empty list for a blank builder.
     */
    public void show(Stage stage, List<RoutineExerciseSelection> preloadExercises) {

        root         = new StackPane();
        primaryPane  = new BorderPane();

        BackgroundFill mainBackgrnd = new BackgroundFill(
                Color.web("#1E5AA8"), CornerRadii.EMPTY, Insets.EMPTY);
        primaryPane.setBackground(new Background(mainBackgrnd));

        exercisePanel = ExerciseList();
        routinePanel  = AddedRoutinePanel();
        routinePanel.setFitToHeight(true);

        centerExerciseList = new HBox(exercisePanel);
        centerExerciseList.setAlignment(Pos.CENTER);

        VBox centerContainer = new VBox(centerExerciseList);
        VBox.setVgrow(centerExerciseList, Priority.ALWAYS);

        primaryPane.setTop(Header());
        primaryPane.setCenter(centerContainer);

        bottomButtons = BottomButtons(stage);
        primaryPane.setBottom(bottomButtons);

        root.getChildren().add(primaryPane);
        addNavigationMenu(root);

        // Pre-populate before showing so layout is correct on first render
        if (preloadExercises != null && !preloadExercises.isEmpty()) {
            for (RoutineExerciseSelection selection : preloadExercises) {
                if (selection != null && selection.isValidSelection()) {
                    addExerciseToPanel(selection);
                }
            }
        }

        Scene scene = new Scene(root);
        stage.setScene(scene);

        // Toggle maximized off then on so JavaFX re-applies it every time,
        // not just the first time the builder is opened in a session.
        stage.setMaximized(false);
        stage.setMaximized(true);

        stage.show();
    }

    /*
     * Adds one exercise row to the right panel, using the provided selection's
     * values for sets, reps, duration, and rest. Shared by the plus button
     * (which uses defaults) and preloadExercises (which uses saved values).
     */
    private void addExerciseToPanel(RoutineExerciseSelection selection) {

        if (!firstExerciseAdded) {
            primaryPane.setLeft(exercisePanel);
            primaryPane.setCenter(routinePanel);
            BorderPane.setMargin(exercisePanel, new Insets(20));
            BorderPane.setMargin(routinePanel, new Insets(20));
            bottomButtons.setVisible(true);
            bottomButtons.setManaged(true);
            firstExerciseAdded = true;
        }

        addedRoutineListPanel.getChildren().add(
                RoutinePanel(selection.getExerciseName(),
                             selection.getSets(),
                             selection.getReps(),
                             selection.getRestSeconds()));
    }

    // ── Layout builders ────────────────────────────────────────────────────

    private VBox Header() {

        Label title = new Label("FITFLOW Workout Builder");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 40));
        title.setTextFill(Color.WHITE);

        VBox header = new VBox(title);
        header.setAlignment(Pos.CENTER);
        header.setPadding(new Insets(15));
        return header;
    }

    private VBox ExerciseList() {

        VBox box = new VBox(10);
        box.setPadding(new Insets(20));
        box.setPrefWidth(700);
        box.setAlignment(Pos.TOP_CENTER);

        Label title = new Label("Exercise Lists");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
        title.setTextFill(Color.WHITE);
        box.getChildren().add(title);

        List<String> exercises = stateManager.getExercises();
        for (String exercise : exercises) {
            box.getChildren().add(ExercisePanelList(exercise));
        }

        return box;
    }

    private ScrollPane AddedRoutinePanel() {

        addedRoutineListPanel = new VBox(20);
        addedRoutineListPanel.setPadding(new Insets(40));

        ScrollPane scrollPane = new ScrollPane(addedRoutineListPanel);
        scrollPane.setFitToWidth(true);
        return scrollPane;
    }

    private ImageView createIcon(String path, int size) {

        ImageView image = new ImageView(
                new Image(getClass().getResourceAsStream(path)));
        image.setFitWidth(size);
        image.setFitHeight(size);
        image.setPreserveRatio(true);
        return image;
    }

    private HBox ExercisePanelList(String exerciseName) {

        HBox listPanel = new HBox(15);
        listPanel.setAlignment(Pos.CENTER_LEFT);
        listPanel.setPadding(new Insets(15));
        listPanel.setBackground(new Background(
                new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY)));

        ImageView exerciseImage = ExerciseImage(exerciseName);

        Label title = new Label(exerciseName);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 15));

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Button plusButton = new Button();
        plusButton.setGraphic(createIcon(ICONS + "/plus.png", 15));
        plusButton.setFocusTraversable(false);

        plusButton.setOnAction(e -> {
            // Add with default values when the user clicks the plus button
            RoutineExerciseSelection selection =
                    new RoutineExerciseSelection(exerciseName, 3, 10, 60, 30);
            addExerciseToPanel(selection);
        });

        listPanel.getChildren().addAll(exerciseImage, title, space, plusButton);
        return listPanel;
    }

    private ImageView ExerciseImage(String exerciseName) {
        switch (exerciseName) {
            case "Push-ups":      return createIcon("/Images/pushup.png", 100);
            case "Plank":         return createIcon("/Images/pushup.png", 100);
            case "Sit-ups":       return createIcon("/Images/situp.png", 100);
            case "Squats":        return createIcon("/Images/squat.png", 100);
            case "Dumbbell Curls":return createIcon("/Images/dumbbell_curl.png", 100);
            default:              return createIcon("/Images/snail.jpg", 100);
        }
    }

    /*
     * Builds a routine panel row.
     *
     * Accepts initial values so it can be used both by the plus-button path
     * (defaults) and by preloadExercises (saved values).
     */
    private HBox RoutinePanel(String exerciseName, int initialSets, int initialReps, int initialRest) {

        HBox addedRoutnPanel = new HBox(20);

        RoutineExerciseSelection selection =
                new RoutineExerciseSelection(exerciseName, initialSets, initialReps, 60, initialRest);
        addedRoutnPanel.setUserData(selection);

        addedRoutnPanel.setAlignment(Pos.CENTER);
        addedRoutnPanel.setPadding(new Insets(20));
        addedRoutnPanel.setBackground(new Background(
                new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY)));

        VBox upDownButtons = new VBox(10);
        upDownButtons.setAlignment(Pos.CENTER);

        Button upButton   = new Button();
        Button downButton = new Button();
        upButton.setGraphic(createIcon(ICONS + "/up_arrow.png", 15));
        downButton.setGraphic(createIcon(ICONS + "/down_arrow.png", 15));
        upDownButtons.getChildren().addAll(upButton, downButton);

        ImageView exerciseImage = ExerciseImage(exerciseName);

        VBox content = new VBox(15);

        Label title = new Label(exerciseName);
        title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        HBox controls = new HBox(30);
        controls.getChildren().add(ComponentsControl("Sets",      initialSets, selection));
        controls.getChildren().add(ComponentsControl("Reps",      initialReps, selection));
        controls.getChildren().add(ComponentsControl("Rest (sec)", initialRest, selection));

        content.getChildren().addAll(title, controls);

        Region space = new Region();
        HBox.setHgrow(space, Priority.ALWAYS);

        Button deleteButton = new Button();
        deleteButton.setGraphic(createIcon(ICONS + "/bin.png", 15));

        deleteButton.setOnAction(e -> {
            addedRoutineListPanel.getChildren().remove(addedRoutnPanel);

            if (addedRoutineListPanel.getChildren().isEmpty()) {
                primaryPane.setLeft(null);

                HBox center = new HBox(exercisePanel);
                center.setAlignment(Pos.CENTER);
                primaryPane.setCenter(center);

                bottomButtons.setVisible(false);
                bottomButtons.setManaged(false);
                firstExerciseAdded = false;
            }
        });

        upButton.setOnAction(e -> {
            int i = addedRoutineListPanel.getChildren().indexOf(addedRoutnPanel);
            if (i > 0) {
                addedRoutineListPanel.getChildren().remove(i);
                addedRoutineListPanel.getChildren().add(i - 1, addedRoutnPanel);
            }
        });

        downButton.setOnAction(e -> {
            int i = addedRoutineListPanel.getChildren().indexOf(addedRoutnPanel);
            if (i < addedRoutineListPanel.getChildren().size() - 1) {
                addedRoutineListPanel.getChildren().remove(i);
                addedRoutineListPanel.getChildren().add(i + 1, addedRoutnPanel);
            }
        });

        addedRoutnPanel.getChildren().addAll(
                upDownButtons, exerciseImage, content, space, deleteButton);

        return addedRoutnPanel;
    }

    private VBox ComponentsControl(String labelText, int initialValue,
                                   RoutineExerciseSelection selection) {

        VBox box = new VBox(5);
        Label label = new Label(labelText);

        Button minusButton = new Button();
        minusButton.setGraphic(createIcon(ICONS + "/minus.png", 15));

        Button addButton = new Button();
        addButton.setGraphic(createIcon(ICONS + "/plus.png", 15));

        Label labelValue = new Label(String.valueOf(initialValue));

        minusButton.setOnAction(e -> {
            int value = Integer.parseInt(labelValue.getText());
            if (value > 0) {
                int newValue = value - 1;
                labelValue.setText(String.valueOf(newValue));
                updateSelectionValue(selection, labelText, newValue);
            }
        });

        addButton.setOnAction(e -> {
            int value    = Integer.parseInt(labelValue.getText());
            int newValue = value + 1;
            labelValue.setText(String.valueOf(newValue));
            updateSelectionValue(selection, labelText, newValue);
        });

        HBox button = new HBox(10, minusButton, labelValue, addButton);
        button.setAlignment(Pos.CENTER);
        button.setPadding(new Insets(5));
        button.setBackground(new Background(
                new BackgroundFill(Color.web("#7aa7ebff"), new CornerRadii(6), Insets.EMPTY)));

        box.getChildren().addAll(label, button);
        return box;
    }

    private void updateSelectionValue(RoutineExerciseSelection selection,
                                      String labelText, int value) {
        if (selection == null || labelText == null) {
            return;
        }

        switch (labelText) {
            case "Sets":
                selection.setSets(value);
                break;
            case "Reps":
                selection.setReps(value);
                break;
            case "Rest (sec)":
                selection.setRestSeconds(value);
                break;
            default:
                break;
        }
    }

    private HBox BottomButtons(Stage stage) {

        Button startButton = new Button("Start Exercise");

        BackgroundFill buttonBckgrnd = new BackgroundFill(
                Color.LIGHTGRAY, new CornerRadii(6), Insets.EMPTY);
        startButton.setBackground(new Background(buttonBckgrnd));
        startButton.setFont(Font.font("Arial", 14));
        startButton.setPrefHeight(40);
        startButton.setMinWidth(Region.USE_PREF_SIZE);

        startButton.setOnAction(e -> startWorkout(stage));

        HBox buttons = new HBox(20);
        buttons.setMinHeight(80);
        buttons.setAlignment(Pos.CENTER_RIGHT);
        buttons.setPadding(new Insets(-50, 20, 5, -20));
        buttons.getChildren().add(startButton);

        buttons.setVisible(false);
        buttons.setManaged(false);

        BorderPane.setMargin(buttons, new Insets(10));
        return buttons;
    }

    private void startWorkout(Stage stage) {

        List<RoutineExerciseSelection> selectedExercises = getSelectedRoutineSelections();

        ServiceResponse<?> response = stateManager.startGuidedWorkoutWithDetails(
                "Guided Workout", selectedExercises);

        if (!response.isSuccess()) {
            // No overlay to show anymore; silently return (builder stays open)
            return;
        }

        stateManager.showGuidedWorkoutScreen();
    }

    private List<String> getSelectedExerciseNames() {

        List<String> names = new ArrayList<>();
        for (RoutineExerciseSelection s : getSelectedRoutineSelections()) {
            names.add(s.getExerciseName());
        }
        return names;
    }

    private List<RoutineExerciseSelection> getSelectedRoutineSelections() {

        List<RoutineExerciseSelection> selected = new ArrayList<>();

        for (Node node : addedRoutineListPanel.getChildren()) {
            Object userData = node.getUserData();
            if (userData instanceof RoutineExerciseSelection) {
                selected.add((RoutineExerciseSelection) userData);
            }
        }

        return selected;
    }
}
