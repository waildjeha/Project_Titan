package com.ken10.Phase2.Missions;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

public class ToTitan {

    protected EphemerisLoader historyEphemeris;
    protected double totalFuelConsumption = 0;

    protected static final double G = 6.67430e-20;       // Gravitational constant (m³ kg⁻¹ s⁻²)
    protected static final double M_TITAN = Titan.MASS;   // Titan's mass (kg)
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
        totalFuelConsumption += thrustMagnitude * Probe.MASS; //the impulse last always one sec so it's enough
    }

    protected LocalDateTime getLastDateTime() {
        return historyEphemeris.getEndTime();
    }
}
