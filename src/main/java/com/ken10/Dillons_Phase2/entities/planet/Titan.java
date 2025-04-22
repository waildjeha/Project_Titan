package com.ken10.Dillons_Phase2.entities.planet;

import com.ken10.Dillons_Phase2.entities.Other.Vector;

public class Titan extends CelestialBodies {
    public final double RADIUS = 2575;
    public Titan(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }
}