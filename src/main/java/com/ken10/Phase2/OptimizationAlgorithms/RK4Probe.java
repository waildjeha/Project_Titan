package com.ken10.Phase2.OptimizationAlgorithms;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

import static com.ken10.Phase2.SolarSystemModel.Earth.EARTH_VELOCITY_INITIAL;
import static com.ken10.Phase2.SolarSystemModel.GravityCalc.computeAcceleration;
import static com.ken10.Phase2.SolarSystemModel.Vector.getDistance;

public class RK4Probe {
    private Probe probe;
    private final Probe launchProbe;
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

    public int getStepSizeMin() {
        return stepSizeMin;
    }

    public void solve() {
        LocalDateTime t0 = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        historyProbe.put(time, probe);
        closestDistance = getDistance(probe.getPosition(),
                historyPlanets.get(time).get(BodyID.TITAN.index()).getPosition());

        while (time.isBefore(endTime)) {
            // Check collision at current time
            if(time.isBefore(t0.plusMinutes(8))) {
                double earthDist = getDistance(probe.getPosition(),
                        historyPlanets.get(time).get(BodyID.EARTH.index()).getPosition());
                if(earthDist <= 6369.9998) break;
            }

            // Calculate new probe state
            probe = rk4Helper();

            // Get Titan position at CURRENT time
            Vector currentTitanPosition = historyPlanets.get(time)
                    .get(BodyID.TITAN.index()).getPosition();

            // Calculate distance at CURRENT time
            double distToTitan = getDistance(currentTitanPosition, probe.getPosition());

            // Update closest approach
            if(distToTitan < closestDistance) {
                closestDistTime = time;  // Use current time
                closestDistance = distToTitan;
            }

            // Store state and advance time
            historyProbe.put(time, probe);
            time = time.plusMinutes(stepSizeMin);
        }

        // Handle final state after loop
        historyProbe.put(time, probe);
    }

    private Probe rk4Helper() {
        int stepMinutes = stepSizeMin;
        // we need to make the step size of the probe
        // in such a way the state of the planets can calculate the acceleration
        // of the probe at each RK4 time step.
        // -> stepRK4Probe MUST be 2*n*stepSizeRK4Planets

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

        return new Probe(probe.getName(), y1.getPosition().add(kVelocity), y1.getVelocity().add(kAcceleration),1.0,11.0);
    }

    @Override
    public String toString(){
        return "----------------------------------------------------------" + "\n" +
                        "Initial probe position and velocity: " + launchProbe + "\n" +
                        "Velocity magnitude relative to earth: " + (launchProbe.getVelocity().subtract(EARTH_VELOCITY_INITIAL).magnitude()) + "\n" +
                        "Closest Distance to Titan: " + closestDistance + "\n" +
                        "Date of closest approach: " + closestDistTime + "\n";
    }


    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.of(2025,4,1,0,0,0);
        Probe probe = new Probe("probe", new Vector(-1.4664541759104577E8, -2.8949304626334388E7, 2241.9186033698497), new Vector(63.28501526589577, -30.337437078355766, -12.818387742029104));
        ArrayList<CelestialBodies> stateT0 = SolarSystem.createPlanets();
        stateT0.add(probe);
        EphemerisLoader eph = new EphemerisLoader(stateT0, startTime, startTime.plusYears(1), 1);
        eph.solve();
        ArrayList<CelestialBodies> stateT1 = eph.history.get(LocalDateTime.of(2026, 3, 23, 0, 48));
        Vector positionT = stateT1.get(BodyID.TITAN.index()).getPosition();
        Vector positionP = stateT1.get(BodyID.SPACESHIP.index()).getPosition();
        System.out.println(positionP.getDistance(positionT));

    }
}

