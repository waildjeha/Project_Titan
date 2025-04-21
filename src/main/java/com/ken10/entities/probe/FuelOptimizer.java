package com.ken10.entities.probe;

import com.ken10.Other.Vector;
import com.ken10.entities.CelestialBodies;
import com.ken10.entities.SolarSystem;
import com.ken10.NumericalSolvers.RK4Solver;

import java.util.ArrayList;
import java.util.Random;

public class FuelOptimizer {
    private static final double DEFAULT_ALPHA = 1.0; // default weight for distance
    private static final double DEFAULT_BETA = 0.0;  // default weight for fuel usage
    private static final int DEFAULT_MAX_ITERATIONS = 100;

    /////
    //Handle defaul t values for ALPHA, BETA, and MAX_ITERATIONS
    public static Vector optimizeInitialVelocity(Probe probeTemplate, CelestialBodies earth, CelestialBodies target, double radius) {
        return optimizeInitialVelocity(probeTemplate, earth, target, radius, DEFAULT_ALPHA, DEFAULT_BETA, DEFAULT_MAX_ITERATIONS);
    }

    public static Vector optimizeInitialVelocity(Probe probeTemplate, CelestialBodies earth, CelestialBodies target, double radius, double ALPHA, double BETA) {
        return optimizeInitialVelocity(probeTemplate, earth, target, radius, ALPHA, BETA, DEFAULT_MAX_ITERATIONS);
    }
    ////

    public static Vector optimizeInitialVelocity(Probe probeTemplate, CelestialBodies earth, CelestialBodies target, double radius, double ALPHA, double BETA, int MAX_ITERATIONS) {
        Random random = new Random();

        double bestCost = Double.MAX_VALUE;
        Vector bestVelocity = null;

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            // Generate a random velocity offset
            Vector candidateVel = new Vector(
                random.nextDouble() * 1000 - 500,
                random.nextDouble() * 1000 - 500,
                random.nextDouble() * 1000 - 500
            );

            // Set up probe starting state
            Probe probe = probeTemplate.setStartingProbe(earth, radius);
            probe.setFuelLevel(10000);
            Vector startVel = probe.getVelocity().add(candidateVel);
            probe.setVelocity(startVel);
            probe.setTargetBody(target);

            // Combine into planetary system
            ArrayList<CelestialBodies> system = SolarSystem.CreatePlanets();
            system.add(probe);

            // Simulate with RK4
            double startTime = 0;
            double endTime = 1000000; // simulate ~11.5 days
            double stepSize = 10000;

            RK4Solver rk4 = new RK4Solver(system, startTime, endTime, stepSize);
            double currentTime = startTime;
            while (currentTime < endTime) {
                rk4.step();
                currentTime += stepSize;
            }

            // After simulation, get the updated probe
            Probe simulatedProbe = null;
            for (CelestialBodies body : system) {
                if (body instanceof Probe) {
                    simulatedProbe = (Probe) body;
                    break;
                }
            }

            if (simulatedProbe == null) continue;

            // Cost: distance to target after simulation
            double distance = simulatedProbe.getRelativePosition(target).magnitude();
            double fuelUsed = probeTemplate.getFuelLevel() - simulatedProbe.getFuelLevel();
            double cost = ALPHA * distance + BETA * fuelUsed;

            if (cost < bestCost) {
                bestCost = cost;
                bestVelocity = candidateVel;
                System.out.printf("New best [%d]: Cost = %.2f | Velocity = %s%n", i, cost, candidateVel);
            }
        }

        return bestVelocity;
    }
}
