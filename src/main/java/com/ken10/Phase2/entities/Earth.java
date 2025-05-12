package com.ken10.Phase2.entities;


import com.ken10.Phase2.newtonCalculations.Vector;

public class Earth extends CelestialBodies {
    public final double RADIUS = 6370;
    public Earth(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
}
}
