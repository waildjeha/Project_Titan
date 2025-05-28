package com.ken10.landing;

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
        System.out.println("  Position: (" + initialState.x + ", " + initialState.y + ") km");
        System.out.println("  Velocity: (" + initialState.vx + ", " + initialState.vy + ") km/s");
        System.out.println("  Angle: " + Math.toDegrees(initialState.theta) + "°");
        System.out.println("  Angular velocity: " + initialState.vtheta + " rad/s");
        System.out.println();

        // Creating and configuring the controller
        OpenLoopController controller = new OpenLoopController(42); // Fixed seed for reproducibility
        controller.setMaxIterations(5000);  // Reduce for faster testing
        controller.setScheduleParameters(150, 0.5); // 150 steps, 0.5s each = 75s total
        controller.setMutationParameters(0.15, 0.25);

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
        // TODO: Provide the actual altitude (in km) from the surface of Titan
        double y = ?; // e.g., 150–300 km (still haven't managed to find the value sorry)

        // Arbitrary horizontal position (x) – doesn’t matter much
        double x = 0.5;

        // Vertical velocity is zero (parallel to the surface)
        double vy = 0.0;

        // Constants for Titan and gravity
        double G = 6.6743e-20;       // Gravitational constant in km^3/kg/s^2
        double M_TITAN = 1.3452e23;  // Mass of Titan in kg
        double R_TITAN = 2575.5;     // Radius of Titan in km

        // Calculate distance from Titan's center (surface + altitude)
        double r = R_TITAN + y;

        // Compute the orbital velocity at that distance
        double vx = Math.sqrt(G * M_TITAN / r);

        // We ignore rotation for now
        double theta = 0.0;
        double vtheta = 0.0;
        double time = 0.0;

        // Print to confirm values
        System.out.println("Initial State:");
        System.out.println("x = " + x + " km");
        System.out.println("y = " + y + " km (altitude)");
        System.out.println("vx = " + vx + " km/s (orbital velocity)");
        System.out.println("vy = " + vy + " km/s");

        return new LandingState(x, y, theta, vx, vy, vtheta, time);
    }

    /**
     * Print landing accuracy information
     */
    private static void printLandingAccuracy(LandingState finalState) {
        System.out.println("Landing Accuracy:");
        System.out.printf("  Horizontal error: %.1f m%n", Math.abs(finalState.x * 1000));
        System.out.printf("  Vertical error: %.1f m%n", Math.abs(finalState.y * 1000));
        System.out.printf("  Angle error: %.2f°%n", Math.toDegrees(Math.abs(finalState.theta % (2*Math.PI))));
        System.out.printf("  Horizontal velocity: %.1f m/s%n", Math.abs(finalState.vx * 1000));
        System.out.printf("  Vertical velocity: %.1f m/s%n", Math.abs(finalState.vy * 1000));
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
                        state.time, state.x, state.y, state.vy);
            }
        }
    }
}