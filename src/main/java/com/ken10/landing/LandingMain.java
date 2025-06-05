package com.ken10.landing;

import com.ken10.landing.Vector2D;
import com.ken10.landing.LandingState;
import com.ken10.landing.OpenLoopController;
import com.ken10.landing.LandingResult;
import com.ken10.landing.ThrustSchedule;

/**
 * Main class to demonstrate the Open-Loop Controller for Titan landing
 * Replace the main method in App.java with this code
 */
public class LandingMain {

    public static void main(String[] args) {
        System.out.println("=== TITAN LANDING SIMULATION ===");
        System.out.println("Open-Loop Controller Demonstration");
        System.out.println();

        // once we have orbit data->modify
        // These values represent a typical approach from orbit
        LandingState initialState = createExampleInitialState();

        System.out.println("Initial conditions:");
        System.out.println("  Position: (" + initialState.position.x + ", " + initialState.position.y + ") km");
        System.out.println("  Velocity: (" + initialState.velocity.x + ", " + initialState.velocity.y + ") km/s");
        System.out.println("  Angle: " + Math.toDegrees(initialState.theta) + "°");
        System.out.println("  Angular velocity: " + initialState.vtheta + " rad/s");
        System.out.println();

        // Creating and configuring the controller
        OpenLoopController controller = new OpenLoopController(42); // Fixed seed for reproducibility
        controller.setMaxIterations(100000);  // Reduce for faster testing
        controller.setScheduleParameters(1500, 0.5); // 1800 Schritte à 1 Sekunde
        controller.setMutationParameters(0.25, 0.4);

        // Find optimal thrust schedule
        long startTime = System.currentTimeMillis();
        ThrustSchedule optimalSchedule = controller.optimize(initialState);
        long endTime = System.currentTimeMillis();

        System.out.println();
        System.out.println("Optimization completed in " + (endTime - startTime) + " ms");
        System.out.println();

        // Test the optimal schedule
        System.out.println("=== TESTING OPTIMAL SCHEDULE ===");
        LandingResult finalResult = controller.test(initialState, optimalSchedule);

        System.out.println("Final Result:");
        System.out.println("  Status: " + finalResult.getLandingStatus());
        System.out.println("  Fuel used: " + finalResult.getFuelUsed());
        System.out.println("  Fitness score: " + finalResult.getFitnessScore());
        System.out.println("  Final state: " + finalResult.getFinalState());

        if (finalResult.getLandingStatus() == LandingResult.LandingStatus.SUCCESS) {
            System.out.println();
            System.out.println("SUCCESSFUL LANDING!");
            printLandingAccuracy(finalResult.getFinalState());
        } else {
            System.out.println();
            System.out.println("Landing failed. Try adjusting parameters or initial conditions.");
        }

        // Show some trajectory points
        System.out.println();
        System.out.println("=== TRAJECTORY SAMPLE ===");
        showTrajectoryPoints(finalResult);
    }

    /**
     * Create example initial state ->> modify these values based on your mission
     */

    /* hier ist es so dass man anscheinend dieses mal nur noch xy hat als xy intiital velocity und nicht mehr xyz und zum
    *             state: y= abstand zum orbit           x= beliebig
               velocity: y=0 (parallel to the ground    x= calculate ->dependent on state y*/

    private static LandingState createExampleInitialState() {
        Vector2D position = new Vector2D(0.5, 200.0);  // x = 0.5, y = 200.0 km
        Vector2D velocity = new Vector2D(Math.sqrt(6.6743e-20 * 1.3452e23 / (2575.5 + 200.0)), 0.0);

        //ignoring rotation for now
            double theta = 0.0;
            double vtheta = 0.0;
            double time = 0.0;

        return new LandingState(position, theta, velocity, vtheta, time);

    }

    /**
     * Print landing accuracy information
     */
    private static void printLandingAccuracy(LandingState finalState) {
        System.out.println("Landing Accuracy:");


        System.out.printf("  Horizontal error: %.1f m%n", Math.abs(finalState.position.x * 1000));
        System.out.printf("  Vertical error: %.1f m%n", Math.abs(finalState.position.y * 1000));
        System.out.printf("  Angle error: %.2f°%n", Math.toDegrees(Math.abs(finalState.theta % (2*Math.PI))));
        System.out.printf("  Horizontal velocity: %.1f m/s%n", Math.abs(finalState.velocity.x * 1000));
        System.out.printf("  Vertical velocity: %.1f m/s%n", Math.abs(finalState.velocity.y * 1000));
        System.out.printf("  Angular velocity: %.3f rad/s%n", Math.abs(finalState.vtheta));
    }

    /**
     * Show some of the points from the trajectory
     */
    private static void showTrajectoryPoints(LandingResult result) {
        var trajectory = result.getTrajectory();
        int totalPoints = trajectory.size();

        if (totalPoints == 0) {
            System.out.println("No trajectory data available.");
            return;
        }

        System.out.println("Key trajectory points:");

        // Show start, middle, and end points
        int[] indices = {0, totalPoints/4, totalPoints/2, 3*totalPoints/4, totalPoints-1};

        for (int i : indices) {
            if (i < totalPoints) {
                LandingState state = trajectory.get(i);
                System.out.printf("  t=%.1fs: x=%.3fkm, y=%.3fkm, vy=%.3fkm/s%n",
                        state.time, state.position.x, state.position.y, state.velocity.y);

            }
        }
    }
}