package com.ken10.Phase2.SolarSystemModel;

/**
 * Contains information on titan including:
 * Radius and mass.
 */
public class Titan extends CelestialBodies {
    public static final double RADIUS = 2575;
    public static final double MASS = 1.35E+23;
    public Titan(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }
    public Titan(String name, Vector position, Vector velocity, double mass, double scaling, double size) {
        super(name, position, velocity, mass, scaling, size);
    }
}