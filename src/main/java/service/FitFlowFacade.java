/*
 * File: FitFlowFacade.java
 * Project: FitFlow - Interactive Workout Assistant
 * Course: UMGC CMSC 495
 * Phase: Phase I Source Code
 * Week: 4
 * Version: v0.4.03
 * Author: David Lewis
 * Last Updated: 2026-06-07
 *
 * Purpose:
 * Provides a central integration point between frontend controllers and
 * backend/service logic. Frontend classes should call this facade instead
 * of directly accessing CSV repositories or lower-level backend classes.
 *
 * Dependencies:
 * ServiceResponse, ErrorMessages, ValidationUtil, Java Standard Library.
 *
 */
package service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * Main integration facade for FitFlow.
 */
public class FitFlowFacade {
    private final Map<String, UserAccount> usersByUsername;
    private final Map<String, String> activeSessions;
    private final Map<String, List<String>> routinesByUserId;
    private final Map<String, List<String>> historyByUserId;
    private final List<String> exerciseLibrary;

    /**
     * Creates the facade and loads Phase I starter data.
     */
    public FitFlowFacade() {
        usersByUsername = new HashMap<String, UserAccount>();
        activeSessions = new HashMap<String, String>();
        routinesByUserId = new HashMap<String, List<String>>();
        historyByUserId = new HashMap<String, List<String>>();
        exerciseLibrary = new ArrayList<String>();

        loadStarterExercises();
        createDemoUser();
    }

    /**
     * Registers a new user account after validating basic fields.
     *
     * @param username Desired username.
     * @param password Desired password.
     * @param email User email address.
     * @return ServiceResponse containing the created user ID or an error.
     */
    public ServiceResponse<String> signUp(String username, String password, String email) {
        if (!ValidationUtil.isValidUsername(username)) {
            return ServiceResponse.error(ErrorMessages.USERNAME_REQUIRED, ErrorMessages.CODE_VALIDATION);
        }

        if (!ValidationUtil.isValidPassword(password)) {
            return ServiceResponse.error(ErrorMessages.PASSWORD_TOO_SHORT, ErrorMessages.CODE_VALIDATION);
        }

        if (!ValidationUtil.isValidEmail(email)) {
            return ServiceResponse.error(ErrorMessages.INVALID_EMAIL, ErrorMessages.CODE_VALIDATION);
        }

        String normalizedUsername = username.trim().toLowerCase();

        if (usersByUsername.containsKey(normalizedUsername)) {
            return ServiceResponse.error(ErrorMessages.USER_ALREADY_EXISTS, ErrorMessages.CODE_AUTH);
        }

        String userId = "USER-" + UUID.randomUUID().toString();
        UserAccount account = new UserAccount(userId, normalizedUsername, hashPassword(password), email.trim(), LocalDateTime.now());

        usersByUsername.put(normalizedUsername, account);
        routinesByUserId.put(userId, new ArrayList<String>());
        historyByUserId.put(userId, new ArrayList<String>());

        return ServiceResponse.success(ErrorMessages.SUCCESS_SIGNUP, userId);
    }

    /**
     * Signs in a user and returns a temporary session token.
     *
     * @param username Username entered by the user.
     * @param password Password entered by the user.
     * @return ServiceResponse containing a session token or an error.
     */
    public ServiceResponse<String> signIn(String username, String password) {
        if (ValidationUtil.isBlank(username)) {
            return ServiceResponse.error(ErrorMessages.USERNAME_REQUIRED, ErrorMessages.CODE_VALIDATION);
        }

        if (ValidationUtil.isBlank(password)) {
            return ServiceResponse.error(ErrorMessages.PASSWORD_REQUIRED, ErrorMessages.CODE_VALIDATION);
        }

        String normalizedUsername = username.trim().toLowerCase();
        UserAccount account = usersByUsername.get(normalizedUsername);

        if (account == null || !account.passwordHash.equals(hashPassword(password))) {
            return ServiceResponse.error(ErrorMessages.INVALID_LOGIN, ErrorMessages.CODE_AUTH);
        }

        String sessionToken = UUID.randomUUID().toString();
        activeSessions.put(sessionToken, account.userId);

        return ServiceResponse.success(ErrorMessages.SUCCESS_LOGIN, sessionToken);
    }

    /**
     * Ends a valid session token.
     *
     * @param sessionToken Active session token.
     * @return ServiceResponse explaining whether logout completed.
     */
    public ServiceResponse<Boolean> logout(String sessionToken) {
        if (!isValidSession(sessionToken)) {
            return ServiceResponse.error(ErrorMessages.SESSION_EXPIRED, ErrorMessages.CODE_SESSION);
        }

        activeSessions.remove(sessionToken);
        return ServiceResponse.success(ErrorMessages.SUCCESS_LOGOUT, Boolean.TRUE);
    }

