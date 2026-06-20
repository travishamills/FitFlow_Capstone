/*
 * File: ProfileRepository.java
 * Author: Michael Lee
 * Project: FitFlow
 * Date: June 2026
 * Version: 1.0
 * Description:
 * This file saves and loads user profiles.
 * I made this so profile data has its own repository instead of keeping
 * it inside CSVHelper.
 */

package repository;

import model.UserProfile;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ProfileRepository {

    private static final String DATA_FOLDER = "data";
    private static final String FILE_PATH = DATA_FOLDER + "/profiles.csv";
    private static final String HEADER = "userId,username,firstName,lastName,age,height,weight,gender";

    /*
     * Makes sure the data folder exists.
     */
    private void makeSureDataFolderExists() {
        try {
            Files.createDirectories(Paths.get(DATA_FOLDER));
        } catch (IOException e) {
            System.out.println("Could not create data folder: " + e.getMessage());
        }
    }

    /*
     * Makes sure the profile file exists before we use it.
     */
    private void makeSureFileExists() {
        makeSureDataFolderExists();

        try {
            if (!Files.exists(Paths.get(FILE_PATH)) || Files.size(Paths.get(FILE_PATH)) == 0) {
                FileWriter writer = new FileWriter(FILE_PATH);
                writer.write(HEADER + "\n");
                writer.close();
            }
        } catch (IOException e) {
            System.out.println("Could not create profile file: " + e.getMessage());
        }
    }

    /*
     * Saves one user profile to the CSV file.
     */
    public void saveProfile(UserProfile profile) {
        makeSureFileExists();

        try {
            FileWriter writer = new FileWriter(FILE_PATH, true);
            writer.write(profile.toCSV() + "\n");
            writer.close();

            System.out.println("Profile saved to CSV.");
        } catch (IOException e) {
            System.out.println("Error saving profile: " + e.getMessage());
        }
    }

    /*
     * Loads all saved user profiles.
     */
    public List<UserProfile> loadAllProfiles() {
        makeSureFileExists();

        List<UserProfile> profileList = new ArrayList<>();

        try {
            List<String> lines = Files.readAllLines(Paths.get(FILE_PATH));

            for (String line : lines) {
                if (line.trim().isEmpty() || line.equals(HEADER)) {
                    continue;
                }

                UserProfile profile = parseProfile(line);

                if (profile != null) {
                    profileList.add(profile);
                }
            }
        } catch (IOException e) {
            System.out.println("Error loading profiles: " + e.getMessage());
        }

        return profileList;
    }

    /*
     * Finds a profile by user ID.
     */
    public UserProfile findProfileByUserId(String userId) {
        List<UserProfile> profiles = loadAllProfiles();

        for (UserProfile profile : profiles) {
            if (profile.getUserId().equals(userId)) {
                return profile;
            }
        }

        return null;
    }

    /*
     * Finds a profile by username.
     */
    public UserProfile findProfileByUsername(String username) {
        List<UserProfile> profiles = loadAllProfiles();

        for (UserProfile profile : profiles) {
            if (profile.getUsername().equalsIgnoreCase(username)) {
                return profile;
            }
        }

        return null;
    }

    /*
     * Turns one CSV line back into a UserProfile object.
     */
    private UserProfile parseProfile(String line) {
        try {
            String[] parts = line.split(",", -1);

            if (parts.length < 8) {
                return null;
            }

            String userId = parts[0];
            String username = parts[1];
            String firstName = parts[2];
            String lastName = parts[3];
            int age = Integer.parseInt(parts[4]);
            double height = Double.parseDouble(parts[5]);
            double weight = Double.parseDouble(parts[6]);
            String gender = parts[7];

            return new UserProfile(
                    userId,
                    username,
                    firstName,
                    lastName,
                    age,
                    height,
                    weight,
                    gender
            );
        } catch (Exception e) {
            System.out.println("Error reading profile line: " + e.getMessage());
            return null;
        }
    }
}
