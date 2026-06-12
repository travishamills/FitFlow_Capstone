/*
 * File: WorkoutRoutine.java
 * Author: Michael Lee
 * Project: FitFow
 * Date: June 8, 2026
 * Version: 1.1
 * Description:
 * This file represents a workout routine in the FitFlow app.
 * I use this class to store the routine name, exercise, sets, reps,
 * duration, and rest time for a user's workout.
 */

package model;

public class WorkoutRoutine {

    // Unique ID for the workout routine
    private String routineId;

    // ID of the user who owns this routine
    private String userId;

    // Name of the workout routine
    private String routineName;

    // Name of the exercise included in the routine
    private String exerciseName;

    // Number of sets for the exercise
    private int sets;

    // Number of reps for the exercise
    private int reps;

    // Duration of the exercise in seconds
    private int duration;

    // Rest time between sets in seconds
    private int restTime;

    /*
     * This constructor creates a new workout routine.
     * It stores the routine details so the app can use them later.
     */
    public WorkoutRoutine(String routineId, String userId, String routineName, String exerciseName,
                          int sets, int reps, int duration, int restTime) {
        this.routineId = routineId;
        this.userId = userId;
        this.routineName = routineName;
        this.exerciseName = exerciseName;
        this.sets = sets;
        this.reps = reps;
        this.duration = duration;
        this.restTime = restTime;
    }

    /*
     * Returns the routine ID.
     */
    public String getRoutineId() {
        return routineId;
    }

    /*
     * Returns the user ID connected to this routine.
     */
    public String getUserId() {
        return userId;
    }

    /*
     * Returns the name of the workout routine.
     */
    public String getRoutineName() {
        return routineName;
    }

    /*
     * Returns the exercise name used in this routine.
     */
    public String getExerciseName() {
        return exerciseName;
    }

    /*
     * Returns the number of sets.
     */
    public int getSets() {
        return sets;
    }

    /*
     * Returns the number of reps.
     */
    public int getReps() {
        return reps;
    }

    /*
     * Returns the exercise duration in seconds.
     */
    public int getDuration() {
        return duration;
    }

    /*
     * Returns the rest time in seconds.
     */
    public int getRestTime() {
        return restTime;
    }

    /*
     * Calculates the total time for this routine.
     * It adds the exercise duration and rest time, then multiplies it by sets.
     */
    public int getTotalTime() {
        return (duration + restTime) * sets;
    }

    /*
     * Checks if the routine has the basic information needed.
     * This helps make sure an empty or invalid routine does not get saved.
     */
    public boolean isValidRoutine() {
        return routineName != null && !routineName.isEmpty()
                && exerciseName != null && !exerciseName.isEmpty()
                && sets > 0
                && reps >= 0
                && duration > 0
                && restTime >= 0;
    }

    /*
     * Converts the workout routine data into CSV format.
     * This makes it easier to save the routine into workout_routines.csv.
     */
    public String toCSV() {
        return routineId + "," + userId + "," + routineName + "," + exerciseName + ","
                + sets + "," + reps + "," + duration + "," + restTime;
    }

    /*
     * Returns the workout routine information in a readable format.
     * This is mostly used for printing test results in the console.
     */
    @Override
    public String toString() {
        return "Routine: " + routineName +
                "\nExercise: " + exerciseName +
                "\nSets: " + sets +
                "\nReps: " + reps +
                "\nDuration: " + duration + " seconds" +
                "\nRest Time: " + restTime + " seconds";
    }
}