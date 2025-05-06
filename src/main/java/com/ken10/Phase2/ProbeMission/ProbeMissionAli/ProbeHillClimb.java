package com.ken10.Phase2.ProbeMission.ProbeMissionAli;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Map;

public class ProbeHillClimb {

    private static final double PROBE_MASS = 50000; 
    private static final int STEP_SIZE_MINUTES = 5; 
    private static final double INITIAL_STEP = 1.0; 
    private static final double MIN_STEP = 0.000001; // precision

    private static final double LANDING_THRESHOLD_KM = 2575.0; // Titan's radius 

    private static final Vector INITIAL_VECTOR = new Vector(6.670000, 33.440869, 0.083851);

    public void runHillClimb() {
        Vector earthPosition = getEarthPositionAtLaunch();
        Vector earthVelocity = getEarthVelocityAtLaunch();

        Vector currentVector = INITIAL_VECTOR;
        Result currentResult = simulate(earthPosition, earthVelocity, currentVector);

        Vector bestOverallVector = currentVector;
        Result bestOverallResult = currentResult;

        double step = INITIAL_STEP;
        LocalDateTime startTime = LocalDateTime.now();
        boolean landed = false;

        while (step >= MIN_STEP) {
            boolean improved = false;
            Vector bestNeighbor = null;
            Result bestNeighborResult = currentResult;

            double[] shifts = {-step, 0, step};

            for (double dx : shifts) {
                for (double dy : shifts) {
                    for (double dz : shifts) {
                        Vector neighborVector = new Vector(
                                currentVector.getX() + dx,
                                currentVector.getY() + dy,
                                currentVector.getZ() + dz
                        );

                        double magnitude = neighborVector.magnitude();
                        if (magnitude > 60) continue;

                        Result result = simulate(earthPosition, earthVelocity, neighborVector);

                        System.out.printf("Test offset vx=%.6f, vy=%.6f, vz=%.6f | Magnitude=%.6f km/s --> Closest: %.2f km at %s\n",
                                neighborVector.getX(), neighborVector.getY(), neighborVector.getZ(), magnitude, result.closestDistance, result.closestTime);

                        if (result.closestDistance < bestNeighborResult.closestDistance - 0.001) {
                            bestNeighbor = neighborVector;
                            bestNeighborResult = result;
                        }

                        if (result.closestDistance < bestOverallResult.closestDistance - 0.001) {
                            bestOverallVector = neighborVector;
                            bestOverallResult = result;
                        }
                    }
                }
            }

            if (bestNeighbor != null) {
                currentVector = bestNeighbor;
                currentResult = bestNeighborResult;
                improved = true;
                System.out.printf("Improved to: vx=%.6f, vy=%.6f, vz=%.6f --> Closest: %.2f km at %s\n",
                        currentVector.getX(), currentVector.getY(), currentVector.getZ(), currentResult.closestDistance, currentResult.closestTime);
            }

            if (bestOverallResult.closestDistance <= LANDING_THRESHOLD_KM) {
                System.out.printf("Landing achieved! Best offset: (vx=%.6f, vy=%.6f, vz=%.6f) with closest approach: %.2f km at %s\n",
                        bestOverallVector.getX(), bestOverallVector.getY(), bestOverallVector.getZ(), bestOverallResult.closestDistance, bestOverallResult.closestTime);
                landed = true;
                break;
            }

            if (!improved) {
                if (step <= MIN_STEP * 2) {
                    System.out.println("No further improvements found and minimum step reached. Optimization stopping.");
                    break;
                }
                step /= 2.0;
            }
        }

        if (!landed) {
            System.out.printf("Best overall result: vx=%.6f, vy=%.6f, vz=%.6f --> Closest: %.2f km at %s\n",
                    bestOverallVector.getX(), bestOverallVector.getY(), bestOverallVector.getZ(), bestOverallResult.closestDistance, bestOverallResult.closestTime);
        }

        LocalDateTime endTime = LocalDateTime.now();
        Duration duration = Duration.between(startTime, endTime);

        System.out.printf("Optimization completed in: %d minutes and %d seconds.\n",
                duration.toMinutesPart(), duration.toSecondsPart());
    }

    private Vector getEarthPositionAtLaunch() {
        ArrayList<CelestialBodies> solarSystem = SolarSystem.CreatePlanets();
        CelestialBodies earth = getBodyByName(solarSystem, "Earth");
        return earth.getPosition().add(new Vector(6371, 0, 0));
    }

    private Vector getEarthVelocityAtLaunch() {
        ArrayList<CelestialBodies> solarSystem = SolarSystem.CreatePlanets();
        CelestialBodies earth = getBodyByName(solarSystem, "Earth");
        return earth.getVelocity();
    }

    private Result simulate(Vector earthPosition, Vector earthVelocity, Vector offset) {
        Vector probeVelocity = earthVelocity.add(offset);
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

        for (Map.Entry<LocalDateTime, ArrayList<CelestialBodies>> entry : simulation.history.entrySet()) {
            ArrayList<CelestialBodies> bodies = entry.getValue();
            CelestialBodies titan = getBodyByName(bodies, "Titan");
            CelestialBodies probeBody = getBodyByName(bodies, "Probe");

            double distance = Vector.getDistance(titan.getPosition(), probeBody.getPosition());
            if (distance < closestDistance) {
                closestDistance = distance;
                closestTime = entry.getKey();
            }
        }

        return new Result(closestDistance, closestTime);
    }

    private CelestialBodies getBodyByName(ArrayList<CelestialBodies> bodies, String name) {
        for (CelestialBodies body : bodies) {
            if (body.getName().equalsIgnoreCase(name)) {
                return body;
            }
        }
        throw new IllegalArgumentException("Body not found: " + name);
    }

    private static class Result {
        double closestDistance;
        LocalDateTime closestTime;

        Result(double closestDistance, LocalDateTime closestTime) {
            this.closestDistance = closestDistance;
            this.closestTime = closestTime;
        }
    }

    public static void main(String[] args) {
        ProbeHillClimb optimizer = new ProbeHillClimb();
        optimizer.runHillClimb();
    }
}
