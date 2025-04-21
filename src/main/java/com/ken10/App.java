package com.ken10;

import com.ken10.entities.CelestialBodies;
import com.ken10.entities.SolarSystem;
import com.ken10.entities.probe.FuelOptimizer;
import com.ken10.entities.probe.Probe;

import java.util.ArrayList;

import com.ken10.NumericalSolvers.RK4Solver;
import com.ken10.Other.Vector;

public class App {
    // public static void main(String[] args) {
    //     CelestialBodies earth = SolarSystem.CreatePlanets().get(2); // Beispiel: Erde
    //     CelestialBodies titan = SolarSystem.CreatePlanets().get(6); // Beispiel: Titan
    //     Probe probeTemplate = new Probe("TitanProbe", new Vector(0, 0, 0), new Vector(0, 0, 0), 50000);

    //     // beginning radius e.g. 6.4e6 (worldradius)
    //     Vector optimalVelocity = FuelOptimizer.optimizeInitialVelocity(probeTemplate, earth, titan, 6.4e6);

    //     System.out.println("Optimierte Startgeschwindigkeit: " + optimalVelocity);
    // }

    public static void main(String[] args) {

        // Defaults are set in FuelOptimizer.java but can also be overridden here
        double ALPHA = 1.0; // weight for distance
        double BETA = 1000;  // weight for fuel usage
        int MAX_ITERATIONS = 10;

        ArrayList<CelestialBodies> planetarySystem = SolarSystem.CreatePlanets();
        CelestialBodies earth = planetarySystem.get(2);  // Earth
        CelestialBodies titan = planetarySystem.get(6);  // Titan

        // Create a probe template
        Probe probeTemplate = new Probe("TitanProbe", new Vector(0, 0, 0), new Vector(0, 0, 0), 50000);

        // Run optimizer to find best initial velocity
        Vector optimalVelocity = FuelOptimizer.optimizeInitialVelocity(probeTemplate, earth, titan, 6.4e6, ALPHA, BETA, MAX_ITERATIONS);
        System.out.println("Optimierte Startgeschwindigkeit: " + optimalVelocity);

        // RUN MISSION CODE
        // Set up optimized probe and add to planetary system
        // Probe optimizedProbe = probeTemplate.setStartingProbe(earth, 6.4e6);
        // optimizedProbe.setVelocity(optimizedProbe.getVelocity().add(optimalVelocity));
        // optimizedProbe.setFuelLevel(10000);
        // optimizedProbe.setTargetBody(titan);

        // planetarySystem.add(optimizedProbe);

        // // Run RK4 simulation with optimized probe
        // double startTime = 0;
        // // double endTime = 31557600; // 1 year in seconds
        // double endTime = 2628000; // 
        // double stepSize = 2628000; // 1 month

        // RK4Solver rk4 = new RK4Solver(planetarySystem, startTime, endTime, stepSize);
        // rk4.solve();
    }

}
