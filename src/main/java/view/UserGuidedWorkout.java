/*
 * File: UserGuidedWorkout.java
 * Original Author: Orange Snaer
 * Version: 6.3
 * Adapted by: Alex Ronn
 * Date last edited: 6/22/2026
 * File Purpose: Plays the workout exercises chosen by a user. The user can
 *      navigate through the workout using play, pause, previous, and next
 *      buttons. Exercise data is pulled from AppStateManager via the active
 *      WorkoutSession so this screen matches the rest of the project architecture.
 */

package view;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import model.WorkoutSession;
import service.ServiceResponse;

public class UserGuidedWorkout extends BaseScreen {

    /*
     * Lightweight exercise data holder used by this screen.
     *
     * WorkoutSession stores exercise names as plain strings. This inner class
     * attaches the UI-only fields (image path, sets, reps, work seconds, rest
     * seconds) that the guided workout screen needs without touching the model
     * layer. Default values are used until per-exercise configuration is added
     * to the routine builder and passed through the session.
     */
    public static class ExerciseData {
        public final String name;
        public final String imagePath;
        public final int sets;
        public final int reps;
        public final int workSeconds;
        public final int restSeconds;

        public ExerciseData(String name, String imagePath,
                            int sets, int reps,
                            int workSeconds, int restSeconds) {
            this.name        = name;
            this.imagePath   = imagePath;
            this.sets        = sets;
            this.reps        = reps;
            this.workSeconds = workSeconds;
            this.restSeconds = restSeconds;
        }
    }

    // UI fields

    public WorkoutTimer    timerManager;
    public ImageView       exerciseImageMain;
    public ProgressBar     progressBar;
    public Label           statusLabel;
    public Label           timerLabel;
    public Label           restLabel;
    public VBox            playingNowCard;
    public VBox            upcomingContainer;
    public Button          playStopButton;

    public static final int EXERCISE_REST = 60;
    public int     currentSet      = 1;
    public int     currentExercise = 0;
    public boolean workoutRunning  = false;

    // Populated from the active WorkoutSession in show().
    public ExerciseData[] exercises;

    // Constructor

    public UserGuidedWorkout(AppStateManager stateManager) {
        super(stateManager);
    }

    // Entry point — called by AppStateManager.showGuidedWorkoutScreen()

    public void show(Stage stage) {

        exercises = loadExercisesFromSession();

        // If there are no exercises the session is invalid; fall back to
        // dashboard so the user sees a clear error rather than a broken screen.
        if (exercises.length == 0) {
            stateManager.showDashboardScreen();
            return;
        }

        BorderPane root = new BorderPane();

        timerManager = new WorkoutTimer(this);

        VBox workoutSection = createWorkoutSection();
        HBox controls       = buildBottomPanel(stage);

        VBox centerPanel = new VBox();
        centerPanel.getChildren().addAll(workoutSection, controls);
        VBox.setVgrow(workoutSection, Priority.ALWAYS);

        root.setCenter(centerPanel);

        VBox queue = buildSidePanel();
        root.setRight(queue);

        // Wrap in a StackPane so addNavigationMenu can overlay the hamburger.
        StackPane stackRoot = new StackPane(root);
        addNavigationMenu(stackRoot);

        updateQueue();
        loadNextExerciseImage();
        updateCurrentExercise();

        Scene scene = new Scene(stackRoot, 1500, 950);

        stage.setTitle("FitFlow Guided Workout");
        stage.setScene(scene);
        stage.show();
    }

    // Session data loading

    /*
     * Builds the ExerciseData array from the active WorkoutSession.
     *
     * The session holds exercise names as plain strings. Default values for
     * sets, reps, work seconds, and rest seconds are applied here until the
     * routine builder passes those values through to the session. Image paths
     * are resolved with the same switch used in RoutineBuilderScreen so both
     * screens show consistent visuals.
     */
    private ExerciseData[] loadExercisesFromSession() {

        ServiceResponse<WorkoutSession> response =
                stateManager.getCurrentWorkoutSession();

        if (!response.isSuccess() || response.getData() == null) {
            return new ExerciseData[0];
        }

        List<String> names = response.getData().getExercises();

        if (names == null || names.isEmpty()) {
            return new ExerciseData[0];
        }

        List<ExerciseData> list = new ArrayList<>();

        for (String name : names) {
            list.add(new ExerciseData(
                    name,
                    resolveImagePath(name),
                    3,   // default sets
                    15,  // default reps
                    response.getData().getExerciseDurationSeconds(),
                    response.getData().getRestDurationSeconds()
            ));
        }

        return list.toArray(new ExerciseData[0]);
    }