    /**
     * Loads the starter exercise library.
     *
     * @param sessionToken Active session token.
     * @return ServiceResponse containing the exercise names.
     */
    public ServiceResponse<List<String>> getWorkouts(String sessionToken) {
        if (!isValidSession(sessionToken)) {
            return ServiceResponse.error(ErrorMessages.SESSION_EXPIRED, ErrorMessages.CODE_SESSION);
        }

        return ServiceResponse.success(ErrorMessages.SUCCESS_WORKOUTS_LOADED, new ArrayList<String>(exerciseLibrary));
    }

    /**
     * Saves a simple workout routine for the signed-in user.
     *
     * @param sessionToken Active session token.
     * @param routineName Name of the workout routine.
     * @param exerciseNames Selected exercise names.
     * @return ServiceResponse confirming the routine save operation.
     */
    public ServiceResponse<Boolean> saveWorkout(String sessionToken, String routineName, List<String> exerciseNames) {
        if (!isValidSession(sessionToken)) {
            return ServiceResponse.error(ErrorMessages.SESSION_EXPIRED, ErrorMessages.CODE_SESSION);
        }

        if (!ValidationUtil.isValidRoutineName(routineName)) {
            return ServiceResponse.error(ErrorMessages.ROUTINE_NAME_REQUIRED, ErrorMessages.CODE_VALIDATION);
        }

        if (!ValidationUtil.hasSelectedExercises(exerciseNames)) {
            return ServiceResponse.error(ErrorMessages.EXERCISE_LIST_REQUIRED, ErrorMessages.CODE_VALIDATION);
        }

        String userId = activeSessions.get(sessionToken);
        List<String> userRoutines = routinesByUserId.get(userId);

        if (userRoutines == null) {
            userRoutines = new ArrayList<String>();
            routinesByUserId.put(userId, userRoutines);
        }

        String routineRecord = routineName.trim() + " -> " + exerciseNames.toString();
        userRoutines.add(routineRecord);

        return ServiceResponse.success(ErrorMessages.SUCCESS_ROUTINE_SAVED, Boolean.TRUE);
    }

    /**
     * Loads saved workout routines for the active user.
     *
     * @param sessionToken Active session token.
     * @return ServiceResponse containing saved routine records.
     */
    public ServiceResponse<List<String>> getSavedRoutines(String sessionToken) {
        if (!isValidSession(sessionToken)) {
            return ServiceResponse.error(ErrorMessages.SESSION_EXPIRED, ErrorMessages.CODE_SESSION);
        }

        String userId = activeSessions.get(sessionToken);
        List<String> routines = routinesByUserId.get(userId);

        if (routines == null) {
            routines = new ArrayList<String>();
        }

        return ServiceResponse.success("Saved workout routines loaded successfully.", new ArrayList<String>(routines));
    }

    /**
     * Saves a completed workout history record for the active user.
     *
     * @param sessionToken Active session token.
     * @param workoutSummary Short summary of the completed workout.
     * @param durationSeconds Total workout duration in seconds.
     * @return ServiceResponse confirming whether history was saved.
     */
    public ServiceResponse<Boolean> saveWorkoutHistory(String sessionToken, String workoutSummary, int durationSeconds) {
        if (!isValidSession(sessionToken)) {
            return ServiceResponse.error(ErrorMessages.SESSION_EXPIRED, ErrorMessages.CODE_SESSION);
        }

        if (ValidationUtil.isBlank(workoutSummary)) {
            return ServiceResponse.error("Workout history summary is required.", ErrorMessages.CODE_VALIDATION);
        }

        if (!ValidationUtil.isValidDuration(durationSeconds)) {
            return ServiceResponse.error(ErrorMessages.INVALID_DURATION, ErrorMessages.CODE_VALIDATION);
        }

        String userId = activeSessions.get(sessionToken);
        List<String> userHistory = historyByUserId.get(userId);

        if (userHistory == null) {
            userHistory = new ArrayList<String>();
            historyByUserId.put(userId, userHistory);
        }

        userHistory.add(LocalDateTime.now() + " | " + workoutSummary + " | " + durationSeconds + " seconds");
        return ServiceResponse.success("Workout history saved successfully.", Boolean.TRUE);
    }

    /**
     * Loads workout history for the active user.
     *
     * @param sessionToken Active session token.
     * @return ServiceResponse containing workout history records.
     */
    public ServiceResponse<List<String>> getWorkoutHistory(String sessionToken) {
        if (!isValidSession(sessionToken)) {
            return ServiceResponse.error(ErrorMessages.SESSION_EXPIRED, ErrorMessages.CODE_SESSION);
        }

        String userId = activeSessions.get(sessionToken);
        List<String> history = historyByUserId.get(userId);

        if (history == null) {
            history = new ArrayList<String>();
        }

        return ServiceResponse.success(ErrorMessages.SUCCESS_HISTORY_LOADED, new ArrayList<String>(history));
    }

