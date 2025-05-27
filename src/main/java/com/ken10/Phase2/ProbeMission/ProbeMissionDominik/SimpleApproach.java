package com.ken10.Phase2.ProbeMission.ProbeMissionDominik;

import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Probe;
import com.ken10.Phase2.SolarSystemModel.Vector;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Hashtable;
import java.util.List;


public class SimpleApproach {
    /// / History always preloaded
    private static final Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyPlanets;
    /// / Step size always defined to be the same for Rk4Probe and EphemerisLoader
    private static final int stepSizeMins = 12;
    /// / Deadline to finish our mission
    private static final LocalDateTime endTime = LocalDateTime.of(2026, 4, 1, 0, 0, 0);
    /// / Probe that we select to be the best fit
    private Probe initialProbe;
    /// / How close we get to Titan using the selected Probe
    private double closestDistance;
    /// / Initial dist to Titan at the ast of April 2025
    private static final double T0_DIST_TO_TITAN = 1.5784208741900856E9;
    /// / At what date and time do we get to Titan
    private LocalDateTime closestDistanceTime;
    /// / I think we might need it for the GUI
    private Hashtable<LocalDateTime, Probe> historyProbe;
    /// / Time to keep track, also defines when the simulation begins
    private LocalDateTime time = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
    /// / Number of degrees we increment by when we look for the all possible positions on the equator
    /// we can start from
    private double positionStepSize = 1;
    /// / Velocity cap, we break current loop when we exceed the given velocity
    private static final double V_MAX = 60; //km/s
    /// / we cannot get to titan in a year with velocity smaller than 50km/s
    private static final double V_MIN = 55;
    /// / List of local minimums stored for further evaluation
    private final int maxGridSearchIterations = 25;


    static {
        EphemerisLoader eph = new EphemerisLoader(2);
        eph.solve();
        historyPlanets = eph.history;
    }

    public SimpleApproach() {
        this.closestDistance = Double.MAX_VALUE;
        this.closestDistanceTime = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
    }

    public static void main(String[] args) {
        SimpleApproach sa = new SimpleApproach();
        sa.solve();
    }

    public void solve() {
//    List<Vector> startingPos = new EarthPositions(positionStepSize).getPositions();

// Get Earth and Titan positions
        Vector earthPosition = historyPlanets.get(LocalDateTime.of(2025, 4, 1, 0, 0, 0))
                .get(BodyID.EARTH.index()).getPosition();
        Vector titanPosition = historyPlanets.get(LocalDateTime.of(2025, 4, 1, 0, 0, 0))
                .get(BodyID.TITAN.index()).getPosition();

// Get direction vector from Earth to Titan
        Vector earthToTitan = titanPosition.subtract(earthPosition);

// Normalize and scale by Earth's radius
        Vector surfaceOffset = earthToTitan.normalize().multiply(-6370);

// Get the position on Earth's surface pointing toward Titan
        Vector pointedAtTitan = earthPosition.add(surfaceOffset);
        Grid globalGrid = new Grid();
        findBestVelocityVector(pointedAtTitan, globalGrid, 5, 0, Double.MAX_VALUE);
        System.out.println(initialProbe.toString() + closestDistance);
    }



    private RK4Probe findBestVelocityVector(Vector position, Grid localGrid,
                                        double stepSize, int iterations, double lastBestDist) {
        if(closestDistance <= 1E2) return localGrid.getRk4Probe();
        List<Grid> bestGrids = createLocalGrids(localGrid, stepSize, position);
        if(bestGrids.getFirst().getGridBest()>=lastBestDist) {
            for(int i = 1 ; i < 10; i++) {
                if(bestGrids.get(i).getGridBest()<lastBestDist) {
                    return findBestVelocityVector(position, bestGrids.get(i), stepSize/10, ++iterations, lastBestDist);
                }
            }
        }//now i want to prune the branch
        System.out.println("Closest dist is: " + bestGrids.getFirst().getGridBest() + " after " + iterations + " iterations");
        return findBestVelocityVector(position, bestGrids.getFirst(), stepSize/10, ++iterations, lastBestDist);
    }



    private List<Grid> createLocalGrids(Grid localGrid, double stepSize, Vector position) {
        List<Grid> grids = new ArrayList<>();
        for (double Vx = localGrid.getMinX(); Vx <= localGrid.getMaxX()-stepSize; Vx += stepSize) {
            for (double Vy = localGrid.getMinY(); Vy <= localGrid.getMaxY()-stepSize; Vy += stepSize) {
                for (double Vz = localGrid.getMinZ(); Vz <= localGrid.getMaxZ()-stepSize; Vz += stepSize) {
                    double velocity = Math.sqrt(Vx * Vx + Vy * Vy + Vz * Vz);
                    if (velocity > V_MAX || velocity < V_MIN) {
                        continue;
                    }
                    Grid instanceGrid = new Grid(Vx,Vy,Vz,Vx+stepSize,Vy+stepSize,Vz+stepSize);
                    instanceGrid.evaluate(maxGridSearchIterations, position, stepSizeMins, historyPlanets);
                    grids.add(instanceGrid);
                }
            }
        }
        grids.sort(Comparator.comparing(Grid::getGridBest));
        return grids;
    }
}



