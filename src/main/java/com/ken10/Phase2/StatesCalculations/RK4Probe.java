package com.ken10.Phase2.StatesCalculations;

import com.ken10.Phase2.SolarSystemModel.*;

import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;

import static com.ken10.Phase2.SolarSystemModel.GravityCalc.computeAcceleration;

public class RK4Probe {
    private Probe probe;
    private final EphemerisLoader eph;
    private final LocalDateTime endTime;
    private final int stepSizeMin;
    private LocalDateTime time;
    public Hashtable<LocalDateTime, Probe> historyProbe = new Hashtable<>();

    public RK4Probe(Probe probe, EphemerisLoader eph, int stepSizeMin) {
        this.probe = probe;
        System.out.println(probe.toString());
        this.eph = eph;
        this.time = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        this.endTime = eph.endTime;
        this.stepSizeMin = stepSizeMin;
        if (this.eph.history.isEmpty()) this.eph.solve();
    }

    public void solve() {
        historyProbe.put(time, probe);
        while (time.isBefore(endTime)) {
            List<CelestialBodies> currentState = eph.history.get(time);
            currentState.add(probe);
            Vector newPosition = probe.getPosition().add(probe.getVelocity().multiply(stepSizeMin * 60));
            System.out.println(newPosition.toString());
            Vector newVelocity = computeAcceleration(currentState, BodyID.SPACESHIP.index());
            System.out.println(newVelocity.toString());
            Probe newProbeState = new Probe("probe", newPosition, newVelocity);
            time = time.plusMinutes(stepSizeMin);
            probe = newProbeState;
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
        RK4Probe dataProbe = new RK4Probe(probe, eph, 2);
        dataProbe.solve();
    }
}

