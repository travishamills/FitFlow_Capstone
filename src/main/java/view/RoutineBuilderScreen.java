/*
 * File: RoutineBuilderScreen.java
 * Version: 0.5.2
 * Date last edited: 6/14/2026
 * Original Author: Orange Snaer
 * Adapted by: Alex Ronn
 * File Purpose: This class builds the workout
 * 			routine builder.
 */

package view;

import java.util.List;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
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

public class RoutineBuilderScreen extends BaseScreen {
	
	private final String IMAGES = "/Images";
	private final String ICONS = "/Icons";

    private BorderPane primaryPane;
    private StackPane root;
    private VBox addedRoutineListPanel;
    private boolean firstExerciseAdded = false;
    private VBox exercisePanel;
    private ScrollPane routinePanel;
    private  HBox centerExerciseList;
    private HBox bottomButtons;
    
    public RoutineBuilderScreen(AppStateManager stateManager) {
        super(stateManager);
    }

    public void show(Stage stage) {

    	root = new StackPane();
        primaryPane = new BorderPane();

         //Set background color for all panels
        BackgroundFill mainBackgrnd = new BackgroundFill(Color.web("#1E5AA8"), CornerRadii.EMPTY, Insets.EMPTY);
        Background backgrnd = new Background(mainBackgrnd);
        primaryPane.setBackground(backgrnd);

        //Create panels for exercise list and added exercise
        exercisePanel = ExerciseList();
        routinePanel = AddedRoutinePanel();

        //Create horizontal panel and store exercise list
        centerExerciseList = new HBox(exercisePanel);
        //center exercise list
        centerExerciseList.setAlignment(Pos.CENTER);

        //Set the header postiion on the screen
        primaryPane.setTop(Header());

        //Set the exercise list to the center of the screen
        primaryPane.setCenter(centerExerciseList);

        bottomButtons = BottomButtons(stage);
        primaryPane.setBottom(bottomButtons);
        
        //Add navigation menu
        root.getChildren().add(primaryPane);
        addNavigationMenu(root);

        //Create window size
        Scene scene = new Scene(root, 1600, 1000);

        //Window title
        stage.setScene(scene);
        stage.show();
}


private VBox Header() {

    //page title
    Label title = new Label("FITFLOW Routine Builder");
    //Set font style, weight and size of the text
    title.setFont(Font.font("Arial", FontWeight.BOLD, 40));
    //set text color to white
    title.setTextFill(Color.WHITE);

    VBox header = new VBox(title);
    //Set position to center
    header.setAlignment(Pos.CENTER);
    //Set spacing to 45 to not touch edge
    header.setPadding(new Insets(40));

    return header;
}

private VBox ExerciseList() {
    //Create a vertical layout container and set spacing between the exercise list panels
    VBox box = new VBox(10);
    //Set spacing to 30 to not touch edge
    box.setPadding(new Insets(20));
    //Exercise panel size
    //set size
    box.setPrefWidth(725);

    //Title set for exercise list
    Label title = new Label("Exercise Lists");

    //Set font style, weight and size of the text
    title.setFont(Font.font("Arial", FontWeight.BOLD, 22));
    //set text color
    title.setTextFill(Color.WHITE);

    //Center the title
    box.setAlignment(Pos.TOP_CENTER);

    //Add title to the screen
    box.getChildren().add(title);
    
    //Add exercise lists such as planks, push ups on the screen
    List<String> exercises = stateManager.getExercises();
    
    for (int i = 0; i < exercises.size(); i++) {
    	String exercise = exercises.get(i);
    	box.getChildren().add(ExercisePanelList(exercise));
    }

    return box;
}

private ScrollPane AddedRoutinePanel() {

    //Create a vertical layout and set the spacing between panels to preferred size
    addedRoutineListPanel = new VBox(20);

    //Set spacing to not touch edge
    addedRoutineListPanel.setPadding(new Insets(20));

    //Create scrollpane for the added exerice list to be scrolled when it gets long
    ScrollPane scrollPane = new ScrollPane(addedRoutineListPanel);

    //Set size of the added exercise panel to fit the screen
    scrollPane.setFitToWidth(true);

    //set prefered size off the main screen on the added routine
    // ADD THESE
    scrollPane.setPrefSize(700, 700);

    return scrollPane;
}

private ImageView createIcon(String path, int size) {

    //Create image
    ImageView image = new ImageView( new Image(getClass().getResourceAsStream(path)));

    //Set image preference size
    image.setFitWidth(size);
    image.setFitHeight(size);

    //Set proportions
    image.setPreserveRatio(true);

    return image;
}

private HBox ExercisePanelList(String exerciseName) {

    //Create horizontal layout panel and set padding to not touch the image
    HBox listPanel = new HBox(15);

    //Set the exercise list panel to center left
    listPanel.setAlignment(Pos.CENTER_LEFT);

    //Set margin or spacing within the panel
    listPanel.setPadding(new Insets(15));

    //Set background color for the exercise list panel
    BackgroundFill exerciseListFill = new BackgroundFill(Color.WHITE, new CornerRadii(8), Insets.EMPTY);
    Background backgrndExerciseList = new Background(exerciseListFill);
    listPanel.setBackground(backgrndExerciseList);

    //mageView exerciseImage = createIcon("/Images/pushup.png", 100);
    ImageView exerciseImage = ExerciseImage(exerciseName);

    //Set title
    Label title = new Label(exerciseName);

    //Set font style, weight, and size
    title.setFont(Font.font("Arial", FontWeight.BOLD, 15));

    //create empty flexible space used to push items apart
    Region space = new Region();

    //Set the spacing of the add(+) components to aligned in columns
    HBox.setHgrow(space, Priority.ALWAYS);

    //Create add or plus button with the set size
    Button plusButton = new Button();
    plusButton.setGraphic(createIcon(ICONS + "/plus.png", 15));

    plusButton.setFocusTraversable(false);

    //Add or plus button action when clicked
    plusButton.setOnAction(e -> {
        //If condition, if user add an exercise it will adjust the exercise,
        //list panels and set it to the left side of the screen
        if (!firstExerciseAdded) {
            //Set the exercise list to the left side of the screen
        	primaryPane.setLeft(exercisePanel);

            //Set the added exercise to center
        	primaryPane.setCenter(routinePanel);

            //Set margin on the ;eft side panel to not touch edge
            BorderPane.setMargin(exercisePanel, new Insets(20));
            //Set the margin of the added exercise panel to not touch edge screen
            BorderPane.setMargin(routinePanel, new Insets(20));

            bottomButtons.setVisible(true);
            bottomButtons.setManaged(true);

            firstExerciseAdded = true;
        }

            //Add selected exercise list to the added routine list
            addedRoutineListPanel.getChildren().add(RoutinePanel(exerciseName));
        });

        //Add title, plus button, etc. to the panel
        listPanel.getChildren().addAll(exerciseImage, title, space, plusButton);

        return listPanel;
}

private ImageView ExerciseImage(String exerciseName) {
    switch (exerciseName) {

        //comment for now till the offical image is decided
        case "Push-ups":
           // return createIcon("/Images/pushup.png", 100);
           return createIcon(IMAGES + "/snail1.jpg", 100);

        case "Plank":
            //return createIcon("/Images/plank.png", 100);
            return createIcon(IMAGES + "/snail2.jpg", 100);

        case "Sit-ups":
            //return createIcon("/Images/situp.png", 100);
            return createIcon(IMAGES + "/snail3.jpg", 100);

        case "Squats":
            //return createIcon("/Images/squat.png", 100);
            return createIcon(IMAGES + "/snail4.jpg", 100);

        case "Dumbbell Curls":
            //return createIcon("/Images/biceps.png", 100);
            return createIcon(IMAGES + "/snail5.jpg", 100);

        default:
            //return createIcon("/Images/default.png", 100);
            return createIcon(IMAGES + "/snail.jpg", 100);
    }
}

private HBox RoutinePanel(String exerciseName) {
    HBox addedRoutnPanel = new HBox(20);
    //Set position
    addedRoutnPanel.setAlignment(Pos.CENTER);
    //Set padding/margin
    addedRoutnPanel.setPadding(new Insets(20));

    //Set background color for added routine list
    BackgroundFill fill = new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY);
    Background fills = new Background(fill);
    addedRoutnPanel.setBackground(fills);

