package com.ken10.Phase2.SolarSystemModel;

/**
 * Planet case of celestial body.
 */
public class PlanetModel extends CelestialBodies {
    public PlanetModel(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }
    public PlanetModel(String name, Vector position, Vector velocity, double mass, double scaling, double size){
        super(name, position, velocity, mass, scaling, size);
    }

}