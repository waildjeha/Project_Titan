package com.ken10.Phase2.ProbeMission;

import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Probe;
import com.ken10.Phase2.SolarSystemModel.Vector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
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

    private final RK4Probe baseSimulation;
    private final Vector EARTH_POSITION;
    private final Hashtable<LocalDateTime, ArrayList<CelestialBodies>> planetHistory;

    // Hyper‑parameters for the adaptive step size
    private final double initialStep;
    private final double minStep;
    private final double enlargeFactor;
    private final double shrinkFactor;

    /**
     * Convenience constructor using sensible defaults.
     */
    public HillClimbing(RK4Probe simulation,
                                 Vector EARTH_POSITION,
                                 Hashtable<LocalDateTime, ArrayList<CelestialBodies>> planetHistory) {
        this(simulation, EARTH_POSITION, planetHistory,
                1e-3,   // initial step (km/s)
                1e-7,   // minimum step
                1.2,    // enlarge factor
                0.5);   // shrink factor
    }

    /**
     * Fully configurable constructor.
     */
    public HillClimbing(RK4Probe simulation,
                                 Vector EARTH_POSITION,
                                 Hashtable<LocalDateTime, ArrayList<CelestialBodies>> planetHistory,
                                 double initialStep,
                                 double minStep,
                                 double enlargeFactor,
                                 double shrinkFactor) {
        this.baseSimulation = simulation;
        this.EARTH_POSITION = EARTH_POSITION;
        this.planetHistory = planetHistory;
        this.initialStep = initialStep;
        this.minStep = minStep;
        this.enlargeFactor = enlargeFactor;
        this.shrinkFactor = shrinkFactor;
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
    public RK4Probe findOptimalVelocity() {
        RK4Probe bestSim = baseSimulation;
        double bestDistance = bestSim.getClosestDistance();
        double step = initialStep;

        while (step > minStep) {
            boolean improved = false;

            for (Vector neighbourVelocity : generateNeighbours(bestSim.getInitialProbe().getVelocity(), step)) {
                Probe probe = new Probe("dominik", EARTH_POSITION, neighbourVelocity);
                RK4Probe sim = new RK4Probe(probe, planetHistory, bestSim.getStepSizeMin());
                sim.solve();
                double d = sim.getClosestDistance();
                if (d < bestDistance) {
                    bestDistance = d;
                    bestSim = sim;
                    improved = true;
                    if(bestDistance <= 2575) return bestSim;
                    System.out.printf("NEW BEST %.3f km | step %.7f | %s%n",
                            bestDistance, step, bestSim.getClosestDistTime());

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
}

