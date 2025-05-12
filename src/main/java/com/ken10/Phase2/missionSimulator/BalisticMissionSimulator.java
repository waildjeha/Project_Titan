package com.ken10.Phase2.missionSimulator;

import com.ken10.Phase2.entities.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;
import com.ken10.Phase2.newtonCalculations.Vector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

/**
 * Runs a purely balistic mission to titan.
 * uses Differential evolution to find a velocity to use.
 * returns mission end results.
 */
public class BalisticMissionSimulator {
    private static final double PROBE_MASS = 50000;
    private static final int STEP_SIZE_MINUTES = 5;

    public void runMission(Vector optimizedVelocity) {
        ArrayList<CelestialBodies> solarSystem = SolarSystem.CreatePlanets();
        CelestialBodies earth = getBodyByName(solarSystem, "Earth");
        Vector earthPosition = earth.getPosition().add(new Vector(6371, 0, 0));
        Vector earthVelocity = earth.getVelocity();

        Vector probeVelocity = earthVelocity.add(optimizedVelocity);
        Probe probe = new Probe("Probe", earthPosition, probeVelocity);
        probe.setMass(PROBE_MASS);

        ArrayList<CelestialBodies> systemCopy = new ArrayList<>(solarSystem);
        systemCopy.add(probe);

        LocalDateTime launchDate = LocalDateTime.of(2025, 4, 1, 0, 0);
        LocalDateTime endDate = launchDate.plusMonths(12);

        EphemerisLoader simulation = new EphemerisLoader(systemCopy, launchDate, endDate, STEP_SIZE_MINUTES);
        simulation.solve();

        double closestDistance = Double.MAX_VALUE;
        LocalDateTime closestTime = null;
        Vector closestPosition = null;

        for (Map.Entry<LocalDateTime, ArrayList<CelestialBodies>> entry : simulation.history.entrySet()) {
            ArrayList<CelestialBodies> bodies = entry.getValue();
            CelestialBodies titan = getBodyByName(bodies, "Titan");
            CelestialBodies probeBody = getBodyByName(bodies, "Probe");

            double distance = Vector.getDistance(titan.getPosition(), probeBody.getPosition());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTime = entry.getKey();
                closestPosition = probeBody.getPosition();
            }
        }

        System.out.println("\nMission Results:");
        System.out.printf("Launch Date: %s\n", launchDate);
        System.out.printf("Initial Velocity: (%.6f, %.6f, %.6f) km/s\n",
                optimizedVelocity.getX(), optimizedVelocity.getY(), optimizedVelocity.getZ());
        System.out.printf("Initial Velocity Magnitude: %.6f km/s\n", optimizedVelocity.magnitude());
        System.out.printf("Closest Approach to Titan: %.2f km\n", closestDistance);
        System.out.printf("Time of Closest Approach: %s\n", closestTime);
        System.out.printf("Days from Launch to Closest Approach: %d\n",
                java.time.Duration.between(launchDate, closestTime).toDays());
    }

    private CelestialBodies getBodyByName(ArrayList<CelestialBodies> bodies, String name) {
        for (CelestialBodies body : bodies) {
            if (body.getName().equalsIgnoreCase(name)) {
                return body;
            }
        }
        throw new IllegalArgumentException("Body not found: " + name);
    }

    public static void main(String[] args) {
        Vector bestVelocity = new Vector(51.656963, -2.127366, -12.546660);

        BalisticMissionSimulator runner = new BalisticMissionSimulator();
        runner.runMission(bestVelocity);
    }
}