package com.ken10.entities.probe;

import com.ken10.Other.Vector;
import com.ken10.entities.CelestialBodies;

public class ProbeMission {
    private final double MAX_MISSION_TIME = 1.0;
    private final double MAX_INIT_VELOCITY = 60.0;
    private Probe probe;
    private double currentTime;
    private boolean missionComplete;
    private Vector probePosition;
    private Vector targetPosition;
    private CelestialBodies target;

    public ProbeMission(Vector probePosition, Vector targetPosition) {
        this.probePosition = probePosition;
        this.targetPosition = targetPosition;
        this.currentTime = 0.0;
        this.missionComplete = false;
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