    VBox upDownButtons = new VBox(10);
    //Up and down arrow buttons position
    upDownButtons.setAlignment(Pos.CENTER);

    //Set up icon image and prefer size
    Button upButton = new Button();
    upButton.setGraphic(createIcon(ICONS + "/up_arrow.png", 15));

    //Set down icon image
    Button downButton = new Button();
    downButton.setGraphic(createIcon(ICONS + "/down_arrow.png", 15));

    //Add buttons to the panel
    upDownButtons.getChildren().addAll(upButton, downButton);

    ImageView exerciseImage = ExerciseImage(exerciseName);

    //vertical layout for text and controls like delete button
    VBox content = new VBox(15);

    //Exercise title
    Label title = new Label(exerciseName);
    //set fontstyle
    title.setFont(Font.font("Arial", FontWeight.BOLD, 18));

    ///Horizontal layout for rest, sets, etc
    HBox controls = new HBox(30);

    //Add components to the panel
    controls.getChildren().add(ComponentsControl("Sets", 3));
    controls.getChildren().add(ComponentsControl("Reps", 10));
    controls.getChildren().add(ComponentsControl("Rest", 30));

    //Add to the panel
    content.getChildren().addAll(title, controls);

    //create empty flexible space used to push items apart
    Region space = new Region();
    //Set the spacing alignment in column
    HBox.setHgrow(space, Priority.ALWAYS);

    //Set delete icon image with prefered size set
    Button deleteButton = new Button();
    deleteButton.setGraphic(createIcon(ICONS + "/bin.png", 15));

