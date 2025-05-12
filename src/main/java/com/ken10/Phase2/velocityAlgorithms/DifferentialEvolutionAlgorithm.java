package com.ken10.Phase2.velocityAlgorithms;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Uses the differential evolution algorithm to find the most optimal initial velocity.
 * This algorithm was used as it is flexible in large spaces such as the solar system.
 * Uses basic method of:
 * 1. Initialization
 * 2. mutation
 * 3. crossover
 * 4. selection
 */

public class DifferentialEvolutionAlgorithm {
    private static final int POPULATION_SIZE = 50;
    private static final int MAX_GENERATIONS = 100;
    private static final double CROSSOVER_RATE = 0.9;
    private static final double DIFFERENTIAL_WEIGHT = 0.5;
    private static final double PROBE_MASS = 50000;
    private static final int STEP_SIZE_MINUTES = 5;
    private static final double MAX_VELOCITY = 60.0;

    private final Random RANDOM = new Random();
    private final Vector EARTH_POSITION;
    private final Vector EARTH_VELOCITY;

    public DifferentialEvolutionAlgorithm() {
        ArrayList<CelestialBodies> solarSystem = SolarSystem.CreatePlanets();
        CelestialBodies earth = getBodyByName(solarSystem, "Earth");
        this.EARTH_POSITION = earth.getPosition().add(new Vector(6371, 0, 0));
        this.EARTH_VELOCITY = earth.getVelocity();
    }

    /**
     * Initializes Vectors in population and distances to titan for each.
     * Only keeps the best Velocity and distance in that population.
     * Randomly selects 3 vectors and combines using mutation equation.
     * Scales using differential weight.
     * Uses crossover operation to find the trial vector.
     * Only returns if trial vector improves distance.
     * @return trial vector.
     */

    public Vector optimizeTrajectory() throws IOException {
        List<Vector> population = initializePopulation();
        List<Double> distances = evaluatePopulation(population);

        FileWriter writer = new FileWriter("GA_results.csv");
        writer.write("Generation,Velocity_X,Velocity_Y,Velocity_Z,Distance_km\n");

        Vector bestVector = null;
        double bestDistance = Double.MAX_VALUE;

        for (int gen = 0; gen < MAX_GENERATIONS; gen++) {
            for (int i = 0; i < POPULATION_SIZE; i++) {
                Vector a = getRandomVector(population, i);
                Vector b = getRandomVector(population, i);
                Vector c = getRandomVector(population, i);

                Vector mutant = a.add(b.subtract(c).multiply(DIFFERENTIAL_WEIGHT));

                if (mutant.magnitude() > MAX_VELOCITY) {
                    mutant = mutant.normalize().multiply(MAX_VELOCITY);
                }
                Vector trial = crossover(population.get(i), mutant);

                double trialDistance = evaluateTrajectory(trial);
                if (trialDistance < distances.get(i)) {
                    population.set(i, trial);
                    distances.set(i, trialDistance);

                    if (trialDistance < bestDistance) {
                        bestVector = trial;
                        bestDistance = trialDistance;
                        System.out.printf("Generation %d: New best distance = %.2f km\n",
                                gen, bestDistance);
                        writeCSV(gen, bestVector, bestDistance, writer);
                    }

                }
            }
        } writer.close();

        return bestVector;
    }

    /**
     * Creates a list of random Vectors that are below 60km/s dependent on the population size.
     * Does this by randomizing a magnitude and an angle.
     *
     * @return list of random Velocity Vectors.
     */

    private List<Vector> initializePopulation() {
        List<Vector> population = new ArrayList<>();
        for (int i = 0; i < POPULATION_SIZE; i++) {
            double magnitude = RANDOM.nextDouble() * MAX_VELOCITY;
            double theta = RANDOM.nextDouble() * 2 * Math.PI;
            double phi = RANDOM.nextDouble() * Math.PI;

            double x = magnitude * Math.sin(phi) * Math.cos(theta);
            double y = magnitude * Math.sin(phi) * Math.sin(theta);
            double z = magnitude * Math.cos(phi);

            population.add(new Vector(x, y, z));
        }
        return population;
    }

