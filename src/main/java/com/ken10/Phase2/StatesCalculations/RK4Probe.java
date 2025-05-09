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
    private Probe launchProbe;
    private final Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyPlanets;
    private static final LocalDateTime endTime = LocalDateTime.of(2026,4,1,0,0,0);
    private final int stepSizeMin;
    private LocalDateTime time;
    public Hashtable<LocalDateTime, Probe> historyProbe;
    private double closestDistance = Double.MAX_VALUE;
    private LocalDateTime closestDistTime;


    public RK4Probe(Probe probe, Hashtable<LocalDateTime,ArrayList<CelestialBodies>> historyPlanets, int stepSizeMin) {
        this.probe = probe;
        this.launchProbe  = new Probe(probe.getName(),
                probe.getPosition().copy(),
                probe.getVelocity().copy());
        this.historyPlanets = historyPlanets;
        this.time = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        this.stepSizeMin = stepSizeMin;
        this.closestDistTime = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        this.historyProbe = new Hashtable<>();
    }
    public double getClosestDistance() {
        return closestDistance;
    }

    public LocalDateTime getClosestDistTime() {
        return closestDistTime;
    }
    public Probe getInitialProbe() {
        return launchProbe;
    }

    public void solve() {
            historyProbe.put(time,probe);
            closestDistance = getDistance(probe.getPosition(), historyPlanets.get(time).get(BodyID.TITAN.index()).getPosition());
        while (time.isBefore(endTime)) {
            List<CelestialBodies> currentState = new ArrayList<>(historyPlanets.get(time));
//            System.out.println(currentState.get(BodyID.EARTH.index()).getPosition().toString());
            currentState.add(probe);
            Vector newPosition = probe.getPosition().add(probe.getVelocity().multiply(stepSizeMin * 60));
//            System.out.println(newPosition.toString());
            Vector newVelocity = probe.getVelocity().add(computeAcceleration(currentState, BodyID.SPACESHIP.index()).multiply(stepSizeMin * 60));
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
            historyProbe.put(time,probe);
            //record(time, probe);
        }
    }

    private void record(LocalDateTime time, Probe probe) {
        probe.putProbeHistory(time,probe);
    }

    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.of(2025,4,1,0,0,0);
        Probe probe = new Probe("probe", new Vector(-1.4665178859104577E8, -2.8949304626334388E7, 2241.9186033698497), new Vector(20, 0, 0));
        EphemerisLoader eph = new EphemerisLoader(2);
        eph.solve();
        RK4Probe dataProbe = new RK4Probe(probe, eph.history, 2);
        dataProbe.solve();
        System.out.println(dataProbe.getClosestDistance());
        System.out.println(dataProbe.getClosestDistTime());
        System.out.println(dataProbe.getInitialProbe().getPosition());

    }
}

