package com.ken10.Dillons_Phase2.entities.planet;
import com.ken10.Dillons_Phase2.entities.Other.Vector;

/**
 * Planet case of celestial body.
 */
public class PlanetModel extends CelestialBodies {
    public PlanetModel(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }

}