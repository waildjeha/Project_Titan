package com.ken10.Phase2.OptimizationAlgorithms;

import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

public class LaunchData {
    private final BodyID destination;
    private final Vector initialPosition;
    private final LocalDateTime launchTime;
    private final BodyID isSurface;
    public Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyPlanets;

    LaunchData(BodyID destination, Vector initialPosition, LocalDateTime launchTime) {
        this.destination = destination;
        this.initialPosition = initialPosition;
        this.launchTime = launchTime;
        this.isSurface = isSurface();
        loadHistory(destination);
    }


    private BodyID isSurface() {
        var initialState = SolarSystem.createPlanets();
        if(initialPosition.getDistance(initialState.get(BodyID.TITAN.index()).getPosition()) == Titan.RADIUS)
            return BodyID.TITAN;
        if (initialPosition.getDistance(initialState.get(BodyID.EARTH.index()).getPosition()) == Earth.RADIUS)
            return BodyID.EARTH;
        else return BodyID.SPACESHIP;
    }

    private void loadHistory(BodyID destination) {
        int duration = 2;
        if (destination.equals(BodyID.TITAN)) duration = 1;
        EphemerisLoader eph = new EphemerisLoader(1, duration);
        eph.solve();
        historyPlanets = eph.history;

    }

    public BodyID getDestination() {
        return destination;
    }
    public Vector getInitialPosition() {
        return initialPosition;
    }
    public LocalDateTime getLaunchTime() {
        return launchTime;
    }
    public Hashtable<LocalDateTime, ArrayList<CelestialBodies>> getHistoryPlanets() {
        return historyPlanets;
    }
    public BodyID getIsSurface() {
        return isSurface;
    }
}
