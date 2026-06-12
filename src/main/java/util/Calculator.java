/*
 * File: Calculator.java
 * Author: Michael Lee
 * Project: FitFlow
 * Date: June 8, 2026
 * Version: 1.1
 * Description:
 * This file handles the basic calculation parts of the FitFlow backend.
 * I use this class to calculate BMI, BMR, calories burned, and total workout time.
 */

package util;

public class Calculator {

    /*
     * This method calculates the user's BMI.
     * If the height is 0 or less, it returns 0 so the program does not crash
     * or divide by zero.
     */
    public static double calculateBMI(double weight, double height) {
        if (height <= 0) {
            return 0;
        }

        return weight / (height * height);
    }

    /*
     * This method calculates a basic BMR estimate.
     * BMR is used to estimate how many calories a person burns at rest.
     * If any of the numbers are invalid, it returns 0.
     */
    public static double calculateBMR(double weight, double height, int age) {
        if (weight <= 0 || height <= 0 || age <= 0) {
            return 0;
        }

        return 10 * weight + 6.25 * height - 5 * age + 5;
    }

    /*
     * This method estimates calories burned during a workout.
     * It multiplies the workout duration by the calories burned per minute.
     */
    public static double calculateCaloriesBurned(int durationMinutes, double caloriesPerMinute) {
        if (durationMinutes <= 0 || caloriesPerMinute <= 0) {
            return 0;
        }

        return durationMinutes * caloriesPerMinute;
    }

    /*
     * This method calculates the total workout time.
     * It adds the exercise duration and rest time, then multiplies that
     * by the number of sets.
     */
    public static int calculateTotalWorkoutTime(int duration, int restTime, int sets) {
        if (duration <= 0 || restTime < 0 || sets <= 0) {
            return 0;
        }

        return (duration + restTime) * sets;
    }
}