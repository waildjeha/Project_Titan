package com.ken10.Phase2.entities;

import com.ken10.Phase2.newtonCalculations.Vector;

public class Titan extends CelestialBodies {
    public final double RADIUS = 2575;
    public Titan(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }
}