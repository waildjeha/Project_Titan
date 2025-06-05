package com.ken10.landing;

import java.util.Random;

/**
 * Open-loop controller that finds optimal thrust schedule using random search
 * Based on the algorithm described in the manual
 */
public class OpenLoopController {
    private LandingSimulator simulator;
    private Random random;

    // Optimization parameters
    private int maxIterations = 1000000;
    private int scheduleSteps = 150;      // Number of thrust commands
    private double timeStep = 0.5;        // Time between commands (seconds)
    private double mutationRate = 0.1;    // Probability of mutating each thrust value
    private double mutationStrength = 2.0; // Strength of mutations

    public OpenLoopController() {
        this.simulator = new LandingSimulator();
        this.random = new Random();
    }

    public OpenLoopController(long seed) {
        this.simulator = new LandingSimulator();
        this.random = new Random(seed);
    }

    /**
     * Find optimal thrust schedule for given initial conditions
     */
    public ThrustSchedule optimize(LandingState initialState) {
        System.out.println("Starting optimization for initial state: " + initialState);
        System.out.println("Target: Land at (0,0) with minimal fuel consumption");
        System.out.println();

        ThrustSchedule bestSchedule = ThrustSchedule.generateRandom(scheduleSteps, timeStep, random);
        LandingResult bestResult = simulator.simulate(initialState, bestSchedule);
        double bestFitness = bestResult.getFitnessScore();

        // Track progress
        int successCount = 0;
        int generation = 0;

        for (int iteration = 0; iteration < maxIterations; iteration++) {
            ThrustSchedule candidate;

            if (bestSchedule == null || random.nextDouble() < 0.3) {
                // Generate completely random schedule (30% of the time)
                candidate = ThrustSchedule.generateRandom(scheduleSteps, timeStep, random);
            } else {
                // Mutate best schedule (70% of the time)
                candidate = bestSchedule.mutate(random, mutationRate, mutationStrength);
            }

            // Test the candidate
            LandingResult result = simulator.simulate(initialState, candidate);
            double fitness = result.getFitnessScore();

            // Check if this is the best so far
            if (fitness > bestFitness) {
                bestFitness = fitness;
                bestSchedule = candidate;
                bestResult = result;

                System.out.printf("Iteration %d: New best fitness %.1f - %s%n",
                        iteration, fitness, result.getLandingStatus());

                if (result.getLandingStatus() == LandingResult.LandingStatus.SUCCESS) {
                    successCount++;
                    System.out.printf("  SUCCESS! Fuel used: %.4f, Final position: (%.6f, %.6f)%n",
                            result.getFuelUsed(),
                            result.getFinalState().position.x,
                            result.getFinalState().position.y);
                }
            }

            // Print progress every 1000 iterations
            if ((iteration + 1) % 1000 == 0) {
                generation++;
                System.out.printf("Generation %d complete. Best fitness: %.1f, Successes: %d%n",
                        generation, bestFitness, successCount);
            }

            // Earlier termination if we found a very good solution
            if (fitness > 1100) { // Successful landing with good fuel efficiency
                System.out.println("Found excellent solution, terminating early.");
                break;
            }
        }

        System.out.println();
        System.out.println("=== OPTIMIZATION COMPLETE ===");
        System.out.println("Best result: " + bestResult);
        System.out.println("Total successful landings found: " + successCount);

        return bestSchedule;
    }

    /**
     * Test a specific thrust schedule
     */
    public LandingResult test(LandingState initialState, ThrustSchedule schedule) {
        return simulator.simulate(initialState, schedule);
    }

    // Configuration methods
    public void setMaxIterations(int maxIterations) {
        this.maxIterations = maxIterations;
    }

    public void setScheduleParameters(int steps, double timeStep) {
        this.scheduleSteps = steps;
        this.timeStep = timeStep;
    }

    public void setMutationParameters(double rate, double strength) {
        this.mutationRate = rate;
        this.mutationStrength = strength;
    }
}