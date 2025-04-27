package com.ken10.ProbeMission;

import com.ken10.entities.CelestialBodies;
import com.ken10.entities.SolarSystem;
import com.ken10.entities.probe.FuelOptimizer;
import com.ken10.entities.probe.Probe;

import java.util.ArrayList;

import com.ken10.NumericalSolvers.RK4Solver;
import com.ken10.Other.Vector;

public class TitanProbeMission {
    public static void main(String[] args) {
        System.out.println("Starting Titan Probe Mission Simulation");
        
        // Load the solar system
        ArrayList<CelestialBodies> solarSystem = SolarSystem.CreatePlanets();
        CelestialBodies earth = solarSystem.get(3);  // Earth is at index 3 based on SolarSystem.java
        CelestialBodies titan = solarSystem.get(8);  // Titan is at index 8
        
        System.out.println("Earth position: " + earth.getPosition());
        System.out.println("Titan position: " + titan.getPosition());
        
        // Earth radius in km
        double earthRadius = 6370.0;
        
        // Create the probe template with initial mass (kg)
        Probe probe = new Probe("TitanProbe", new Vector(0, 0, 0), new Vector(0, 0, 0), 50000);
        
        // Set optimization parameters
        double ALPHA = 1.0;      // weight for distance
        double BETA = 0.01;    // very low weight for fuel usage
        int MAX_ITERATIONS = 100; // fewer iterations with systematic search

        // Calculate initial velocity more precisely
        Vector earthToTitan = titan.getPosition().subtract(earth.getPosition());
        double distanceToTitan = earthToTitan.magnitude();
        Vector initialGuessDirection = earthToTitan.normalize();
        
        // Calculate escape velocity (11.2 km/s) plus additional speed
        double escapeVelocity = 11.2;
        double initialSpeed = escapeVelocity * 3.5; // Try 39.2 km/s
        Vector initialGuess = initialGuessDirection.multiply(initialSpeed);
        
        System.out.println("Initial velocity guess: " + initialGuess);
        System.out.println("Finding optimal initial velocity...");
        
        // Run the optimizer with the initial guess
        Vector optimalVelocity = FuelOptimizer.optimizeInitialVelocity(
            probe, earth, titan, earthRadius, ALPHA, BETA, MAX_ITERATIONS, initialGuess);
        
        System.out.println("Optimal initial velocity: " + optimalVelocity);
        
        // Verify the velocity doesn't exceed 60 km/s
        double velocityMagnitude = optimalVelocity.magnitude();
        System.out.println("Velocity magnitude: " + velocityMagnitude + " km/s");
        
        if (velocityMagnitude > 60.0) {
            System.out.println("WARNING: Velocity exceeds maximum allowed (60 km/s)");
            // Scale down velocity if needed
            double scaleFactor = 60.0 / velocityMagnitude;
            optimalVelocity = optimalVelocity.multiply(scaleFactor);
        }
        
        // Set up optimized probe and add to planetary system
//        Probe missionProbe = probe.setStartingProbe(earth, earthRadius,ve);
    }
}