    /**
     * Calculates estimated calories burned using a simple Phase I formula.
     *
     * @param sessionToken Active session token.
     * @param caloriesPerMinute Estimated calories burned per minute.
     * @param durationSeconds Exercise duration in seconds.
     * @return ServiceResponse containing the estimated calories burned.
     */
    public ServiceResponse<Double> calculateCalories(String sessionToken, double caloriesPerMinute, int durationSeconds) {
        if (!isValidSession(sessionToken)) {
            return ServiceResponse.error(ErrorMessages.SESSION_EXPIRED, ErrorMessages.CODE_SESSION);
        }

        if (caloriesPerMinute <= 0 || !ValidationUtil.isValidDuration(durationSeconds)) {
            return ServiceResponse.error(ErrorMessages.INVALID_DURATION, ErrorMessages.CODE_CALCULATION);
        }

        double minutes = durationSeconds / 60.0;
        double estimatedCalories = caloriesPerMinute * minutes;

        return ServiceResponse.success(ErrorMessages.SUCCESS_CALORIES_CALCULATED, estimatedCalories);
    }

    /**
     * Creates a basic workout recommendation using the user's stated goal.
     *
     * @param sessionToken Active session token.
     * @param fitnessGoal User's fitness goal.
     * @return ServiceResponse containing a recommendation sentence.
     */
    public ServiceResponse<String> getRecommendations(String sessionToken, String fitnessGoal) {
        if (!isValidSession(sessionToken)) {
            return ServiceResponse.error(ErrorMessages.SESSION_EXPIRED, ErrorMessages.CODE_SESSION);
        }

        if (ValidationUtil.isBlank(fitnessGoal)) {
            return ServiceResponse.success(ErrorMessages.SUCCESS_RECOMMENDATION_CREATED,
                    "Start with a balanced beginner routine using push-ups, squats, and planks.");
        }

        String goal = fitnessGoal.trim().toLowerCase();

        if (goal.contains("weight") || goal.contains("fat")) {
            return ServiceResponse.success(ErrorMessages.SUCCESS_RECOMMENDATION_CREATED,
                    "Recommended: use shorter rest periods and include squats, sit-ups, and push-ups.");
        }

        if (goal.contains("strength") || goal.contains("muscle")) {
            return ServiceResponse.success(ErrorMessages.SUCCESS_RECOMMENDATION_CREATED,
                    "Recommended: focus on push-ups, squats, and dumbbell curls with steady rest periods.");
        }

        if (goal.contains("core")) {
            return ServiceResponse.success(ErrorMessages.SUCCESS_RECOMMENDATION_CREATED,
                    "Recommended: focus on planks, sit-ups, and controlled bodyweight movements.");
        }

        return ServiceResponse.success(ErrorMessages.SUCCESS_RECOMMENDATION_CREATED,
                "Recommended: follow a balanced routine using all five starter exercises.");
    }

    /**
     * Checks whether a session token exists.
     *
     * @param sessionToken Session token to validate.
     * @return true if the session is active.
     */
    public boolean isValidSession(String sessionToken) {
        return sessionToken != null && activeSessions.containsKey(sessionToken);
    }

    /**
     * Loads the five starter exercises discussed in the project scope.
     */
    private void loadStarterExercises() {
        exerciseLibrary.add("Push-ups");
        exerciseLibrary.add("Sit-ups");
        exerciseLibrary.add("Plank");
        exerciseLibrary.add("Squats");
        exerciseLibrary.add("Dumbbell Curls");
    }

    /**
     * Creates a demo user for quick integration testing.
     */
    private void createDemoUser() {
        UserAccount demo = new UserAccount("USER-DEMO", "demo", hashPassword("password"),
                "demo@fitflow.local", LocalDateTime.now());

        usersByUsername.put("demo", demo);
        routinesByUserId.put("USER-DEMO", new ArrayList<String>());
        historyByUserId.put("USER-DEMO", new ArrayList<String>());
    }

    /**
     * Hashes a password using SHA-256 for Phase I demonstration purposes.
     *
     * @param password Raw password entered by the user.
     * @return Hashed password string.
     */
    private String hashPassword(String password) {
        if (password == null) {
            return "";
        }

        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] hashBytes = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexValue = new StringBuilder();

            for (int index = 0; index < hashBytes.length; index++) {
                String hex = Integer.toHexString(0xff & hashBytes[index]);

                if (hex.length() == 1) {
                    hexValue.append('0');
                }

                hexValue.append(hex);
            }

            return hexValue.toString();
        } catch (NoSuchAlgorithmException exception) {
            return "";
        }
    }

    /**
     * Minimal internal account record used for Phase I integration testing.
     */
    private static class UserAccount {
        private final String userId;
        private final String username;
        private final String passwordHash;
        private final String email;
        private final LocalDateTime createdAt;

        private UserAccount(String userId, String username, String passwordHash, String email, LocalDateTime createdAt) {
            this.userId = userId;
            this.username = username;
            this.passwordHash = passwordHash;
            this.email = email;
            this.createdAt = createdAt;
        }

        /**
         * Formats account data for debugging without exposing password details.
         *
         * @return Safe account summary.
         */
        @Override
        public String toString() {
            return "UserAccount{" +
                    "userId='" + userId + '\'' +
                    ", username='" + username + '\'' +
                    ", email='" + email + '\'' +
                    ", createdAt=" + createdAt +
                    '}';
        }
    }
}