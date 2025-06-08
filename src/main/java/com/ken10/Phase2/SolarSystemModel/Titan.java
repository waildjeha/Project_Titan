package com.ken10.Phase2.SolarSystemModel;

/**
 * Represents Saturn's moon Titan in the solar system simulation.
 * Extends CelestialBodies with Titan's specific physical properties,
 * particularly its radius for surface gravity calculations.
*/
public class Titan extends CelestialBodies {
    public static final double RADIUS = 2575; // Titan's radius in kilometers.
    
    public Titan(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }
    public Titan(String name, Vector position, Vector velocity, double mass, double scaling, double size) {
        super(name, position, velocity, mass, scaling, size);
    }

    public double getRadius() {
        return RADIUS;
    }
}