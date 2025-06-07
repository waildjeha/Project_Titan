package com.ken10.Phase2;

import com.ken10.Phase2.SolarSystemModel.*;

import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.time.temporal.ChronoUnit;

/**
 * Titan engine mission which uses an optimzation algorithm to find a path and velocity.
 * Works in stages of launch, journey, orbit, journey back
 */
public class TitanMission {
    private static final int STEP_SIZE_MINUTES = 1;
    private static final LocalDateTime LAUNCH_DATE = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
    private static final LocalDateTime END_DATE = LAUNCH_DATE.plusYears(1);
    private static final double TITAN_ORBIT_MIN_HEIGHT = 100;
    private static final double TITAN_ORBIT_MAX_HEIGHT = 300;

    private Probe probe;
    private EphemerisLoader eph;
    private Hashtable<LocalDateTime, ArrayList<CelestialBodies>> history;
  //  private GradientDescent optimizer;
    private MissionState currentState;
    private double totalFuelUsed;

    public enum MissionState {
        LAUNCH,
        TRANSIT_TO_TITAN,
        TITAN_ORBIT,
        TRANSIT_TO_EARTH,
        EARTH_RETURN,
        COMPLETE
    }

    public TitanMission() {
        initializeMission();
    }

    /**
     * Sets up mission by creating solar system and getting position/velocity of the probe
     */
    void initializeMission() {
        ArrayList<CelestialBodies> solarSystem = SolarSystem.createPlanets();
        CelestialBodies earth = solarSystem.get(BodyID.EARTH.index());
        CelestialBodies titan = solarSystem.get(BodyID.TITAN.index());

        Vector earthPosition = earth.getPosition();
        Vector earthVelocity = earth.getVelocity();
        Vector earthToTitan = titan.getPosition().subtract(earthPosition);
        Vector surfaceOffset = earthToTitan.normalize().multiply(6370);
        Vector probePosition = earthPosition.add(surfaceOffset);

        probe = new Probe("probe", probePosition, earthVelocity);

        solarSystem.add(probe);

        eph = new EphemerisLoader(solarSystem, LAUNCH_DATE, END_DATE, STEP_SIZE_MINUTES);
        optimizer = new GradientDescent(probe, eph);

        currentState = MissionState.LAUNCH;
        totalFuelUsed = 0;
    }

    /**
     * Switches through stages of mission according to conditions
     */
    public void executeMission() {
        while (currentState != MissionState.COMPLETE) {
            switch (currentState) {
                case LAUNCH:
                    Vector optimalVelocity = optimizer.findOptimalLaunchVelocity();
                    Vector currentVelocity = probe.getVelocity();
                    Vector velocityChange = optimalVelocity.subtract(currentVelocity);
                    probe.applyImpulse(velocityChange.multiply(probe.getMass()));

                    currentState = MissionState.TRANSIT_TO_TITAN;
                    break;
                case TRANSIT_TO_TITAN:
                    titanJourney();
                    break;
                case TITAN_ORBIT:
                    titanOrbit();
                    break;
                case TRANSIT_TO_EARTH:
                    returnJourney();
                    break;
                case EARTH_RETURN:
                    earthOrbit();
                    break;
            }
        }
    }

    /**
     * Corrects probe trajectory throughout the journey
     */
    private void titanJourney() {
        Vector optimalManeuver = optimizer.findOptimalManeuver("titan");

        if (optimalManeuver != null) {
            probe.applyImpulse(optimalManeuver);
        }
        CelestialBodies titan = eph.history.get(eph.time).get(BodyID.TITAN.index());
        double distance = probe.getPosition().getDistance(titan.getPosition());

        if (distance <= TITAN_ORBIT_MAX_HEIGHT) {
            currentState = MissionState.TITAN_ORBIT;
        }
    }

    /**
     * calculates a change neccesary to reach the orbital velocity for titan
     * stays until half a year at the moment
     */
    private void titanOrbit() {
        CelestialBodies titan = eph.history.get(eph.time).get(BodyID.TITAN.index());
        double distance = probe.getPosition().getDistance(titan.getPosition());

        if (distance >= TITAN_ORBIT_MIN_HEIGHT && distance <= TITAN_ORBIT_MAX_HEIGHT) {
            Vector titanToProbe = probe.getPosition().subtract(titan.getPosition());
            Vector orbitalVelocity = calculateOrbitalVelocity(titan, distance);

            Vector currentVelocity = probe.getVelocity();
            Vector velocityChange = orbitalVelocity.subtract(currentVelocity);
            probe.applyImpulse(velocityChange.multiply(probe.getMass()));

            if (eph.time.isAfter(LAUNCH_DATE.plusMonths(6))) {
                currentState = MissionState.TRANSIT_TO_EARTH;
            }
        } else {
            Vector correction = calculateOrbitCorrection(titan, distance);
            probe.applyImpulse(correction);
        }
    }

    /**
     * same as the journey to titan
     */
    private void returnJourney() {
        Vector optimalManeuver = optimizer.findOptimalManeuver("earth");

        if (optimalManeuver != null) {
            probe.applyImpulse(optimalManeuver);
        }

        CelestialBodies earth = eph.history.get(eph.time).get(BodyID.EARTH.index());
        double distance = probe.getPosition().getDistance(earth.getPosition());

        if (distance <= 1000000) {
            currentState = MissionState.EARTH_RETURN;
        }
    }

    /**
     * orbital calc
     * @return
     */
    /*private void earthOrbit() {
        CelestialBodies earth = eph.history.get(eph.time).get(BodyID.EARTH.index());

        Vector earthToProbe = probe.getPosition().subtract(earth.getPosition());
        Vector reentryVelocity = calculateReentryVelocity(earth);

        Vector currentVelocity = probe.getVelocity();
        Vector velocityChange = reentryVelocity.subtract(currentVelocity);
        probe.applyImpulse(velocityChange.multiply(probe.getMass()));

        currentState = MissionState.COMPLETE;
    }

    private Vector calculateOrbitalVelocity(CelestialBodies orbitPlanet, double distance) {
        double orbitalSpeed = Math.sqrt(GravityCalc.g * orbitPlanet.getMass() / distance); // orbital velocity formula
        Vector distanceV = probe.getPosition().subtract(orbitPlanet.getPosition());
        Vector orbitVector = new Vector(-distanceV.getY(), distanceV.getX(), 0);
        return orbitVector.normalize().multiply(orbitalSpeed);
    }

    private Vector calculateOrbitCorrection(CelestialBodies orbitPlanet, double currentDistance) {
        Vector distanceV = probe.getPosition().subtract(orbitPlanet.getPosition());
        double targetDistance = 200; // just to stay kinda central
        double correctionMagnitude = (targetDistance - currentDistance) * 0.1;
        return distanceV.normalize().multiply(correctionMagnitude);
    }*/

    public MissionState getCurrentState() {
        return currentState;
    }

    public double getTotalFuelUsed() {
        return probe.getTotalFuelUsed();
    }

    public double getMissionDuration() {
        return ChronoUnit.DAYS.between(LAUNCH_DATE, eph.time);
    }

    public Probe getProbe() {
        return probe;
    }
}