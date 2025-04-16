package com.ken10.entities.probe;

import com.ken10.Other.Vector;

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