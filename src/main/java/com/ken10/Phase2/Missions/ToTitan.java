package com.ken10.Phase2.Missions;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
/**
 * This class simulates a mission trajectory from Earth to Titan using the
 * ephemeris data. It evaluates the probe's path and estimates the total fuel consumption based on
 * the initial velocity delta required to escape Earth.
 *
 * <p>Internally, it uses EphemerisLoader to load historical position and velocity data
 * of celestial bodies.</p>
 */
public class ToTitan {

    protected EphemerisLoader historyEphemeris;
    protected double totalFuelConsumption = 0;

    protected static final double G = 6.67430e-20;
    protected static final double M_TITAN = Titan.MASS;
    protected static final double R_TITAN = Titan.RADIUS;

    protected ToTitan() {
        this.historyEphemeris = new EphemerisLoader(false);
        evaluateFuel();
    }

    public ArrayList<CelestialBodies> getLastState() {
        var time = historyEphemeris.history.keySet().stream().sorted().toList().getLast();
        return historyEphemeris.history.get(time);
    }

    public double getTotalFuelConsumption() {
        return totalFuelConsumption;
    }

    private void evaluateFuel() {
        var time = historyEphemeris.history.keySet().stream().sorted().toList().getFirst();
        var spaceshipVel = historyEphemeris.history.get(time).get(BodyID.SPACESHIP.index()).getVelocity();
        Vector earthVelocity = historyEphemeris.history.get(time).get(BodyID.EARTH.index()).getVelocity();
        double thrustMagnitude = spaceshipVel.subtract(earthVelocity).magnitude();
        totalFuelConsumption += thrustMagnitude * Probe.MASS;
    }

    protected LocalDateTime getLastDateTime() {
        return historyEphemeris.getEndTime();
    }

    public static EphemerisLoader getEphemerisLoader(){
        ToTitan backToEarth = new ToTitan();
        return backToEarth.historyEphemeris;
    }

}
