//Draft 

/*
 * File: DashboardScreen.java
 * Project: FitFlow - Interactive Workout Assistant
 * Course: UMGC CMSC 495
 * Week: 5
 * Version: v0.5.1
 * Author: Orange Snaer
 * Adapted by: Alex Ronn
 *  /

/*Purpose: This class is a the dashboard page where it will be the main home screen
of the other pages that would help for a smooth navigation between screens.
This class gives user an information or data of their workout progress,
workout schedule they set.*/

package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;


public class DashboardScreen extends BaseScreen {

    private BorderPane root;
    private Scene scene;

    public DashboardScreen(AppStateManager stateManager) {
    	super(stateManager);
    	createScene();
    }


    private void createScene() {

        //Create dashboard layout
        GridPane dashboard = dashboard();

        //added
        StackPane rootLayout = createRootLayout();

        //main layout of the screen using borderpane
        root = new BorderPane();
        //Set position
        root.setCenter(dashboard);

        //added
        rootLayout.getChildren().add(root);

        addNavigationMenu(rootLayout);

        scene = new Scene(rootLayout, 1000, 700);

        //return new Scene(root);
    }



    private GridPane dashboard() {
        String currentCaloriesBurned = "300 kcal";
        String currentWrkoutDuration = "30 mins";

        //Set the current workout status of the day on the screen with a spacing of 40
        HBox workoutStats = new HBox(40, currentStatusWrkout("Calories Burned", currentCaloriesBurned),
                                         currentStatusWrkout("Duration", currentWrkoutDuration));

        GridPane grid = new GridPane();

        //Space between panels
        //grid.setHgap(20);
        grid.setVgap(20);

        //Space from screen edges
        grid.setPadding(new Insets(20));

        //Set the panels to the center
        grid.setAlignment(Pos.CENTER);

        //Panels like recommendations, etc. position set on screen
        grid.add(workoutStats, 0, 1);
        grid.add(reminderPanel(), 0, 2);

        return grid;
    }

    private VBox reminderPanel() {
        //need to link the calendar

        VBox reminders = new VBox();
        //apply the styles from the PanelStyle
        PanelStyle(reminders, "UPCOMING WORKOUT");

        //Labels
        Label schedTimeDate = new Label("Date & Time: ");
        Label schedWorkout = new Label("Exercise list: ");
        Label exerciseName = new Label("Push up");
        Label categoryName = new Label("Core");
        Label duration = new Label("3 mins");

        //Display the labels on panel
        VBox label = new VBox(exerciseName, categoryName, duration);

        //Display the labels on the panel
        reminders.getChildren().addAll(schedTimeDate,schedWorkout, label);

        return reminders;
    }


    private VBox currentStatusWrkout(String title, String value) {

        VBox panelStatus = new VBox(10);
        //Set alignment to center
        panelStatus.setAlignment(Pos.CENTER);
        //Padding set to not touch the edge
        panelStatus.setPadding(new Insets(15));
        //panel size
        panelStatus.setPrefSize(180, 120);

        //Background color of the main screen
        BackgroundFill workoutStatusBckgrnd = new BackgroundFill(Color.WHITE, new CornerRadii(10), Insets.EMPTY);
        Background backgroundStat = new Background(workoutStatusBckgrnd);
        panelStatus.setBackground(backgroundStat);

        //Title
        Label titleLabel = new Label(title);
        //Set font style weeight and size
        titleLabel.setFont(Font.font("Arial", FontWeight.BOLD, 14));

        //Workout status data workout duration in minutes and calorie burned in kcal
        Label goalData = new Label(value);
        //Set fontstyle and size
        goalData.setFont(Font.font("Arial", 24));

        panelStatus.getChildren().addAll(titleLabel, goalData);

        return panelStatus;
    }

    //panels style shared amongst the panels recommendation, reminder, etc.
    private void PanelStyle(VBox panel, String titleText) {

        //Set panels size
        panel.setPrefSize(400, 250);

        //Set background color for all panels
        BackgroundFill createPanelBackgrnd = new BackgroundFill(Color.WHITE, new CornerRadii(10),Insets.EMPTY);
        Background backgroundPanel = new Background(createPanelBackgrnd);
        panel.setBackground(backgroundPanel);

        //Set margins to not touch the egde
        panel.setPadding(new Insets(15));


        //set vertical spacing
        panel.setSpacing(25);

        //set style label for all panels for consistency
        Label title = new Label(titleText);

        //set font size and weight for emphasis
        title.setFont(Font.font("Arial", FontWeight.BOLD, 12));

        panel.getChildren().add(title);
    }
    
    public Scene getScene() {
        return scene;
    }

}