    // Returns the resource image path for a given exercise name.
    private String resolveImagePath(String exerciseName) {
        switch (exerciseName) {
            case "Push-ups":       return "/Images/pushup.png";
            case "Plank":          return "/Images/pushup.png";
            case "Sit-ups":        return "/Images/situp.png";
            case "Squats":         return "/Images/squat.png";
            case "Dumbbell Curls": return "/Images/dumbbell_curl.png";
            default:               return "/Images/snail.jpg";
        }
    }

    // Returns the resource gif path for a given exercise name.
    private String resolveGifPath(String exerciseName) {
        switch (exerciseName) {
            case "Push-ups":       return "/Images/pushup.gif";
            case "Plank":          return "/Images/pushup.png";
            case "Sit-ups":        return "/Images/situp.gif";
            case "Squats":         return "/Images/squat.gif";
            case "Dumbbell Curls": return "/Images/dumbbell_curl.gif";
            default:               return "/Images/snail.jpg";
        }
    }

    // Image loading

    // Loads the image for the current exercise into the main display area.
    public void loadNextExerciseImage() {
        try {
            String path = resolveGifPath(exercises[currentExercise].name);
            InputStream stream = getClass().getResourceAsStream(path);
            Image image = new Image(stream);
            exerciseImageMain.setImage(image);
        } catch (Exception e) {
            System.out.println("Image failed to load: "
                    + exercises[currentExercise].name);
        }
    }

    // Layout builders

    /*
     * Main screen layout: title, exercise image display, timer row.
     */
    public VBox createWorkoutSection() {

        VBox container = new VBox(15);
        container.setAlignment(Pos.TOP_CENTER);

        BackgroundFill mainBackground = new BackgroundFill(
                Color.web("#2962bdff"), CornerRadii.EMPTY, Insets.EMPTY);
        container.setBackground(new Background(mainBackground));

        Label title = new Label("FITFLOW GUIDED WORKOUT");
        title.setFont(Font.font("Arial", FontWeight.BOLD, 30));
        title.setTextFill(Color.WHITE);

        // Main image display area
        StackPane mainScreenImage = new StackPane();
        mainScreenImage.setMinSize(800, 700);
        mainScreenImage.setPrefSize(800, 700);
        mainScreenImage.setMaxSize(800, 700);

        BackgroundFill bckground = new BackgroundFill(
                Color.web("#6c6e72ff"), new CornerRadii(6), Insets.EMPTY);
        mainScreenImage.setBackground(new Background(bckground));

        exerciseImageMain = new ImageView();
        exerciseImageMain.setFitWidth(700);
        exerciseImageMain.setFitHeight(600);
        exerciseImageMain.setPreserveRatio(true);
        mainScreenImage.getChildren().add(exerciseImageMain);

        // REST badge
        restLabel = new Label("REST");
        restLabel.setBackground(new Background(new BackgroundFill(
                Color.web("#E53935"), new CornerRadii(6), Insets.EMPTY)));
        restLabel.setPadding(new Insets(6, 12, 6, 12));
        restLabel.setTextFill(Color.WHITE);
        restLabel.setFont(Font.font("Arial", FontWeight.BOLD, 20));
        restLabel.setVisible(false);

        // Countdown timer
        timerLabel = new Label("00:00");
        timerLabel.setBackground(new Background(new BackgroundFill(
                Color.web("#174A8B"), CornerRadii.EMPTY, Insets.EMPTY)));
        timerLabel.setPadding(new Insets(12, 30, 12, 30));
        timerLabel.setTextFill(Color.WHITE);
        timerLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        // Progress bar
        progressBar = new ProgressBar(0);
        progressBar.setPrefWidth(350);
        progressBar.setPrefHeight(20);

        HBox timerRow = new HBox(15);
        timerRow.setAlignment(Pos.CENTER);
        timerRow.getChildren().addAll(restLabel, timerLabel, progressBar);

        statusLabel = new Label();
        statusLabel.setTextFill(Color.WHITE);
        statusLabel.setFont(Font.font("Arial", FontWeight.BOLD, 18));

        container.getChildren().addAll(title, statusLabel, mainScreenImage, timerRow);

        return container;
    }

