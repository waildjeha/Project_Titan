package com.ken10.Phase2.SolarSystemModel;

public class Titan extends CelestialBodies {
    public static final double RADIUS = 2575;
    public Titan(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }
    public Titan(String name, Vector position, Vector velocity, double mass, double scaling, double size) {
        super(name, position, velocity, mass, scaling, size);
    }
}