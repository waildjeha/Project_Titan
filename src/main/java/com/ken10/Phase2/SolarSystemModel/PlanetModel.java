package com.ken10.Phase2.SolarSystemModel;

/**
 * Generic planet implementation of CelestialBodies.
 * Used for most planets in the solar system that don't require
 * specialized behavior or properties.
*/
public class PlanetModel extends CelestialBodies {
    public PlanetModel(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }
    public PlanetModel(String name, Vector position, Vector velocity, double mass, double scaling, double size){
        super(name, position, velocity, mass, scaling, size);
    }

    public double getRadius() {
        return 0; // Placeholder, should be overridden by subclasses
    }

}