    //Delete button action when clicked
    deleteButton.setOnAction(e -> {

    //Remove the added routine of the user
    addedRoutineListPanel.getChildren().remove(addedRoutnPanel);

    //check if routine panel is empty
    if (addedRoutineListPanel.getChildren().isEmpty()) {
        //remove panel when exercise from added routine are empty
    	primaryPane.setLeft(null);

        HBox centerExerciseList = new HBox(exercisePanel);
        centerExerciseList.setAlignment(Pos.CENTER);

        primaryPane.setCenter(centerExerciseList);

        bottomButtons.setVisible(false);
        bottomButtons.setManaged(false);

        firstExerciseAdded = false;
    }});


        //Move up exercise down the list on the rouine panel based on user preference
        upButton.setOnAction(e -> {
            int i = addedRoutineListPanel.getChildren().indexOf(addedRoutnPanel);

            if (i > 0) {
                addedRoutineListPanel.getChildren().remove(i);
                addedRoutineListPanel.getChildren().add(i - 1, addedRoutnPanel);
                }});

        //Move down exercise down the list on the rouine panel based on user preference
        downButton.setOnAction(e -> {
            int i = addedRoutineListPanel.getChildren().indexOf(addedRoutnPanel);

            if (i < addedRoutineListPanel.getChildren().size() - 1) {

                addedRoutineListPanel.getChildren().remove(i);
                addedRoutineListPanel.getChildren().add(i + 1, addedRoutnPanel);
                }});

        //Add up and down button arrows, delete button, etc to the panel
        addedRoutnPanel.getChildren().addAll(upDownButtons, exerciseImage, content, space, deleteButton);

        return addedRoutnPanel;
}

private VBox ComponentsControl(String labelText, int initialValue) {
    VBox box = new VBox(5);
    Label label = new Label(labelText);

    //Set minus icon image
    Button minusButton = new Button();
    minusButton.setGraphic(createIcon(ICONS + "/minus.png", 15));

    //Set Add/plus icon image
    Button addButton = new Button();
    addButton.setGraphic(createIcon(ICONS + "/plus.png", 15));

    //Create label to display the initial value for sets, reps, andrest
    Label labelValue = new Label(String.valueOf(initialValue));

    //minus button actioon when clicked
    minusButton.setOnAction(e -> {
        //Get current value from the label
        int value = Integer.parseInt(labelValue.getText());

        if (value > 0) {
            //decrease value by 1
            labelValue.setText(String.valueOf(value - 1));
        }});

        addButton.setOnAction(e -> {

        int value = Integer.parseInt(labelValue.getText());

        //increase value by 1
        labelValue.setText(String.valueOf(value + 1));
        });

        HBox button = new HBox(10, minusButton, labelValue, addButton);
        button.setAlignment(Pos.CENTER);
        button.setPadding(new Insets(5));


        //Set background color for the sets, reps panel
        BackgroundFill mainBackgrnd = new BackgroundFill(Color.web("#7aa7ebff"), new CornerRadii(6), Insets.EMPTY);
        Background backgrnd = new Background(mainBackgrnd);
        button.setBackground(backgrnd);

        //Add label
        box.getChildren().addAll(label, button);

        return box;
}


private HBox BottomButtons(Stage stage) {
    Button saveButton = new Button("Save");
    Button startButton = new Button("Start Exercise");

    //Set background color for all panels
    BackgroundFill buttonBckgrnd = new BackgroundFill(Color.LIGHTGRAY, new CornerRadii(6), Insets.EMPTY);
    Background backgrnd = new Background(buttonBckgrnd);
    saveButton.setBackground(backgrnd);
    startButton.setBackground(backgrnd);

    //Set font style, weight and size of the text
    saveButton.setFont(Font.font("Arial", 14));
    startButton.setFont(Font.font("Arial", 14));

    saveButton.setPrefSize(120, 40);
    startButton.setPrefSize(160, 40);

    saveButton.setOnAction(e -> {
        if (addedRoutineListPanel.getChildren().isEmpty()) {
            System.out.println("No exercises to save.");
            return;
        }

        System.out.println("Routine Saved!");
    });

    startButton.setOnAction(e -> startWorkout(stage));

    HBox buttons = new HBox(20);
    buttons.setAlignment(Pos.CENTER_RIGHT);
    buttons.setPadding(new Insets(20));

    //Add to screen
    buttons.getChildren().addAll(saveButton, startButton);

    //Hide buttons
    buttons.setVisible(false);
    buttons.setManaged(false);

    return buttons;
}

private void startWorkout(Stage stage) {
    BorderPane guidedWorkout = new BorderPane();
    //title
    Label title = new Label("Guided workout sample page");
    //set fontstyle
    title.setFont(Font.font("Arial", FontWeight.BOLD, 30));

    guidedWorkout.setCenter(title);

    Scene workoutScene = new Scene(guidedWorkout, 1280, 800);

    stage.setScene(workoutScene);
    }


}


