/*
 * File: UserProfile.java
 * Version: 0.4.2
 * Date last edited: 6/6/2026
 * Original Author: Michael Lee
 * Adapted by: Alex Ronn, Travisha Mills
 * Purpose:
 * Represents one user's profile information.
 *
 * This class combines the frontend profile fields
 * with the backend workout-related fields so the whole
 * application uses one shared UserProfile model.
 */

package model;

public class UserProfile {

    // Unique identifier for the user.
    private String userId;

    // Username used for login.
    private String username;

    // User's first name.
    private String firstName;

    // User's last name.
    private String lastName;

    // User's age.
    private int age;

    // User's height.
    private double height;

    // User's weight.
    private double weight;

    // User's selected gender.
    private String gender;

    /**
     * Creates a user profile.
     */
    public UserProfile(
            String userId,
            String username,
            String firstName,
            String lastName,
            int age,
            double height,
            double weight,
            String gender) {

        this.userId = userId;
        this.username = username;
        this.firstName = firstName;
        this.lastName = lastName;
        this.age = age;
        this.height = height;
        this.weight = weight;
        this.gender = gender;
    }

    // Returns the user's ID.
    public String getUserId() {
        return userId;
    }

    // Returns the username.
    public String getUsername() {
        return username;
    }

    // Returns the first name.
    public String getFirstName() {
        return firstName;
    }

    // Returns the last name.
    public String getLastName() {
        return lastName;
    }

    // Returns the age.
    public int getAge() {
        return age;
    }

    // Returns the height.
    public double getHeight() {
        return height;
    }

    // Returns the weight.
    public double getWeight() {
        return weight;
    }

    // Returns the gender.
    public String getGender() {
        return gender;
    }

    // Updates first name.
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    // Updates last name.
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    // Updates age.
    public void setAge(int age) {
        this.age = age;
    }

    // Updates height.
    public void setHeight(double height) {
        this.height = height;
    }

    // Updates weight.
    public void setWeight(double weight) {
        this.weight = weight;
    }

    // Updates gender.
    public void setGender(String gender) {
        this.gender = gender;
    }

    /**
     * Converts profile information into CSV format.
     */
    public String toCSV() {

        return userId + "," +
                username + "," +
                firstName + "," +
                lastName + "," +
                age + "," +
                height + "," +
                weight + "," +
                gender;
    }

    /**
     * Returns a readable profile summary.
     */
    @Override
    public String toString() {

        return "User: " + username +
                "\nFirst Name: " + firstName +
                "\nLast Name: " + lastName +
                "\nAge: " + age +
                "\nHeight: " + height +
                "\nWeight: " + weight +
                "\nGender: " + gender;
    }
}