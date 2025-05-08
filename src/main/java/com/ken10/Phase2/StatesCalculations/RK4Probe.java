package com.ken10.Phase2.StatesCalculations;

import com.ken10.Phase2.SolarSystemModel.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.ken10.Phase2.SolarSystemModel.GravityCalc.computeAcceleration;
import static com.ken10.Phase2.SolarSystemModel.Vector.getDistance;

public class RK4Probe {
    private Probe probe;
    private final Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyPlanets;
    private static final LocalDateTime endTime = LocalDateTime.of(2026,4,1,0,0,0);
    private final int stepSizeMin;
    private LocalDateTime time;
    public Hashtable<LocalDateTime, Probe> historyProbe;
    private double closestDistance = Double.MAX_VALUE;
    private LocalDateTime closestDistTime;


    public RK4Probe(Probe probe, Hashtable<LocalDateTime,ArrayList<CelestialBodies>> historyPlanets, int stepSizeMin) {
        this.probe = probe;
        this.historyPlanets = historyPlanets;
        this.time = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        this.stepSizeMin = stepSizeMin;
        this.closestDistTime = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
    }
    public double getClosestDistance() {
        return closestDistance;
    }

    public LocalDateTime getClosestDistTime() {
        return closestDistTime;
    }
    public Probe getInitialProbe() {
        return new Probe("dominik", probe.getInitialPosition(), probe.getInitialVelocity());
    }

    public void solve() {
            historyProbe.put(time, probe);
            closestDistance = getDistance(probe.getPosition(), historyPlanets.get(time).get(BodyID.TITAN.index()).getPosition());
        while (time.isBefore(endTime)) {
            List<CelestialBodies> currentState = historyPlanets.get(time);
            currentState.add(probe);
            Vector newPosition = probe.getPosition().add(probe.getVelocity().multiply(stepSizeMin * 60));
//            System.out.println(newPosition.toString());
            Vector newVelocity = computeAcceleration(currentState, BodyID.SPACESHIP.index());
//            System.out.println(newVelocity.toString());
            Probe newProbeState = new Probe("probe", newPosition, newVelocity);
            time = time.plusMinutes(stepSizeMin);
            probe = newProbeState;
            Vector currentTitanPosition = historyPlanets.get(time).get(BodyID.TITAN.index()).getPosition();
            double distToTitan = getDistance(currentTitanPosition, probe.getPosition());
            if(distToTitan<closestDistance) {
                closestDistTime = time;
                closestDistance = distToTitan;
            };
            record(time, probe);
        }
    }

    private void record(LocalDateTime time, Probe probe) {
        historyProbe.put(time, probe);
    }

    public static void main(String[] args) {
        Probe probe = new Probe("probe", new Vector(0, 0, 0), new Vector(0, 0, 0));
        EphemerisLoader eph = new EphemerisLoader(2);
        eph.solve();
        RK4Probe dataProbe = new RK4Probe(probe, eph.history, 2);
        dataProbe.solve();
    }
}

