/*
 * File: AppStateManager.java
 * Version: 0.5.2
 * Date last edited: 6/14/2026
 * Author: Alex Ronn
 * File Purpose: Manages swapping between different pages in the application.
 */

package view;

import javafx.stage.Stage;
import model.UserProfile;

public class AppStateManager {

    private final Stage primaryStage;

    public AppStateManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }

    /**
     * Shows the login screen.
     */
    public void showLoginScreen() {

        LoginScreen loginScreen =
                new LoginScreen(this);

        primaryStage.setScene(
                loginScreen.getScene()
        );
    }

    /**
     * Shows the signup screen.
     */
    public void showSignupScreen() {

        SignupScreen signupScreen =
                new SignupScreen(this);

        primaryStage.setScene(
                signupScreen.getScene()
        );
    }
    
    /**
     * Shows the Dashboard screen.
     */
    public void showDashboardScreen() {

    	DashboardScreen dashboardScreen =
                new DashboardScreen(this);

        primaryStage.setScene(
        		dashboardScreen.getScene()
        );
    }
    
    /**
     * Shows the routine builder screen.
     */
    public void showRoutineBuilderScreen() {

    	RoutineBuilderScreen routineBuilderScreen =
                new RoutineBuilderScreen(this);

    	routineBuilderScreen.show(primaryStage);
    }
    
    /**
     * Shows the profile screen.
     */
    public void showProfileScreen() {

        UserProfile testUser =
                new UserProfile(
                        "1",
                        "jsmith",
                        "John",
                        "Smith",
                        27,
                        70,
                        180,
                        "Male"
                );

        ProfileScreen screen =
                new ProfileScreen(
                        this,
                        testUser
                );

        primaryStage.setScene(
                screen.getScene()
        );
    }
    
}