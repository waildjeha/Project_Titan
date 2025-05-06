package com.ken10.Phase2.ProbeMission.ProbeMissionAli;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;

public class ProbeMission {

    private static final double PROBE_MASS = 50000; 
    private static final int STEP_SIZE_MINUTES = 5; 


    public void runMission() {
        ArrayList<CelestialBodies> solarSystem = SolarSystem.CreatePlanets();

        CelestialBodies earth = getBodyByName(solarSystem, "Earth");
        Vector earthPosition = earth.getPosition().add(new Vector(6371, 0, 0)); // surface position
        Vector earthVelocity = earth.getVelocity();

        double bestDistance = Double.MAX_VALUE;
        Vector bestVelocity = null;
        LocalDateTime bestTime = null;

        for (double vx = 0; vx <= 15; vx += 1) {
            for (double vy = 30; vy <= 50; vy += 2) {
                for (double vz = -10; vz <= 10; vz += 2) {
                    double magnitude = Math.sqrt(vx * vx + vy * vy + vz * vz);
                    if (magnitude > 60) continue;

                    Vector probeVelocity = earthVelocity.add(new Vector(vx, vy, vz));
                    Probe probe = new Probe("Probe", earthPosition, probeVelocity);
                    probe.setMass(PROBE_MASS);

                    ArrayList<CelestialBodies> systemCopy = SolarSystem.CreatePlanets();
                    systemCopy.add(probe);

                    LocalDateTime launchDate = LocalDateTime.of(2025, 4, 1, 0, 0);
                    LocalDateTime endDate = launchDate.plusMonths(12);

                    EphemerisLoader simulation = new EphemerisLoader(systemCopy, launchDate, endDate, STEP_SIZE_MINUTES);
                    simulation.solve();

                    double closestDistance = Double.MAX_VALUE;
                    LocalDateTime closestTime = null;

                    for (var entry : simulation.history.entrySet()) {
                        ArrayList<CelestialBodies> bodies = entry.getValue();
                        CelestialBodies titan = getBodyByName(bodies, "Titan");
                        CelestialBodies probeBody = getBodyByName(bodies, "Probe");

                        double distance = Vector.getDistance(titan.getPosition(), probeBody.getPosition());
                        if (distance < closestDistance) {
                            closestDistance = distance;
                            closestTime = entry.getKey();
                        }
                    }

                    System.out.printf("Test vx=%.2f, vy=%.2f, vz=%.2f | Magnitude=%.2f km/s --> Closest: %.2f km at %s\n",
                            vx, vy, vz, magnitude, closestDistance, closestTime);

                    if (closestDistance < bestDistance) {
                        bestDistance = closestDistance;
                        bestVelocity = new Vector(vx, vy, vz);
                        bestTime = closestTime;
                    }
                }
            }
        }

        System.out.printf("Best initial velocity offset: (vx=%.2f, vy=%.2f, vz=%.2f) with closest approach: %.2f km at %s\n",
                bestVelocity.getX(), bestVelocity.getY(), bestVelocity.getZ(), bestDistance, bestTime);
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
        ProbeMission mission = new ProbeMission();
        mission.runMission();
    }
}
