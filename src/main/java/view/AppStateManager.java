/*
 * File: AppStateManager.java
 * Version: 0.7.0
 * Date last edited: 6/28/2026
 * Author: Alex Ronn
 * Modified by: David Lewis
 * File Purpose: Manages swapping between different pages in the application.
 */

package view;

import java.util.List;

import javafx.stage.Stage;
import model.RoutineExerciseSelection;
import model.UserProfile;
import model.WorkoutHistory;
import model.WorkoutSession;
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

        primaryStage.setScene(loginScreen.getScene());
    }

    /**
     * Shows the signup screen.
     */
    public void showSignupScreen() {

        if (signupScreen == null)
            signupScreen = new SignupScreen(this);
        else
            signupScreen.clearPasswords();

        primaryStage.setScene(signupScreen.getScene());
    }

    /**
     * Shows the Dashboard screen.
     */
    public void showDashboardScreen() {

        DashboardScreen dashboardScreen = new DashboardScreen(this);
        primaryStage.setScene(dashboardScreen.getScene());
    }

    /**
     * Shows the routine builder screen with no pre-loaded exercises.
     */
    public void showRoutineBuilderScreen() {

        RoutineBuilderScreen routineBuilderScreen = new RoutineBuilderScreen(this);
        routineBuilderScreen.show(primaryStage);
    }

    /**
     * Shows the routine builder screen pre-populated with a prior workout's
     * exercises. Called by the Replay button on the workout history screen.
     *
     * @param preloadedExercises Exercise selections to load into the builder.
     */
    public void showRoutineBuilderScreen(List<RoutineExerciseSelection> preloadedExercises) {

        RoutineBuilderScreen routineBuilderScreen = new RoutineBuilderScreen(this);
        routineBuilderScreen.show(primaryStage, preloadedExercises);
    }

    /**
     * Shows the workout history screen.
     */
    public void showWorkoutHistoryScreen() {

        WorkoutHistoryScreen historyScreen = new WorkoutHistoryScreen(this);
        primaryStage.setScene(historyScreen.getScene());
    }

    /**
     * Shows the profile screen for the signed-in user.
     */
    public void showProfileScreen() {

        ServiceResponse<UserProfile> profileLoad = facade.getCurrentUserProfile(sessionToken);

        if (profileLoad.isSuccess()) {
            profileScreen = new ProfileScreen(this, profileLoad.getData());
        } else {
            showLoginScreen();
            loginScreen.showError(profileLoad.getMessage());
            return;
        }

        primaryStage.setScene(profileScreen.getScene());
    }

    /**
     * Shows the guided workout screen for the active session.
     */
    public void showGuidedWorkoutScreen() {

        UserGuidedWorkout guidedWorkout = new UserGuidedWorkout(this);
        guidedWorkout.show(primaryStage);
    }

    /**
     * Shows the congratulations screen after a guided workout completes.
     *
     * Called by WorkoutTimer.setDone() once the final exercise finishes. The
     * guided workout UI calculates the planned duration and passes it here so
     * the screen can display accurate stats without querying the backend.
     *
     * @param durationSeconds  Total planned workout duration in seconds.
     * @param exerciseCount    Number of exercises in the completed session.
     */
    public void showCongratulationsScreen(int durationSeconds, int exerciseCount) {

        CongratulationsScreen congratsScreen =
                new CongratulationsScreen(this, durationSeconds, exerciseCount);

        primaryStage.setScene(congratsScreen.getScene());
    }

    /**
     * Loads saved exercise rows for a named routine from the repository.
     *
     * Used by the history screen Replay button. The facade loads all routines
     * saved for the current user and filters to the given name, converting each
     * WorkoutRoutine repository row into a RoutineExerciseSelection so the
     * builder screen can pre-populate its right panel.
     *
     * @param routineName The routine name to look up.
     * @return List of exercise selections for that routine, or empty list.
     */
    public List<RoutineExerciseSelection> getRoutineExercisesByName(String routineName) {

        ServiceResponse<List<RoutineExerciseSelection>> attempt =
                facade.getRoutineExercisesByName(sessionToken, routineName);

        if (attempt.isSuccess()) {
            return attempt.getData();
        }

        return List.of();
    }

    /*
     * Handles attempts to log in.
     */
    public void signInAttempt(String username, String password) {

        ServiceResponse<String> attempt = facade.signIn(username, password);

        if (attempt.isSuccess()) {
            sessionToken = attempt.getData();
            showDashboardScreen();
        } else {
            loginScreen.showError(attempt.getMessage());
        }
    }

    /*
     * Handles logging out.
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
     * Handles attempts to sign up.
     */
    public void signUpAttempt(String username, String password, String email) {

        ServiceResponse<String> attempt = facade.signUp(username, password, email);

        if (attempt.isSuccess()) {
            showLoginScreen();
            loginScreen.showSuccess(attempt.getMessage());
            loginScreen.fillUsername(username);
        } else {
            signupScreen.showError(attempt.getMessage());
        }
    }

    /*
     * Gets the exercise names to display in the routine builder.
     */
    public List<String> getExercises() {

        ServiceResponse<List<String>> attempt = facade.getWorkouts(sessionToken);

        if (attempt.isSuccess()) {
            return attempt.getData();
        }

        return List.of();
    }

    /*
     * Gets the list of completed workout history entries as display strings.
     */
    public List<String> getWorkoutHistory() {

        ServiceResponse<List<String>> attempt = facade.getWorkoutHistory(sessionToken);

        if (attempt.isSuccess()) {
            return attempt.getData();
        }

        return List.of();
    }

    /*
     * Gets the full WorkoutHistory objects for the current user.
     * Used by WorkoutHistoryScreen so each row can access exercise selections
     * for the Replay button directly from the history record.
     */
    public List<WorkoutHistory> getWorkoutHistoryObjects() {

        ServiceResponse<List<WorkoutHistory>> attempt =
                facade.getWorkoutHistoryObjects(sessionToken);

        if (attempt.isSuccess()) {
            return attempt.getData();
        }

        return List.of();
    }

    /*
     * Sends a profile save request from the UI to the service facade.
     *
     * Receives updated profile data from ProfileScreen.
     * ProfileScreen should not save directly to CSV or call backend files
     * because the UI layer should stay separate from service/data logic.
     * AppStateManager passes the active session token and UserProfile
     * object to FitFlowFacade.saveProfile, then returns the ServiceResponse so
     * ProfileScreen can show a clear success or error message.
     */
    public ServiceResponse<Boolean> saveProfile(UserProfile profileData) {
        return facade.saveProfile(sessionToken, profileData);
    }

    // Start workout
    public ServiceResponse<WorkoutSession> startGuidedWorkout(String routineName,
                                                              List<String> exercises,
                                                              int exerciseDuration,
                                                              int restDuration) {
        return facade.startGuidedWorkout(
                sessionToken,
                routineName,
                exercises,
                exerciseDuration,
                restDuration
        );
    }

    /*
     * Starts a guided workout with the detailed values from Routine Builder.
     */
    public ServiceResponse<WorkoutSession> startGuidedWorkoutWithDetails(
            String routineName,
            List<RoutineExerciseSelection> selectedExercises) {

        return facade.startGuidedWorkoutWithDetails(
                sessionToken,
                routineName,
                selectedExercises
        );
    }

    // Pause Workout
    public ServiceResponse<WorkoutSession> pauseGuidedWorkout() {

        // Sends pause request to the backend timer service.
        return facade.pauseGuidedWorkout(sessionToken);
    }

    // Resume Workout
    public ServiceResponse<WorkoutSession> resumeGuidedWorkout() {

        // Sends resume request to the backend timer service.
        return facade.resumeGuidedWorkout(sessionToken);
    }

    // Reset Workout
    public ServiceResponse<WorkoutSession> resetGuidedWorkout() {

        // Sends reset request to the backend timer service.
        return facade.resetGuidedWorkout(sessionToken);
    }

    // Skip Exercise
    public ServiceResponse<WorkoutSession> skipGuidedWorkoutStep() {

        // Sends skip request to move to next exercise.
        return facade.skipGuidedWorkoutStep(sessionToken);
    }

    // Timer Tick
    public ServiceResponse<WorkoutSession> tickGuidedWorkout() {

        // Updates the timer by one second.
        return facade.tickGuidedWorkout(sessionToken);
    }

    // Current Session
    public ServiceResponse<WorkoutSession> getCurrentWorkoutSession() {

        // Returns the current guided workout state.
        return facade.getCurrentWorkoutSession(sessionToken);
    }

    // Save Completed Workout
    public ServiceResponse<Boolean> saveCompletedWorkout() {

        // Saves completed guided workout to workout history.
        return facade.saveCompletedGuidedWorkout(sessionToken);
    }

    /*
     * Saves a completed guided workout directly from the JavaFX timer screen.
     *
     * The guided workout UI uses its own visual countdown timer. Because that
     * screen was not sending UI tick back to TimerService, the backend
     * WorkoutSession was not marked complete. This bridge lets WorkoutTimer save the completed
     * workout summary through the normal FitFlowFacade.saveWorkoutHistory path
     * as soon as the user-facing workout finishes.
     */
    public ServiceResponse<Boolean> saveCompletedWorkout(String workoutSummary, int durationSeconds) {
        return facade.saveWorkoutHistory(sessionToken, workoutSummary, durationSeconds);
    }

    /*
     * Saves a completed guided workout with the full exercise selection list.
     * Used by UserGuidedWorkout so replay can restore exact exercise settings.
     *
     * @param workoutSummary  Short display summary for the history row.
     * @param durationSeconds Total planned workout duration in seconds.
     * @param selections      Exercise selections from the active workout session.
     */
    public ServiceResponse<Boolean> saveCompletedWorkout(String workoutSummary,
                                                         int durationSeconds,
                                                         List<RoutineExerciseSelection> selections) {
        return facade.saveWorkoutHistory(sessionToken, workoutSummary, durationSeconds, selections);
    }
}
