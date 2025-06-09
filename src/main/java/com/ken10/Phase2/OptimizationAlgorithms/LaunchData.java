package com.ken10.Phase2.OptimizationAlgorithms;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

public class LaunchData {
    private final BodyID destination;
    private final BodyID launchPlanet;
    private final Vector initialPosition;
    private final LocalDateTime launchTime;
    private final LocalDateTime endTime;
    private final BodyID isSurface;
    public Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyPlanets;

    public LaunchData(BodyID destination, BodyID launchPlanet, Vector initialPosition, LocalDateTime launchTime, LocalDateTime endTime) {
        this.destination = destination;
        this.launchPlanet = launchPlanet;
        this.initialPosition = initialPosition;
        this.launchTime = launchTime;
        this.endTime = endTime;
        loadHistory(destination);
        this.isSurface = isSurface();
    }
    public LaunchData(BodyID destination, BodyID launchPlanet, Vector initialPosition, LocalDateTime launchTime, LocalDateTime endTime, ArrayList<CelestialBodies> initialPlanets) {
        this.destination = destination;
        this.launchPlanet = launchPlanet;
        this.initialPosition = initialPosition;
        this.launchTime = launchTime;
        this.endTime = endTime;
        loadHistory(initialPlanets);
        this.isSurface = isSurface();
    }


    private BodyID isSurface() {
        var initialState = historyPlanets.get(launchTime);
        if(initialPosition.getDistance(initialState.get(BodyID.TITAN.index()).getPosition()) <= Titan.RADIUS)
            return BodyID.TITAN;
        if (initialPosition.getDistance(initialState.get(BodyID.EARTH.index()).getPosition()) <= Earth.RADIUS)
            return BodyID.EARTH;
         return BodyID.SPACESHIP;
    }

    private void loadHistory(BodyID destination) {
        int duration = destination.equals(BodyID.TITAN) ? 1 : 2;
        EphemerisLoader eph = new EphemerisLoader(1, duration);
        eph.solve();
        historyPlanets = eph.history;
    }
    private void loadHistory(ArrayList<CelestialBodies> planets) {
        EphemerisLoader eph = new EphemerisLoader(planets, this.launchTime, this.endTime, 1, false);
        eph.solve();
        historyPlanets = eph.history;
    }

    public BodyID getDestination() {
        return destination;
    }
    public BodyID getLaunchPlanet() {
        return launchPlanet;
    }
    public Vector getInitialPosition() {
        return initialPosition;
    }
    public LocalDateTime getLaunchTime() {
        return launchTime;
    }
    public LocalDateTime getEndTime() {
        return endTime;
    }
    public Hashtable<LocalDateTime, ArrayList<CelestialBodies>> getHistoryPlanets() {
        return historyPlanets;
    }
    public BodyID getIsSurface() {
        return isSurface;
    }
}
