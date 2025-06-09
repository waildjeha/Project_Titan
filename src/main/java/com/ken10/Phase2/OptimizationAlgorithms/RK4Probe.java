package com.ken10.Phase2.OptimizationAlgorithms;

import com.ken10.Phase2.SolarSystemModel.*;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

import static com.ken10.Phase2.SolarSystemModel.Earth.EARTH_VELOCITY_INITIAL;
import static com.ken10.Phase2.SolarSystemModel.GravityCalc.computeAcceleration;
import static com.ken10.Phase2.SolarSystemModel.Vector.getDistance;

/**
 * class that models the motion of a probe using the RK4 method.
 * It simulates the probe's trajectory through the solar system using celestial body data
 * and calculates the probe’s closest approach to a given destination body.
 */
public class RK4Probe{
    private Probe probe;
    private final Probe launchProbe;
    private final Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyPlanets;
    private final LocalDateTime endTime;
    private final int stepSize;
    private final LocalDateTime startTime;
    private LocalDateTime time;
    private double closestDistance = Double.MAX_VALUE;
    private LocalDateTime closestDistTime;
    private final BodyID destination;
    private final BodyID launchPlanet;

    //PUBLIC HISTORY
    public Hashtable<LocalDateTime, Probe> historyProbe;


    public RK4Probe(Probe probe, Hashtable<LocalDateTime,ArrayList<CelestialBodies>> historyPlanets, int stepSizeMin, LocalDateTime startTime, LocalDateTime endTime, BodyID destination, BodyID launchPlanet) {
        this.probe = probe;
        this.launchProbe  = new Probe(probe.getName(),
                probe.getPosition().copy(),
                probe.getVelocity().copy());
        this.destination = destination;
        this.launchPlanet = launchPlanet;
        this.historyPlanets = historyPlanets;
        this.startTime = startTime;
        this.time = startTime;
        this.endTime = endTime;
        this.stepSize = stepSizeMin;
        this.closestDistTime = startTime;
        this.historyProbe = new Hashtable<>();
    }

    public RK4Probe(Probe probe, LaunchData launchData) {
        this.probe = probe;
        this.launchProbe = new Probe(probe.getName(), probe.getPosition().copy(), probe.getVelocity().copy());
        this.historyPlanets = launchData.getHistoryPlanets();
        var keys = historyPlanets.keySet().stream().sorted().toList();
        int stepSizeMins = (int) Duration.between(keys.getFirst(), keys.get(1)).toMinutes();
        this.endTime = launchData.getEndTime();
        this.stepSize = stepSizeMins*2;
        this.startTime = launchData.getLaunchTime();
        this.time = startTime;
        this.destination = launchData.getDestination();
        this.launchPlanet = launchData.getLaunchPlanet();
        Vector earthPos = this.historyPlanets.get(this.startTime).get(destination.index()).getPosition();
        this.closestDistance = probe.getPosition().getDistance(earthPos);
        this.closestDistTime = startTime;
        this.historyProbe = new Hashtable<>();
    }
    public double getError() {
        return closestDistance;
    }

    public LocalDateTime getClosestDistTime() {
        return closestDistTime;
    }

    public Probe getInitialProbe() {
        return launchProbe;
    }

    public int getStepSize() {
        return stepSize;
    }

    /**
     * Runs the RK4 simulation, updating the probe's state over time.
     * Results are stored in historyProbe.
     * Also tracks the closest distance to the destination.
     * If a collision occurs with the launch planet, the history is cleared and simulation stops
     */
    public void solve() {
        closestDistance = getDistance(probe.getPosition(),
                historyPlanets.get(time).get(destination.index()).getPosition());
        while (time.isBefore(endTime)) {
            historyProbe.put(time, probe);
            // Check collision at current time
            if(checkLaunchPlanetCollision()){historyProbe.clear(); break;}
            updateBestResult();
            // Calculate new probe state
            probe = rk4Helper();
            // Store state and advance time
            time = time.plusMinutes(stepSize);
        }
        historyProbe.put(time, probe);
        updateBestResult();
    }

    /**
     * Checks whether the probe has collided with the launch planet within the first 8 minutes
     * or falls into the Sun
     * @return true if collisions happens
     */
    private boolean checkLaunchPlanetCollision() {
        if(time.isBefore(startTime.plusMinutes(8))) {
            double launchPlanetDistance = getDistance(probe.getPosition(),
                    historyPlanets.get(time).get(launchPlanet.index()).getPosition());
            double radius;
            if(launchPlanet.equals(BodyID.TITAN)) {radius = Titan.RADIUS-1e-8;}
            else if (launchPlanet.equals(BodyID.EARTH)) {radius = Earth.RADIUS-1e-8;}
            else {radius = 0;}
            return launchPlanetDistance <= radius;
        }
        return probe.getPosition().getDistance(historyPlanets.get(time).get(BodyID.SUN.index()).getPosition()) < 696_340;
    }

