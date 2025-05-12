package com.ken10.Phase2.entities;

import com.ken10.Phase2.newtonCalculations.Vector;

/**
 * Planet case of celestial body.
 */
public class PlanetModel extends CelestialBodies {
    public PlanetModel(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }

}