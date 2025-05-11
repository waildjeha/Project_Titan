package com.ken10.unused;

import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Vector;

public class Titan extends CelestialBodies {
    public final double RADIUS = 2575;
    public Titan(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }
}