    /**
     * Evaluates each vector in the population.
     * @param population Vectors in population.
     * @return distance to titan.
     */

    private List<Double> evaluatePopulation(List<Vector> population) {
        List<Double> distances = new ArrayList<>();
        for (Vector v : population) {
            distances.add(evaluateTrajectory(v));
        }
        return distances;
    }

    /**
     *
     * @param velocity
     * @return
     */

    private double evaluateTrajectory(Vector velocity) {
        Vector probeVelocity = EARTH_VELOCITY.add(velocity);
        Probe probe = new Probe("Probe", EARTH_POSITION, probeVelocity);
        probe.setMass(PROBE_MASS);

        ArrayList<CelestialBodies> systemCopy = SolarSystem.CreatePlanets();
        systemCopy.add(probe);

        LocalDateTime launchDate = LocalDateTime.of(2025, 4, 1, 0, 0);
        LocalDateTime endDate = launchDate.plusMonths(12);

        EphemerisLoader simulation = new EphemerisLoader(systemCopy, launchDate, endDate, STEP_SIZE_MINUTES);
        simulation.solve();

        double closestDistance = Double.MAX_VALUE;

        for (var entry : simulation.history.entrySet()) {
            ArrayList<CelestialBodies> bodies = entry.getValue();
            CelestialBodies titan = getBodyByName(bodies, "Titan");
            CelestialBodies probeBody = getBodyByName(bodies, "Probe");

            double distance = Vector.getDistance(titan.getPosition(), probeBody.getPosition());
            closestDistance = Math.min(closestDistance, distance);
        }

        return closestDistance;
    }

    /**
     *
     * @param population
     * @param excludeIndex
     * @return
     */

    private Vector getRandomVector(List<Vector> population, int excludeIndex) {
        int index;
        do {
            index = RANDOM.nextInt(POPULATION_SIZE);
        } while (index == excludeIndex);
        return population.get(index);
    }

    /**
     *
     * @param target
     * @param mutant
     * @return
     */

    private Vector crossover(Vector target, Vector mutant) {
        Vector trial = new Vector(0, 0, 0);

        for (int j = 0; j < 3; j++) {
            if (RANDOM.nextDouble() < CROSSOVER_RATE) {
                switch (j) {
                    case 0: trial = new Vector(mutant.getX(), target.getY(), target.getZ()); break;
                    case 1: trial = new Vector(trial.getX(), mutant.getY(), target.getZ()); break;
                    case 2: trial = new Vector(trial.getX(), trial.getY(), mutant.getZ()); break;
                }
            } else{
                switch (j) {
                    case 0: trial = new Vector(target.getX(), trial.getY(), trial.getZ()); break;
                    case 1: trial = new Vector(trial.getX(), target.getY(), trial.getZ()); break;
                    case 2: trial = new Vector(trial.getX(), trial.getY(), target.getZ()); break;
                }
            }
        }

        return trial;
    }

    private CelestialBodies getBodyByName(ArrayList<CelestialBodies> bodies, String name) {
        for (CelestialBodies body : bodies) {
            if (body.getName().equalsIgnoreCase(name)) {
                return body;
            }
        }
        throw new IllegalArgumentException("Body not found: " + name);
    }


    private void writeCSV(int generation, Vector velocity, double distance, FileWriter writer) throws IOException {
        writer.write(String.format("%d,%.6f,%.6f,%.6f,%.2f\n", generation, velocity.getX(), velocity.getY(), velocity.getZ(), distance));
    }
    public static void main(String[] args) throws IOException {
        DifferentialEvolutionAlgorithm optimizer = new DifferentialEvolutionAlgorithm();
        Vector bestVelocity = optimizer.optimizeTrajectory();
        System.out.printf("Best velocity found: (%.6f, %.6f, %.6f) km/s\n",
                bestVelocity.getX(), bestVelocity.getY(), bestVelocity.getZ());
    }
    // Best Velocity at current parameters = (55.941793, -2.277140, -11.512891)
    // mag = 57.15956814 km/s
    // closest distance = 4355.21 km from titan.
    // takes a few hours at current param.
}