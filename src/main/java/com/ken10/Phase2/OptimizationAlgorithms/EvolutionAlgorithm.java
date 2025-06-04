package com.ken10.Phase2.OptimizationAlgorithms;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
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

public class EvolutionAlgorithm {
    private static final int POPULATION_SIZE = 50;
    private static final int MAX_GENERATIONS = 100;
    private static final double CROSSOVER_RATE = 0.9;
    private static final double DIFFERENTIAL_WEIGHT = 0.5;
    private static final double PROBE_MASS = 50000;
    private static final int STEP_SIZE_MINUTES = 5;
    private static final double MAX_VELOCITY = 60.0;

    private final Random RANDOM = new Random();
    private final Vector LAUNCH_POSITION;
    private final Vector SURFACE_PLANET_VELOCITY;

    private final LaunchData launchData;
    private RK4Probe evolutionAlgorithmInitialGuess;

    public EvolutionAlgorithm(LaunchData launchData) {
        this.launchData = launchData;

        this.LAUNCH_POSITION = launchData.getInitialPosition();
        this.SURFACE_PLANET_VELOCITY = loadSurfaceVelocity(launchData.getIsSurface());
    }

    private Vector loadSurfaceVelocity(BodyID body) {
        if (body.equals(BodyID.SPACESHIP)) return new Vector(0,0,0);
        ArrayList<CelestialBodies> solarSystem = SolarSystem.createPlanets();
        return solarSystem.get(body.index()).getVelocity();
    }

//    private Hashtable<LocalDateTime, ArrayList<CelestialBodies>> loadHistory(int duration) {
//        EphemerisLoader ephemerisLoader = new EphemerisLoader(1, duration);
//        ephemerisLoader.solve();
//        return ephemerisLoader.history;
//    }

    /**
     * Initializes Vectors in population and distances to titan for each.
     * Only keeps the best Velocity and distance in that population.
     * Randomly selects 3 vectors and combines using mutation equation.
     * Scales using differential weight.
     * Uses crossover operation to find the trial vector.
     * Only returns if trial vector improves distance.
     * @return trial vector.
     */

    public void solve(){
        optimizeTrajectory();
    }

    public RK4Probe getEvolutionAlgorithmInitialGuess() {
        return evolutionAlgorithmInitialGuess;
    }

    private void optimizeTrajectory() {
        List<Vector> population = initializePopulation();
        List<Double> distances = evaluatePopulation(population);

//        FileWriter writer = new FileWriter("GA_results.csv");
//        writer.write("Generation,Velocity_X,Velocity_Y,Velocity_Z,Distance_km\n");

//        Vector bestVector = null;
        double bestDistance = Double.MAX_VALUE;
        RK4Probe bestSimulation = null;

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
                RK4Probe trailSimulation = evaluateTrajectory(trial);
                double trialDistance = trailSimulation.getClosestDistance();

                if (trialDistance < distances.get(i)) {
                    population.set(i, trial);
                    distances.set(i, trialDistance);

                    if (trialDistance < bestDistance) {
                        bestDistance = trialDistance;
                        bestSimulation = trailSimulation;
                        System.out.println("New best distance: " + bestDistance + ", date: " + bestSimulation.getClosestDistTime());
                        System.out.println("Generation : " + gen);
                    }
                    if(bestDistance<=1E6) {
                        System.out.println("Hill climbing takes over");
//                        HillClimbing hillClimbing = new HillClimbing(bestSimulation, EARTH_POSITION, planetHistory);
                        evolutionAlgorithmInitialGuess = bestSimulation;
                        return;
//                        return hillClimbing.findOptimalVelocity();
                    }
                }
            }
        }
//        return bestSimulation;
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
            distances.add(evaluateTrajectory(v).getClosestDistance());
        }
        return distances;
    }

    /**
     *
     * @param velocity
     * @return
     */

    private RK4Probe evaluateTrajectory(Vector velocity) {
        RK4Probe simulation = new RK4Probe(new Probe("dominik", LAUNCH_POSITION, SURFACE_PLANET_VELOCITY.add(velocity)), launchData.getHistoryPlanets(), 2);
        simulation.solve();
        return simulation;
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
                trial = switch (j) {
                    case 0 -> new Vector(mutant.getX(), target.getY(), target.getZ());
                    case 1 -> new Vector(trial.getX(), mutant.getY(), target.getZ());
                    case 2 -> new Vector(trial.getX(), trial.getY(), mutant.getZ());
                    default -> trial;
                };
            } else{
                trial = switch (j) {
                    case 0 -> new Vector(target.getX(), trial.getY(), trial.getZ());
                    case 1 -> new Vector(trial.getX(), target.getY(), trial.getZ());
                    case 2 -> new Vector(trial.getX(), trial.getY(), target.getZ());
                    default -> trial;
                };
            }
        }

        return trial;
    }

//    public static void main(String[] args) throws IOException {
//        EvolutionAlgorithm optimizer = new EvolutionAlgorithm();
//        RK4Probe bestSimulation = optimizer.optimizeTrajectory();
//        System.out.printf("Final result" + bestSimulation.toString());
//    }
    // Best Velocity at current parameters = (55.941793, -2.277140, -11.512891)
    // mag = 57.15956814 km/s
    // closest distance = 4355.21 km from titan.
    // takes a few hours at current param.
}
//Final result----------------------------------------------------------
//Initial probe position and velocity: (-1.4664541859104577E8, -2.8949304626334388E7, 2241.9186033698497) (63.116478338136574, -26.26627885747304, 15.895966791164073)
//Velocity magnitude relative to earth: 60.04618330310422
//Closest Distance to Titan: 2573.5335720234507
//Date of closest approach: 2026-03-29T01:16