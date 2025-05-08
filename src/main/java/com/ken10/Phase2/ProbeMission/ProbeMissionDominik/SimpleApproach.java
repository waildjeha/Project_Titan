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

import static com.ken10.Phase2.SolarSystemModel.Vector.getDistance;

public class SimpleApproach {
    private static final Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyPlanets;
    private static final int stepSizeMins = 2;
    private static final LocalDateTime endTime = LocalDateTime.of(2026, 4, 1, 0, 0, 0);
    private Probe initialProbe;
    private double closestDistance;
    private Hashtable<LocalDateTime, Probe> historyProbe;
    private LocalDateTime time = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
    private double positionStepSize = 1;
    private static final double V_MAX = 60; //km/s


    static {
        EphemerisLoader eph = new EphemerisLoader(stepSizeMins);
        eph.solve();
        historyPlanets = eph.history;
    }

    public SimpleApproach() {
    }

    public void solve() {
    List<Vector> startingPos = new EarthPositions(positionStepSize).getPositions();
    for(Vector v : startingPos) {

    }
    }

    public static void main(String[] args) {
        SimpleApproach sa = new SimpleApproach();
        sa.solve();
    }





}
