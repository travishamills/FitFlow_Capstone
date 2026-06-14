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
import service.*;

public class AppStateManager {

    private final Stage primaryStage;
    private LoginScreen loginScreen;
    private SignupScreen signupScreen; 
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
    
}