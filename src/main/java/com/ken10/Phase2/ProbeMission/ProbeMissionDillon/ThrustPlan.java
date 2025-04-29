package com.ken10.Phase2.ProbeMission.ProbeMissionDillon;


import com.ken10.Phase2.SolarSystemModel.Vector;

public class ThrustPlan {
    public double time;
    public Vector direction;
    public double magnitude;
    public double duration;

    public ThrustPlan(double time, Vector direction, double magnitude, double duration) {
        this.time = time;
        this.direction = direction.normalize();
        this.magnitude = magnitude;
        this.duration = duration;
    }
}