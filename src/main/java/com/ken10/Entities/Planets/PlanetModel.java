package com.ken10.Entities.Planets;
import com.ken10.Entities.CelestialBodies;
import com.ken10.Other.Vector;

public class PlanetModel extends CelestialBodies {
    public PlanetModel(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }
    public void update(Vector acceleration, double dt) {
        velocity = velocity.add(acceleration.multiply(dt));
        position = position.add(velocity.multiply(dt));

    }
}