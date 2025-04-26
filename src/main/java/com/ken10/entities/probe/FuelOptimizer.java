package com.ken10.entities.probe;

import com.ken10.Other.Vector;
import com.ken10.entities.CelestialBodies;
import com.ken10.entities.SolarSystem;
import com.ken10.NumericalSolvers.RK4Solver;

import java.util.ArrayList;

public class FuelOptimizer {
    private static final double DEFAULT_ALPHA = 1.0; // default weight for distance
    private static final double DEFAULT_BETA = 0.0;  // default weight for fuel usage
    private static final int DEFAULT_MAX_ITERATIONS = 100;

    // Base method with all parameters
    public static Vector optimizeInitialVelocity(Probe probeTemplate, CelestialBodies earth, 
        CelestialBodies target, double radius, double ALPHA, double BETA, int MAX_ITERATIONS, Vector initialGuess) {
        
        Vector currentVelocity = initialGuess;
        double currentCost = evaluateMission(probeTemplate, earth, target, radius, currentVelocity, ALPHA, BETA);
        double stepSize = 2.0; // Smaller initial step size for more precise search
        
        System.out.println("\n=== Starting Hill Climbing Optimization ===");
        System.out.println("Initial velocity: " + currentVelocity);
        System.out.println("Initial cost: " + currentCost);

        // Define fixed directions for systematic search
        Vector[] directions = {
            new Vector(1, 0, 0),  // +X
            new Vector(-1, 0, 0), // -X
            new Vector(0, 1, 0),  // +Y
            new Vector(0, -1, 0), // -Y
            new Vector(0, 0, 1),  // +Z
            new Vector(0, 0, -1)  // -Z
        };

        for (int i = 0; i < MAX_ITERATIONS; i++) {
            boolean improved = false;
            
            // Try all six directions
            for (Vector direction : directions) {
                Vector neighbor = currentVelocity.add(direction.multiply(stepSize));
                
                // Ensure velocity doesn't exceed 60 km/s
                if (neighbor.magnitude() > 60.0) {
                    neighbor = neighbor.normalize().multiply(60.0);
                }
                
                double neighborCost = evaluateMission(probeTemplate, earth, target, radius, neighbor, ALPHA, BETA);
                
                if (neighborCost < currentCost) {
                    currentVelocity = neighbor;
                    currentCost = neighborCost;
                    improved = true;
                    break;
                }
            }
            
            if (!improved) {
                stepSize *= 0.5;
                if (stepSize < 0.0001) {
                    break;
                }
            }
        }

        return currentVelocity;
    }

    private static double evaluateMission(Probe probeTemplate, CelestialBodies earth, CelestialBodies target, 
                                        double radius, Vector velocity, double ALPHA, double BETA) {
        Probe probe = probeTemplate.setStartingProbe(earth, radius);
        probe.setVelocity(velocity);
        probe.setTargetBody(target);

        ArrayList<CelestialBodies> system = SolarSystem.CreatePlanets();
        system.add(probe);

        double startTime = 0;
        double endTime = 31557600; // 1 year
        double stepSize = 86400; // 1 day steps

        RK4Solver rk4 = new RK4Solver(system, startTime, endTime, stepSize);
        double closestDistance = Double.MAX_VALUE;

        while (rk4.getCurrentTime() < endTime) {
            rk4.step();
            Vector relativePosition = probe.getRelativePosition(target);
            double distance = relativePosition.magnitude();
            closestDistance = Math.min(closestDistance, distance);
        }

        double fuelUsed = probeTemplate.getFuelLevel() - probe.getFuelLevel();
        return ALPHA * closestDistance + BETA * fuelUsed;
    }

    // Overloaded methods with default parameters
    public static Vector optimizeInitialVelocity(Probe probeTemplate, CelestialBodies earth, 
        CelestialBodies target, double radius, double ALPHA, double BETA, int MAX_ITERATIONS) {
        Vector earthToTitan = target.getPosition().subtract(earth.getPosition());
        Vector initialGuess = earthToTitan.normalize().multiply(24.0);
        return optimizeInitialVelocity(probeTemplate, earth, target, radius, ALPHA, BETA, MAX_ITERATIONS, initialGuess);
    }

    public static Vector optimizeInitialVelocity(Probe probeTemplate, CelestialBodies earth, 
        CelestialBodies target, double radius, double ALPHA, double BETA) {
        return optimizeInitialVelocity(probeTemplate, earth, target, radius, ALPHA, BETA, DEFAULT_MAX_ITERATIONS);
    }

    public static Vector optimizeInitialVelocity(Probe probeTemplate, CelestialBodies earth, 
        CelestialBodies target, double radius) {
        return optimizeInitialVelocity(probeTemplate, earth, target, radius, DEFAULT_ALPHA, DEFAULT_BETA);
    }
}
