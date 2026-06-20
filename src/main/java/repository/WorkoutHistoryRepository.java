/*
 * File: WorkoutHistoryRepository.java
 * Author: Michael Lee
 * Project: FitFlow
 * Date: June 2026
 * Version: 1.0
 *
 * Description:
 * This file handles saving and loading workout history.
 * I made this so workout history has its own repository instead of keeping
 * everything inside one big CSVHelper file.
 */

package repository;

import model.WorkoutHistory;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class WorkoutHistoryRepository {

    private static final String DATA_FOLDER = "data";
    private static final String FILE_PATH = DATA_FOLDER + "/workout_history.csv";
    private static final String HEADER = "historyId,userId,routineName,completedDate,duration,estimatedCalories";

    /*
     * Checks that the data folder is there before saving anything.
     */
    private void makeSureDataFolderExists() {
        try {
            Files.createDirectories(Paths.get(DATA_FOLDER));
        } catch (IOException e) {
            System.out.println("Could not create data folder: " + e.getMessage());
        }
    }

    /*
     * Checks that the workout history file exists.
     * If it does not, this creates it with a header row.
     */
    private void makeSureFileExists() {
        makeSureDataFolderExists();

        try {
            if (!Files.exists(Paths.get(FILE_PATH))) {
                FileWriter writer = new FileWriter(FILE_PATH);
                writer.write(HEADER + "\n");
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Could not create workout history file: " + e.getMessage());
        }
    }

    /*
     * Saves one completed workout to the workout history CSV file.
     */
    public void saveWorkoutHistory(WorkoutHistory history) {
        makeSureFileExists();

        try {
            FileWriter writer = new FileWriter(FILE_PATH, true);
            writer.write(history.toCSV() + "\n");
            writer.close();

            System.out.println("Workout history saved to CSV.");
        } catch (IOException e) {
            System.out.println("Error saving workout history: " + e.getMessage());
        }
    }

    /*
     * Loads all saved workout history records.
     */
    public List<WorkoutHistory> loadAllWorkoutHistory() {
        makeSureFileExists();

        List<WorkoutHistory> historyList = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));

            for (int i = 1; i < lines.size(); i++) {
                WorkoutHistory history = parseWorkoutHistory(lines.get(i));

                if (history != null) {
                    historyList.add(history);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading workout history: " + e.getMessage());
        }

        return historyList;
    }

    /*
     * Gets only the workout history for one user.
     */
    public List<WorkoutHistory> loadWorkoutHistoryByUser(String userId) {
        List<WorkoutHistory> allHistory = loadAllWorkoutHistory();
        List<WorkoutHistory> userHistory = new ArrayList<>();

        for (WorkoutHistory history : allHistory) {
            if (history.getUserId().equals(userId)) {
                userHistory.add(history);
            }
        }

        return userHistory;
    }

    /*
     * Turns one CSV line back into a WorkoutHistory object.
     */
    private WorkoutHistory parseWorkoutHistory(String line) {
        try {
            String[] parts = line.split(",", -1);

            if (parts.length < 6) {
                return null;
            }

            String historyId = parts[0];
            String userId = parts[1];
            String routineName = parts[2];
            String completedDate = parts[3];
            int duration = Integer.parseInt(parts[4]);
            double estimatedCalories = Double.parseDouble(parts[5]);

            return new WorkoutHistory(
                    historyId,
                    userId,
                    routineName,
                    completedDate,
                    duration,
                    estimatedCalories
            );
        } catch (Exception e) {
            System.out.println("Error reading workout history line: " + e.getMessage());
            return null;
        }
    }
}