    // Creates an ImageView for a button icon.
    public ImageView IconImage(String path) {
        Image image = new Image(getClass().getResourceAsStream(path));
        ImageView icon = new ImageView(image);
        icon.setFitWidth(35);
        icon.setFitHeight(35);
        icon.setPreserveRatio(true);
        return icon;
    }

    /*
     * Bottom panel with previous, play/pause, and next buttons.
     * The skip button routes through stateManager.skipGuidedWorkoutStep() so
     * the backend WorkoutSession state stays in sync with the UI.
     */
    private HBox buildBottomPanel(Stage stage) {

        HBox controls = new HBox(35);
        controls.setAlignment(Pos.CENTER);
        controls.setPadding(new Insets(20));
        controls.setPrefHeight(100);
        controls.setBackground(new Background(new BackgroundFill(
                Color.WHITE, CornerRadii.EMPTY, Insets.EMPTY)));

        Button previousButton = new Button();
        Button nextButton     = new Button();
        playStopButton        = new Button();

        previousButton.setGraphic(IconImage("/Icons/previous.png"));
        nextButton.setGraphic(IconImage("/Icons/next.png"));
        playStopButton.setGraphic(IconImage("/Icons/play.png"));

        previousButton.setPrefSize(60, 60);
        nextButton.setPrefSize(60, 60);
        playStopButton.setPrefSize(90, 90);

        previousButton.setBackground(new Background(new BackgroundFill(
                Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        nextButton.setBackground(new Background(new BackgroundFill(
                Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        playStopButton.setBackground(new Background(new BackgroundFill(
                Color.WHITE, new CornerRadii(50), Insets.EMPTY)));

        previousButton.setOnAction(e -> {
            if (currentExercise > 0) {
                currentExercise--;
                updateCurrentExercise();
            }
        });

        /*
         * Next / skip routes through the backend so WorkoutSession.skip() is
         * called and the service layer stays in sync. The UI then advances its
         * own index to match.
         */
        nextButton.setOnAction(e -> {
            if (currentExercise < exercises.length - 1) {
                stateManager.skipGuidedWorkoutStep();
                currentExercise++;
                updateCurrentExercise();
            }
        });

        playStopButton.setOnAction(e -> {
            if (workoutRunning) {
                timerManager.stopWorkout();
                stateManager.pauseGuidedWorkout();
            } else {
                timerManager.startWorkout();
                stateManager.resumeGuidedWorkout();
            }
        });

        controls.getChildren().addAll(previousButton, playStopButton, nextButton);

        return controls;
    }

    /*
     * Creates an exercise card showing image, name, sets/reps, duration, rest.
     */
    public VBox WorkoutImageCard(ExerciseData exercise,
                                  String category,
                                  String reps,
                                  String duration) {

        VBox imageCard = new VBox(4);
        imageCard.setPadding(new Insets(12));
        imageCard.setMinHeight(120);
        imageCard.setBackground(new Background(new BackgroundFill(
                Color.web("#2D82E5"), new CornerRadii(15), Insets.EMPTY)));
        imageCard.setBorder(new Border(new BorderStroke(
                Color.web("#8BB7F0"), BorderStrokeStyle.SOLID,
                new CornerRadii(15), new BorderWidths(2))));

        HBox content = new HBox(15);
        content.setAlignment(Pos.CENTER_LEFT);

        ImageView imageQueue = new ImageView(
                new Image(getClass().getResourceAsStream(exercise.imagePath)));
        imageQueue.setFitWidth(80);
        imageQueue.setFitHeight(80);
        imageQueue.setPreserveRatio(true);

        VBox infoLabels = new VBox(4);

        Label nameLabel = new Label(exercise.name);
        nameLabel.setTextFill(Color.WHITE);
        nameLabel.setFont(Font.font("System", FontWeight.BOLD, 16));

        Label categoryLabel = new Label("Set: " + category);
        Label repsLabel     = new Label("Reps: " + reps);
        Label durationLabel = new Label("Time: " + duration);
        Label restInfo      = new Label("Rest: " + exercise.restSeconds + " sec");

        categoryLabel.setTextFill(Color.WHITE);
        repsLabel.setTextFill(Color.WHITE);
        durationLabel.setTextFill(Color.WHITE);
        restInfo.setTextFill(Color.WHITE);

        infoLabels.getChildren().addAll(
                nameLabel, categoryLabel, repsLabel, durationLabel, restInfo);
        content.getChildren().addAll(imageQueue, infoLabels);
        imageCard.getChildren().add(content);

        return imageCard;
    }

    /*
     * Side panel showing workout queue, currently playing card, and upcoming list.
     */
    public VBox buildSidePanel() {

        VBox panel = new VBox(20);
        panel.setPadding(new Insets(20));
        panel.setPrefWidth(400);
        panel.setBackground(new Background(new BackgroundFill(
                Color.web("#F4F6F8"), CornerRadii.EMPTY, Insets.EMPTY)));

        Label queueTitle = new Label("Workout Queue");
        queueTitle.setFont(Font.font("System", FontWeight.BOLD, 20));
        queueTitle.setTextFill(Color.web("#2D82E5"));

        Label playingNowTitle = new Label("Playing Now");
        playingNowTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        playingNowTitle.setTextFill(Color.web("#2D82E5"));

        playingNowCard = new VBox();
        playingNowCard.setSpacing(10);

        Label upcomingTitle = new Label("Upcoming Workout");
        upcomingTitle.setFont(Font.font("System", FontWeight.BOLD, 18));
        upcomingTitle.setTextFill(Color.web("#2D82E5"));

        upcomingContainer = new VBox(15);

        panel.getChildren().addAll(
                queueTitle, playingNowTitle, playingNowCard,
                upcomingTitle, upcomingContainer);

        return panel;
    }

    // State update helpers

    // Refreshes the "Playing Now" card with the current exercise and set number.
    public void updatePlayingNow() {
        ExerciseData exercise = exercises[currentExercise];

        playingNowCard.getChildren().clear();

        VBox card = WorkoutImageCard(
                exercise,
                "Set " + currentSet + " of " + exercise.sets,
                String.valueOf(exercise.reps),
                exercise.workSeconds + " sec");

        card.setBorder(new Border(new BorderStroke(
                Color.web("#FFD54F"), BorderStrokeStyle.SOLID,
                new CornerRadii(15), new BorderWidths(3))));

        playingNowCard.getChildren().add(card);
    }

    // Refreshes both the "Playing Now" card and the upcoming exercise list.
    public void updateQueue() {
        updatePlayingNow();
        loadNextExerciseImage();

        upcomingContainer.getChildren().clear();

        for (int i = currentExercise + 1; i < exercises.length; i++) {
            ExerciseData next = exercises[i];
            upcomingContainer.getChildren().add(WorkoutImageCard(
                    next,
                    "Category",
                    next.sets + " x " + next.reps,
                    next.workSeconds + " sec"));
        }
    }

    // Resets the REST label to its default red style.
    public void RestLabelStyle() {
        restLabel.setBackground(new Background(new BackgroundFill(
                Color.web("#E53935"), new CornerRadii(6), Insets.EMPTY)));
        restLabel.setPadding(new Insets(6, 12, 6, 12));
        restLabel.setTextFill(Color.WHITE);
        restLabel.setFont(Font.font("System", FontWeight.BOLD, 20));
    }

    /*
     * Resets all UI state for the newly selected exercise.
     * Called when the user presses previous or next, and after each exercise
     * transition inside WorkoutTimer.
     */
    public void updateCurrentExercise() {
        currentSet     = 1;
        workoutRunning = false;

        ExerciseData exercise = exercises[currentExercise];

        timerManager.reset();

        timerLabel.setText(String.format(
                "%02d:%02d",
                exercise.workSeconds / 60,
                exercise.workSeconds % 60));

        progressBar.setProgress(0);

        restLabel.setVisible(false);
        restLabel.setText("REST");
        RestLabelStyle();

        updateQueue();

        playStopButton.setGraphic(IconImage("/Icons/play.png"));
    }
}
