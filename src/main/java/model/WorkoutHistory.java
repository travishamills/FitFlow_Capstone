/*
 * File: WorkoutHistory.java
 * Author: Michael Lee
 * Project: FitFlow
 * Date: June 8, 2026
 * Version: 1.1
 * Description:
 * This file represents a completed workout in the FitFlow app.
 * I use this class to keep track of what routine was finished,
 * when it was completed, how long it took, and the estimated calories burned.
 */

package model;

public class WorkoutHistory {

    // Unique ID for this workout history record
    private String historyId;

    // ID of the user who completed the workout
    private String userId;

    // Name of the workout routine that was completed
    private String routineName;

    // Date when the workout was completed
    private String completedDate;

    // Total workout duration in seconds
    private int duration;

    // Estimated calories burned during the workout
    private double estimatedCalories;

    /*
     * This constructor creates a new workout history record.
     * It stores the completed workout information so it can be saved later.
     */
    public WorkoutHistory(String historyId, String userId, String routineName,
                          String completedDate, int duration, double estimatedCalories) {
        this.historyId = historyId;
        this.userId = userId;
        this.routineName = routineName;
        this.completedDate = completedDate;
        this.duration = duration;
        this.estimatedCalories = estimatedCalories;
    }

    /*
     * Returns the history ID.
     */
    public String getHistoryId() {
        return historyId;
    }

    /*
     * Returns the user ID connected to this workout history.
     */
    public String getUserId() {
        return userId;
    }

    /*
     * Returns the name of the routine that was completed.
     */
    public String getRoutineName() {
        return routineName;
    }

    /*
     * Returns the date when the workout was completed.
     */
    public String getCompletedDate() {
        return completedDate;
    }

    /*
     * Returns the total workout duration.
     */
    public int getDuration() {
        return duration;
    }

    /*
     * Returns the estimated calories burned.
     */
    public double getEstimatedCalories() {
        return estimatedCalories;
    }

    /*
     * Converts the workout history data into CSV format.
     * This makes it easier to save the record into workout_history.csv.
     */
    public String toCSV() {
        return historyId + "," + userId + "," + routineName + "," + completedDate + ","
                + duration + "," + estimatedCalories;
    }

    /*
     * Returns the workout history information in a readable format.
     * This is mostly used for printing test results in the console.
     */
    @Override
    public String toString() {
        return "Workout History" +
                "\nRoutine: " + routineName +
                "\nDate Completed: " + completedDate +
                "\nDuration: " + duration + " seconds" +
                "\nEstimated Calories: " + estimatedCalories;
    }
}