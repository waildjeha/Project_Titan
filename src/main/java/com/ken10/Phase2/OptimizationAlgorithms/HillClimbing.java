package com.ken10.Phase2.OptimizationAlgorithms;

import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.Earth;
import com.ken10.Phase2.SolarSystemModel.Probe;
import com.ken10.Phase2.SolarSystemModel.Vector;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Adaptive hill‑climbing optimiser for the probe’s initial velocity.
 * <p>
 * The algorithm starts with an initial step size (a.k.a. learning rate) and
 * generates six neighbour candidates (±step on each axis). If any neighbour
 * improves the closest‑approach distance, it becomes the new current best and
 * the step is enlarged (bold‑driver heuristic). Otherwise the step is shrunk.
 * The search terminates when the step becomes smaller than {@code minStep}.
 */
public class HillClimbing {

    private RK4Probe bestSimulation;
    private final LaunchData launchData;

    // Hyper‑parameters for the adaptive step size
    private final double initialStep;
    private final double minStep;
    private final double enlargeFactor;
    private final double shrinkFactor;

    /**
     * Convenience constructor using sensible defaults.
     */
    public HillClimbing(RK4Probe simulation, LaunchData launchData) {
        this.bestSimulation = simulation;
        this.launchData = launchData;
        this.initialStep =     1;   // initial step (km/s)
        this.minStep = 1e-7;   // minimum step
        this.enlargeFactor =   1.2;    // enlarge factor
        this.shrinkFactor =  0.5;   // shrink factor
    }

    /**
     * Fully configurable constructor.
     */
    public HillClimbing(RK4Probe simulation,
                                 LaunchData launchData,
                                 double initialStep,
                                 double minStep,
                                 double enlargeFactor,
                                 double shrinkFactor) {
        this.bestSimulation = simulation;
        this.launchData = launchData;
        this.initialStep = initialStep;
        this.minStep = minStep;
        this.enlargeFactor = enlargeFactor;
        this.shrinkFactor = shrinkFactor;
    }

    public void solve() {
        bestSimulation = findOptimalVelocity();
        System.out.println(bestSimulation);
    }

    public RK4Probe getBestSimulation() {
        return bestSimulation;
    }
    public void setBestVelocity(RK4Probe bestSimulation){
        this.bestSimulation = bestSimulation;
    }
    /**
     * Runs an adaptive hill‑climbing search over the probe's initial velocity
     * vector.  The step size (“learning rate”) is increased each time an
     * improvement is found and decreased when no neighbour beats the current
     * best.  The loop terminates when the step size falls below
     * {@code minStep}.
     *
     * @return a simulation whose closest approach distance is the best found.
     */
    private RK4Probe findOptimalVelocity() {
        RK4Probe bestSim = bestSimulation;
        double bestDistance = bestSim.getError();
        double step = initialStep;

        while (step > minStep) {
            boolean improved = false;

            for (Vector neighbourVelocity : generateNeighbours(bestSim.getInitialProbe().getVelocity(), step)) {

                Probe probe = new Probe("probe", this.launchData.getInitialPosition(), neighbourVelocity);
                RK4Probe sim = new RK4Probe(probe, this.launchData.getHistoryPlanets(), bestSim.getStepSize(), this.launchData.getLaunchTime(), this.launchData.getEndTime(), this.launchData.getDestination(), this.launchData.getLaunchPlanet());
                sim.solve();
                double d = sim.getError();
                if (d < bestDistance) {
                    bestDistance = d;
                    bestSim = sim;
                    improved = true;
                    if(bestDistance <= 2575) return bestSim;
//                    System.out.printf("NEW BEST %.3f km | step %.7f | %s%n",
//                            bestDistance, step, bestSim.getClosestDistTime());
                    long seconds = Duration.between(EvolutionAlgorithm.experiment_start_time, LocalDateTime.now()).toSeconds();
                    System.out.println("It took " + seconds + " to get this closest distance: " + bestDistance);

                    break;      // explore around the new best solution
                }
            }
            // Adapt the step size (“learning rate”)
            step = improved ? step * enlargeFactor : step * shrinkFactor;
        }

        return bestSim;
    }

    /**
     * Generates the six axial neighbours of the given velocity vector using
     * the supplied step size.
     */
    private List<Vector> generateNeighbours(Vector v, double step) {
        List<Vector> neighbours = new ArrayList<>(6);
        neighbours.add(v.addX(step));
        neighbours.add(v.addX(-step));
        neighbours.add(v.addY(step));
        neighbours.add(v.addY(-step));
        neighbours.add(v.addZ(step));
        neighbours.add(v.addZ(-step));
        return neighbours;
    }

    public static void main(String[] args) {
        var probe = new Probe("probe",new Vector(-1.4664541859104577E8, -2.8949304626334388E7, 2241.9186033698497),new Vector (63.289186302398114, -33.49000052271595, -15.073058267640539));
        EphemerisLoader eph = new EphemerisLoader(1);
        eph.solve();
        LocalDateTime launchTime = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        RK4Probe bestSimulation = new RK4Probe(probe, eph.history, 2, launchTime, launchTime.plusYears(1), BodyID.TITAN, BodyID.EARTH);
        bestSimulation.solve();
        System.out.println(bestSimulation);
        LaunchData launchData = new LaunchData(BodyID.TITAN, BodyID.EARTH, Earth.EARTH_INITIAL_POSITION.addX(6370), launchTime, launchTime.plusYears(1));
        HillClimbing hc = new HillClimbing(bestSimulation, launchData);
        hc.solve();
        System.out.println(hc.getBestSimulation());
    }
}

