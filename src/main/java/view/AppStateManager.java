/*
 * File: AppStateManager.java
 * Version: 0.6.1
 * Date last edited: 6/20/2026
 * Author: Alex Ronn
 * Modified by: David Lewis
 * File Purpose: Manages swapping between different pages in the application.
 * Update Notes: Added routine-save routing so RoutineBuilderScreen sends save
 * requests through AppStateManager before reaching FitFlowFacade. This keeps
 * the view layer separate from the service layer and matches the team UML flow.
 */

package view;
import java.util.List;
import javafx.stage.Stage;
import model.UserProfile;
import service.*;

public class AppStateManager {
    private final Stage primaryStage;
    private LoginScreen loginScreen;
    private SignupScreen signupScreen; 
    private ProfileScreen profileScreen;
    private FitFlowFacade facade;
    private String sessionToken;

    public AppStateManager(Stage primaryStage) {
        this.primaryStage = primaryStage;
        facade = new FitFlowFacade();
    }

    public Stage getPrimaryStage() {
        return primaryStage;
    }
    
    /**
     * Shows the login screen.
     */
    public void showLoginScreen() {

    	if (loginScreen == null)
    		loginScreen = new LoginScreen(this);
    	else
    		loginScreen.clearPassword();

        primaryStage.setScene(
                loginScreen.getScene()
        );
    }

    /**
     * Shows the signup screen.
     */
    public void showSignupScreen() {

    	if (signupScreen == null)
    		signupScreen = new SignupScreen(this);
    	else
    		signupScreen.clearPasswords();

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
    	
    	if (profileScreen == null) {
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

            profileScreen =
                    new ProfileScreen(
                            this,
                            testUser
                    );
    	}
    	
        primaryStage.setScene(
        		profileScreen.getScene()
        );
    }
    
    /*
     * Handles attempts to log in
     */
    public void signInAttempt(String username, String password) {
    	ServiceResponse<String> attempt = facade.signIn(username, password);
    	if (attempt.isSuccess()) {
    		sessionToken = attempt.getData();
    		showDashboardScreen();
    	} else {
    		String reason = attempt.getMessage();
    		loginScreen.showError(reason);
    	}
    }
    
    /*
     * Handles logging out
     */
    public void logOut() {
    	ServiceResponse<Boolean> attempt = facade.logout(sessionToken);
    	if (attempt.isSuccess()) {
    		loginScreen.showSuccess(attempt.getMessage());
    		showLoginScreen();
    	} else {
    		loginScreen.showError(attempt.getMessage());
    		showLoginScreen();
    	}
    }
    
    /*
     * Handles attempts to sign up
     */
    public void signUpAttempt(String username, String password, String email) {
    	ServiceResponse<String> attempt = facade.signUp(username, password, email);
    	if (attempt.isSuccess()) {
    		showLoginScreen();
    		loginScreen.showSuccess(attempt.getMessage());
    		loginScreen.fillUsername(username);
    	} else {
    		String reason = attempt.getMessage();
    		signupScreen.showError(reason);
    	}
    }
    
    /*
     * Gets the exercise names that should be displayed in the routine builder.
     *
     * What: Requests the exercise list from FitFlowFacade.
     * Why: Keeps the JavaFX screen from building its own workout data.
     * How: Sends the current session token to the service layer and returns
     * only the list data when the request succeeds.
     */
    public List<String> getExercises() {
    	ServiceResponse<List<String>> attempt = facade.getWorkouts(sessionToken);
    	if (attempt.isSuccess()) {
    		return attempt.getData();
    	}
    	return List.of();
    }
    
    /*
     * Sends a routine save request from the UI to the service facade.
     *
     * What: Receives a routine name and selected exercise names from the
     * routine builder screen.
     * Why: The view package should not call FitFlowFacade directly.
     * How: AppStateManager passes the current session token and routine data
     * to FitFlowFacade.saveWorkout, then returns the ServiceResponse so the
     * screen can display the real success or error message.
     */
    public ServiceResponse<Boolean> saveRoutine(String routineName, List<String> exerciseNames) {
        return facade.saveWorkout(sessionToken, routineName, exerciseNames);
    }
    
    /*
     * Handles attempts to sign up
     */
    public void saveProfile(UserProfile profileData) {
    	System.out.println("PLACEHOLDER - SHOULD ATTEMPT TO SAVE PROFILE AS " + profileData);
    	/*
    	ServiceResponse<String> attempt = facade.saveProfile(sessionToken, profileData);
    	if (attempt.isSuccess()) {
    		// notify success? need label on profileScreen
    	} else {
    		// notify error? still need label
    	}
    	*/
    }
    
}
