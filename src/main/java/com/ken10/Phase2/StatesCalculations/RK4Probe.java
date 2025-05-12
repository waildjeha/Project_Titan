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

            LocalDateTime t0 = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
            historyProbe.put(time,probe);
            closestDistance = getDistance(probe.getPosition(), historyPlanets.get(time).get(BodyID.TITAN.index()).getPosition());
            double noChangeLoopBreak = closestDistance;

        while (time.isBefore(endTime)) {
            if((time.isAfter((t0.plusMinutes(180))) && noChangeLoopBreak==closestDistance))
                {historyProbe.clear();break;}
            if(time.isBefore(t0.plusMinutes(8))&&getDistance(probe.getPosition(),historyPlanets.get(time).get(BodyID.EARTH.index()).getPosition())<6000)
            {System.out.println("Probe gets inside the Earth");break;}
            probe = rk4Helper();
            time = time.plusMinutes(stepSizeMin);
            Vector currentTitanPosition = historyPlanets.get(time).get(BodyID.TITAN.index()).getPosition();
            double distToTitan = getDistance(currentTitanPosition, probe.getPosition());
                if(distToTitan<closestDistance) {
                closestDistTime = time;
                closestDistance = distToTitan;
                }
            historyProbe.put(time,probe);
        }
    }
    private Probe rk4Helper() {
        int stepMinutes = stepSizeMin;
        // we need to make the step size of the probe
        // in such a way the state of the planets can calculate the acceleration
        // of the probe at each RK4 time step.
        // -> stepRK4Probe must be 2*n*stepSizeRK4Planets

        //1st step RK4
        Probe y1 = probe.copy();
        ArrayList<CelestialBodies> state = new ArrayList<>(historyPlanets.get(time));
        state.add(y1);
        Vector k1Velocity = y1.getVelocity().multiply(stepSizeMin*60);
        Vector k1Acceleration = computeAcceleration(state, BodyID.SPACESHIP.index()).multiply(stepSizeMin*60);

        //2nd step RK4
        Probe y2 = new Probe("dominik",y1.getPosition().add(k1Velocity.multiply(0.5)),y1.getVelocity().add(k1Acceleration).multiply(0.5));
        state.clear();
        state = new ArrayList<>(historyPlanets.get(time.plusMinutes(stepMinutes/2)));
        state.add(y2);
        Vector k2Velocity = y2.getVelocity().multiply(stepSizeMin*60);
        Vector k2Acceleration = computeAcceleration(state, BodyID.SPACESHIP.index()).multiply(stepSizeMin*60);

        //3rd step RK4
        Probe y3 = new Probe("dominik", y1.getPosition().add(k2Velocity.multiply(0.5)), y1.getVelocity().add(k2Acceleration.multiply(0.5)));
        state.removeLast();
        state.add(y3);
        Vector k3Velocity = y3.getVelocity().multiply(stepSizeMin*60);
        Vector k3Acceleration = computeAcceleration(state, BodyID.SPACESHIP.index()).multiply(stepMinutes*60);

        //4th step RK4
        Probe y4 = new Probe("dominik", y1.getPosition().add(k3Velocity), y1.getVelocity().add(k3Acceleration));
        state.clear();
        state = new ArrayList<>(historyPlanets.get(time.plusMinutes(stepMinutes)));
        state.add(y4);
        Vector k4Velocity = y4.getVelocity().multiply(stepSizeMin*60);
        Vector k4Acceleration = computeAcceleration(state, BodyID.SPACESHIP.index()).multiply(stepMinutes*60);

        Vector kVelocity = k1Velocity.add(k2Velocity.multiply(2)).add(k3Velocity.multiply(2)).add(k4Velocity);
        Vector kAcceleration = k1Acceleration.add(k2Acceleration.multiply(2)).add(k3Acceleration.multiply(2)).add(k4Acceleration);

        kVelocity = kVelocity.divide(6);
        kAcceleration = kAcceleration.divide(6);

        return new Probe(probe.getName(), y1.getPosition().add(kVelocity), y1.getVelocity().add(kAcceleration));
    }

    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.of(2025,4,1,0,0,0);
        Probe probe = new Probe("probe", new Vector(-1.4665178859104577E8, -2.8949304626334388E7, 2241.9186033698497), new Vector(20, 0, 0));
        EphemerisLoader eph = new EphemerisLoader(2);
        eph.solve();
        RK4Probe dataProbe = new RK4Probe(probe, eph.history, 4);
        dataProbe.solve();
        System.out.println(dataProbe.getClosestDistance());
        System.out.println(dataProbe.getClosestDistTime());
        System.out.println(dataProbe.getInitialProbe().getPosition());
        System.out.println(dataProbe.getInitialProbe().getVelocity());

    }
}

