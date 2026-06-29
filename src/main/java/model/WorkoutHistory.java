/*
 * File: WorkoutHistory.java
 * Author: Michael Lee
 * Course: CMSC 495
 * Project: FitFlow
 * Date: June 2026
 * Version: 1.2
 *
 * Description:
 * This file stores one completed workout record for FitFlow.
 */

package model;

import java.util.ArrayList;
import java.util.List;

public class WorkoutHistory {

    // Basic workout history details
    private String historyId;
    private String userId;
    private String routineName;
    private String completedDate;
    private int duration;
    private double estimatedCalories;

    // Exercise selections stored so replay can restore full detail.
    // Empty list when loaded from older rows that predate this field.
    private List<RoutineExerciseSelection> exerciseSelections;

    /*
     * Creates a completed workout history record without exercise detail.
     * Used by older code paths and when selections are not available.
     */
    public WorkoutHistory(String historyId, String userId, String routineName,
                          String completedDate, int duration, double estimatedCalories) {
        this.historyId          = historyId;
        this.userId             = userId;
        this.routineName        = routineName;
        this.completedDate      = completedDate;
        this.duration           = duration;
        this.estimatedCalories  = estimatedCalories;
        this.exerciseSelections = new ArrayList<>();
    }

    /*
     * Creates a completed workout history record with full exercise detail.
     * Used by the guided workout save path so replay can restore all settings.
     */
    public WorkoutHistory(String historyId, String userId, String routineName,
                          String completedDate, int duration, double estimatedCalories,
                          List<RoutineExerciseSelection> exerciseSelections) {
        this.historyId          = historyId;
        this.userId             = userId;
        this.routineName        = routineName;
        this.completedDate      = completedDate;
        this.duration           = duration;
        this.estimatedCalories  = estimatedCalories;
        this.exerciseSelections = exerciseSelections != null
                ? exerciseSelections
                : new ArrayList<>();
    }

    /*
     * Gets the history ID.
     */
    public String getHistoryId() {
        return historyId;
    }

    /*
     * Gets the user ID.
     */
    public String getUserId() {
        return userId;
    }

    /*
     * Gets the completed routine name.
     */
    public String getRoutineName() {
        return routineName;
    }

    /*
     * Gets the completed date.
     */
    public String getCompletedDate() {
        return completedDate;
    }

    /*
     * Gets the workout duration.
     */
    public int getDuration() {
        return duration;
    }

    /*
     * Gets the estimated calories burned.
     */
    public double getEstimatedCalories() {
        return estimatedCalories;
    }

    /*
     * Gets the exercise selections stored with this history record.
     * Returns an empty list when loaded from an older row without this field.
     */
    public List<RoutineExerciseSelection> getExerciseSelections() {
        return exerciseSelections;
    }

    /*
     * Serializes the exercise selection list to a single string.
     * Format: name:sets:reps:workSeconds:restSeconds per exercise,
     * separated by semicolons. Colons inside exercise names are replaced
     * with a pipe so parsing is unambiguous.
     * Returns an empty string when the list is empty.
     */
    public static String serializeSelections(List<RoutineExerciseSelection> selections) {
        if (selections == null || selections.isEmpty()) {
            return "";
        }

        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < selections.size(); i++) {
            RoutineExerciseSelection s = selections.get(i);
            // Replace colons and semicolons in the name to protect the format
            String safeName = s.getExerciseName()
                    .replace(":", "|")
                    .replace(";", "|");

            sb.append(safeName)
              .append(":").append(s.getSets())
              .append(":").append(s.getReps())
              .append(":").append(s.getWorkSeconds())
              .append(":").append(s.getRestSeconds());

            if (i < selections.size() - 1) {
                sb.append(";");
            }
        }

        return sb.toString();
    }

    /*
     * Parses a serialized selection string back into a list of selections.
     * Returns an empty list when the string is blank or cannot be parsed.
     */
    public static List<RoutineExerciseSelection> parseSelections(String serialized) {
        List<RoutineExerciseSelection> list = new ArrayList<>();

        if (serialized == null || serialized.trim().isEmpty()) {
            return list;
        }

        String[] entries = serialized.split(";");

        for (String entry : entries) {
            try {
                String[] parts = entry.split(":");

                if (parts.length < 5) {
                    continue;
                }

                // Name may have had pipes substituted for colons; restore them
                String name        = parts[0].replace("|", ":");
                int sets           = Integer.parseInt(parts[1]);
                int reps           = Integer.parseInt(parts[2]);
                int workSeconds    = Integer.parseInt(parts[3]);
                int restSeconds    = Integer.parseInt(parts[4]);

                list.add(new RoutineExerciseSelection(
                        name, sets, reps, workSeconds, restSeconds));
            } catch (Exception e) {
                System.out.println("Could not parse exercise selection: " + entry);
            }
        }

        return list;
    }

    /*
     * Turns the workout history into one CSV row.
     * The exercise selections are appended as a 7th column.
     * Old readers that split on comma with limit 6 will still work
     * because the serialized string uses colons and semicolons only.
     */
    public String toCSV() {
        return historyId + "," + userId + "," + routineName + "," + completedDate + ","
                + duration + "," + estimatedCalories + ","
                + serializeSelections(exerciseSelections);
    }

    /*
     * Shows the workout history in a simple readable way.
     */
    @Override
    public String toString() {
        return "Workout History" +
                "\nRoutine: " + routineName +
                "\nDate Completed: " + completedDate +
                "\nDuration: " + duration + " seconds" +
                "\nEstimated Calories: " + estimatedCalories +
                "\nExercises: " + exerciseSelections.size();
    }
}
