package com.ken10.Phase2.ProbeMission.ProbeMissionDominik;

import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Probe;
import com.ken10.Phase2.SolarSystemModel.Vector;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;
import com.ken10.Phase2.StatesCalculations.RK4Probe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;


public class SimpleApproach {
    /// / History always preloaded
    private static final Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyPlanets;
    /// / Step size always defined to be the same for Rk4Probe and EphemerisLoader
    private static final int stepSizeMins = 2;
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
    private static final double V_MIN = 50;
    /// / List of local minimums stored for further evaluation
    private List<Probe> goodGuesses;


    static {
        EphemerisLoader eph = new EphemerisLoader(stepSizeMins);
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
        Vector surfaceOffset = earthToTitan.normalize().multiply(6370);

// Get the position on Earth's surface pointing toward Titan
        Vector pointedAtTitan = earthPosition.add(surfaceOffset).multiply(-1);

        findLocalMinima(pointedAtTitan, -60, 60, -60, 60, -60, 60, 5);
        System.out.println(initialProbe.toString());
    }

    private void findLocalMinima(Vector position, double minVx, double maxVx,
                                        double minVy, double maxVy, double minVz, double maxVz,
                                        double stepSize) {
//        Probe goodGuess = null;
        for (double Vx = minVx; Vx <= maxVx; Vx = Vx + stepSize) {
            for (double Vy = minVy; Vy <= maxVy; Vy = Vy + stepSize) {
                for (double Vz = minVz; Vz <= maxVz; Vz = Vz + stepSize) {
                    double velocity = Math.sqrt(Vx * Vx + Vy * Vy + Vz * Vz);
                    if (velocity > V_MAX || velocity < V_MIN) {
                        continue;
                    }
                    Probe probe = new Probe("dominik", position, new Vector(Vx, Vy, Vz));
                    RK4Probe rk4Probe = new RK4Probe(probe, historyPlanets, 4);
                    rk4Probe.solve();
                    if (rk4Probe.historyProbe != null) {
                        double closestDistanceTmp = rk4Probe.getClosestDistance();

                        if (closestDistanceTmp < closestDistance) {
                            closestDistance = closestDistanceTmp;
                            closestDistanceTime = rk4Probe.getClosestDistTime();
                            initialProbe = rk4Probe.getInitialProbe();
                            System.out.println("----------------------------------\n" +
                                    "NEW BEST RESULT");
                            System.out.println(closestDistanceTime);
                            System.out.println(initialProbe);
                            System.out.println(closestDistance);
                        }

                    }

                }// else System.out.println("Probe goes through the inside of the Earth, or it goes in the wrong direction");
            }
        }

    }
}



