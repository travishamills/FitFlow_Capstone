package repository;

import model.UserProfile;
import model.WorkoutRoutine;
import model.WorkoutHistory;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;


/*
 * File: CSVHelper.java
 * Author: Michael Lee
 * Project: FitFlow
 * Date: June 8, 2026
 * Version: 1.1
 * Description:
 * This file handles the CSV saving part of the backend.
 * I use this class to save profiles, workout routines, and workout history
 * into the data folder so the information does not only stay in the program.
 */

public class CSVHelper {

    /*
     * This method makes sure the data folder exists before anything gets saved.
     * If the folder is already there, nothing really changes.
     * If it is missing, the program creates it automatically.
     */
    private static void makeSureDataFolderExists() {
        try {
            Files.createDirectories(Paths.get("data"));
        } catch (IOException e) {
            System.out.println("Could not create data folder: " + e.getMessage());
        }
    }

    /*
     * This method saves a user profile into profiles.csv.
     * It first checks that the data folder exists, then it adds the profile
     * information as a new line in the CSV file.
     */
    public static void saveProfile(UserProfile profile) {
        makeSureDataFolderExists();

        try {
            FileWriter writer = new FileWriter("data/profiles.csv", true);
            writer.write(profile.toCSV() + "\n");
            writer.close();

            System.out.println("Profile saved to CSV.");
        } catch (IOException e) {
            System.out.println("Error saving profile: " + e.getMessage());
        }
    }

    /*
     * This method saves a workout routine into workout_routines.csv.
     * It is used after a routine is created and checked as valid.
     */
    public static void saveWorkoutRoutine(WorkoutRoutine routine) {
        makeSureDataFolderExists();

        try {
            FileWriter writer = new FileWriter("data/workout_routines.csv", true);
            writer.write(routine.toCSV() + "\n");
            writer.close();

            System.out.println("Workout routine saved to CSV.");
        } catch (IOException e) {
            System.out.println("Error saving workout routine: " + e.getMessage());
        }
    }

    /*
     * This method saves a completed workout record into workout_history.csv.
     * This helps keep track of workouts that the user has finished.
     */
    public static void saveWorkoutHistory(WorkoutHistory history) {
        makeSureDataFolderExists();

        try {
            FileWriter writer = new FileWriter("data/workout_history.csv", true);
            writer.write(history.toCSV() + "\n");
            writer.close();

            System.out.println("Workout history saved to CSV.");
        } catch (IOException e) {
            System.out.println("Error saving workout history: " + e.getMessage());
        }
    }
}