    /**
     * Updates the record of the closest distance between the probe and the destination planet.
     */
    private void updateBestResult() {
        // Get Titan position at CURRENT time
        Vector currentDestinationPosition = historyPlanets.get(time)
                .get(destination.index()).getPosition();

        // Calculate distance at CURRENT time
        double distToDestinantion = currentDestinationPosition.getDistance(probe.getPosition());

        // Update closest approach
        if(distToDestinantion < closestDistance) {
            closestDistTime = time;  // Use current time
            closestDistance = distToDestinantion;
        }
    }

    /**
     * Helper method to perform a single RK4 step.
     *
     * @return A new Probe instance representing the probe's next state.
     */
    private Probe rk4Helper() {
        int stepMinutes = stepSize;
        // we need to make the step size of the probe
        // in such a way the state of the planets can calculate the acceleration
        // of the probe at each RK4 time step.
        // -> stepRK4Probe MUST be 2*n*stepSizeRK4Planets

        //1st step RK4
        Probe y1 = probe.copy();
        ArrayList<CelestialBodies> state = new ArrayList<>(historyPlanets.get(time));
        state.add(y1);
        Vector k1Velocity = y1.getVelocity().multiply(stepSize *60);
        Vector k1Acceleration = computeAcceleration(state, BodyID.SPACESHIP.index()).multiply(stepSize *60);

        //2nd step RK4
        Probe y2 = new Probe("dominik",y1.getPosition().add(k1Velocity.multiply(0.5)),y1.getVelocity().add(k1Acceleration).multiply(0.5));
        state.clear();
        state = new ArrayList<>(historyPlanets.get(time.plusMinutes(stepMinutes/2)));
        state.add(y2);
        Vector k2Velocity = y2.getVelocity().multiply(stepSize *60);
        Vector k2Acceleration = computeAcceleration(state, BodyID.SPACESHIP.index()).multiply(stepSize *60);

        //3rd step RK4
        Probe y3 = new Probe("dominik", y1.getPosition().add(k2Velocity.multiply(0.5)), y1.getVelocity().add(k2Acceleration.multiply(0.5)));
        state.removeLast();
        state.add(y3);
        Vector k3Velocity = y3.getVelocity().multiply(stepSize *60);
        Vector k3Acceleration = computeAcceleration(state, BodyID.SPACESHIP.index()).multiply(stepMinutes*60);

        //4th step RK4
        Probe y4 = new Probe("dominik", y1.getPosition().add(k3Velocity), y1.getVelocity().add(k3Acceleration));
        state.clear();
        state = new ArrayList<>(historyPlanets.get(time.plusMinutes(stepMinutes)));
        state.add(y4);
        Vector k4Velocity = y4.getVelocity().multiply(stepSize *60);
        Vector k4Acceleration = computeAcceleration(state, BodyID.SPACESHIP.index()).multiply(stepMinutes*60);

        Vector kVelocity = k1Velocity.add(k2Velocity.multiply(2)).add(k3Velocity.multiply(2)).add(k4Velocity);
        Vector kAcceleration = k1Acceleration.add(k2Acceleration.multiply(2)).add(k3Acceleration.multiply(2)).add(k4Acceleration);

        kVelocity = kVelocity.divide(6);
        kAcceleration = kAcceleration.divide(6);

        return new Probe(probe.getName(), y1.getPosition().add(kVelocity), y1.getVelocity().add(kAcceleration));
    }

    @Override
    public String toString(){
        return "----------------------------------------------------------" + "\n" +
                        "Initial probe position and velocity: " + launchProbe + "\n" +
                        "Velocity magnitude relative to earth(only applicable from earth launch): " + (launchProbe.getVelocity().subtract(EARTH_VELOCITY_INITIAL).magnitude()) + "\n" +
                        "Velocity magnitude relative to the Sun " + launchProbe.getVelocity().magnitude() + "\n" +
                        "Closest Distance to destination: " + closestDistance + "\n" +
                        "Date of closest approach: " + closestDistTime + "\n";
    }


    public static void main(String[] args) {
//    EphemerisLoader eph = new EphemerisLoader(1);
//    eph.solve();
//        Vector earthPos = new Vector (-1.4664541859104577E8, -2.8949304626334388E7, 2241.9186033698497);
//        Probe probe = new Probe("probe", earthPos, new Vector (63.29024702239812, -33.49000052271595, -15.072908267640539));
//        RK4Probe sim = new RK4Probe(probe, eph.history, 2, SolarSystem.T_0, eph.getEndTime(), BodyID.TITAN, BodyID.EARTH);
//        sim.solve();
//        Probe
//    LaunchData launch = new LaunchData(BodyID.EARTH, BodyID.TITAN,  )
    }

    //----------------------------------------------------------
//Initial probe position and velocity: (-1.4664541859104577E8, -2.8949304626334388E7, 2241.9186033698497) (63.29024702239812, -33.49000052271595, -15.072908267640539)
//Velocity magnitude relative to earth: 60.066056771931656
//Closest Distance to Titan: 2508.27816086085
//Date of closest approach: 2026-03-20T02:26
}

