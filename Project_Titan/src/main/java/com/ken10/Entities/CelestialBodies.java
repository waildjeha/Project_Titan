package com.ken10.Entities;
import com.ken10.Other.Vector;
/*Uses the Vector class to hold blueprints for creating Planets and the rocket.*/

public abstract class CelestialBodies {
    protected Vector position;
    protected Vector velocity;
    protected double mass;

    public CelestialBodies(Vector position, Vector velocity, double mass) {
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
    }
    public abstract void update(double time);

}
