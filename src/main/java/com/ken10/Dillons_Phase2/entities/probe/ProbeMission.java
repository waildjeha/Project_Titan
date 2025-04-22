package com.ken10.Dillons_Phase2.entities.probe;

import com.ken10.Dillons_Phase2.entities.Other.Vector;
import com.ken10.Dillons_Phase2.entities.planet.CelestialBodies;


public class ProbeMission {
    private static final double MAX_MISSION_TIME = 31449600;
    //changed back to secs // = 31449600
    // it's described in years (1 year is 31,449,600 secs)
    private static final double MAX_INIT_VELOCITY = 60;
    // = 60.0 km s^-1
    private Probe probe;
    private double currentTime;
    private boolean missionComplete;
    private CelestialBodies initialPosition;
    private CelestialBodies targetPosition;

    public ProbeMission(CelestialBodies initialPosition, CelestialBodies targetPosition) {
        this.targetPosition = targetPosition;
        this.probe = new Probe("Titanic 2.0", getInitialPosition(), getTargetPosition());
        this.currentTime = 0.0;
        this.missionComplete = false;
    }

    private Vector getTargetPosition() {
        return null;
    }

    public Vector getInitialPosition() {
    return null;
    }
    public void launchProbe(Probe probe, CelestialBodies launchPlanet) {
        this.probe = probe;
        if(probe.getRelativeVelocity(launchPlanet).magnitude() > MAX_INIT_VELOCITY)
            throw new IllegalArgumentException("nuh uh");
        else{probe.launch();
        }
    }

    public void updateMission(double stepSize) {
        if (missionComplete) return;

        currentTime += stepSize;

        if (currentTime > MAX_MISSION_TIME) {
            boolean missionFailed = true;
        }
    }
    public boolean isMissionComplete() {
        return missionComplete;
    }
    public double getCurrentTime() {
        return currentTime;
    }





    }

