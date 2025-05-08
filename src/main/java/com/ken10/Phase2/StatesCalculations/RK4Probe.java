package com.ken10.Phase2.StatesCalculations;

import com.ken10.Phase2.SolarSystemModel.*;

import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;

import static com.ken10.Phase2.SolarSystemModel.GravityCalc.computeAcceleration;

public class RK4Probe {
    private final Probe probe;
    private final EphemerisLoader eph;
    private final LocalDateTime endTime;
    private final int stepSizeMin;
    private LocalDateTime time;
    public Hashtable<LocalDateTime, Probe> historyProbe = new Hashtable<>();

    Hashtable<LocalDateTime, Probe> probeHistory = new Hashtable<>();
    public RK4Probe(Probe probe, EphemerisLoader eph, int stepSizeMin) {
        this.probe = probe;
        this.eph = eph;
        this.time = LocalDateTime.of(2025,4,1,0,0,0);
        this.endTime = eph.endTime;
        this.stepSizeMin = stepSizeMin;


    }
    public void solve(){
        historyProbe.put(time, probe);
        while(time.isBefore(endTime)){
            List<CelestialBodies> currentState = eph.history.get(time);
            currentState.add(probe);
            Vector newPosition = probe.getPosition().add(probe.getVelocity().multiply(stepSizeMin*60));
            Vector newVelocity = computeAcceleration(currentState, currentState.size()-1);
            Probe newProbeState = new Probe("probe",newPosition, newVelocity);
            time = time.plusMinutes(stepSizeMin);
            record(time,newProbeState);
        }
    }

    private void record(LocalDateTime time, Probe probe){
        probeHistory.put(time, probe);
    }
}

