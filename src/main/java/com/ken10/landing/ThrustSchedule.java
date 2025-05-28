package com.ken10.landing;

import java.util.Random;

/**
 * Represents a thrust schedule for the landing module
 * Contains arrays of thrust values over time
 */
public class ThrustSchedule {
    private double[] mainThrust;    // u values (main thruster acceleration)
    private double[] sideThrust;    // v values (rotational torque)
    private double timeStep;        // time step between thrust commands
    private double totalTime;       // total duration of schedule

    // Constants from manual
    private static final double G_TITAN = 1.352e-3;  // km/s²
    private static final double U_MAX = 10 * G_TITAN; // max main thrust
    private static final double V_MAX = 1.0;          // max rotational torque (rad/s²)

    public ThrustSchedule(int steps, double timeStep) {
        this.mainThrust = new double[steps];
        this.sideThrust = new double[steps];
        this.timeStep = timeStep;
        this.totalTime = steps * timeStep;
    }

    /**
     * Generate random thrust schedule
     */
    public static ThrustSchedule generateRandom(int steps, double timeStep, Random random) {
        ThrustSchedule schedule = new ThrustSchedule(steps, timeStep);

        for (int i = 0; i < steps; i++) {
            // Random main thrust between 0 and U_MAX
            schedule.mainThrust[i] = random.nextDouble() * U_MAX;

            // Random side thrust between -V_MAX and V_MAX
            schedule.sideThrust[i] = (random.nextDouble() - 0.5) * 2 * V_MAX;
        }

        return schedule;
    }

    /**
     * Create mutated version of this schedule
     */
    public ThrustSchedule mutate(Random random, double mutationRate, double mutationStrength) {
        ThrustSchedule mutated = new ThrustSchedule(mainThrust.length, timeStep);

        for (int i = 0; i < mainThrust.length; i++) {
            mutated.mainThrust[i] = mainThrust[i];
            mutated.sideThrust[i] = sideThrust[i];

            if (random.nextDouble() < mutationRate) {
                // Mutate main thrust
                double change = (random.nextDouble() - 0.5) * 2 * mutationStrength * U_MAX;
                mutated.mainThrust[i] = Math.max(0, Math.min(U_MAX, mainThrust[i] + change));

                // Mutate side thrust
                change = (random.nextDouble() - 0.5) * 2 * mutationStrength * V_MAX;
                mutated.sideThrust[i] = Math.max(-V_MAX, Math.min(V_MAX, sideThrust[i] + change));
            }
        }

        return mutated;
    }

    /**
     * Get main thrust at specific time step
     */
    public double getMainThrust(int step) {
        if (step >= 0 && step < mainThrust.length) {
            return mainThrust[step];
        }
        return 0; // No thrust after schedule ends
    }

    /**
     * Get side thrust at specific time step
     */
    public double getSideThrust(int step) {
        if (step >= 0 && step < sideThrust.length) {
            return sideThrust[step];
        }
        return 0;
    }

    /**
     * Calculate total fuel consumption
     */
    public double getFuelConsumption() {
        double totalFuel = 0;
        for (int i = 0; i < mainThrust.length; i++) {
            totalFuel += Math.abs(mainThrust[i]) * timeStep;
            totalFuel += Math.abs(sideThrust[i]) * timeStep;
        }
        return totalFuel;
    }

    public int getSteps() {
        return mainThrust.length;
    }

    public double getTimeStep() {
        return timeStep;
    }

    public double getTotalTime() {
        return totalTime;
    }

    @Override
    public String toString() {
        return String.format("ThrustSchedule[steps=%d, dt=%.2f, fuel=%.4f]",
                mainThrust.length, timeStep, getFuelConsumption());
    }
}
