package com.ken10.Entities;
import com.ken10.Other.Vector;
/**
 * Uses the Vector class to hold blueprints for creating Planets and the rocket.
 * Mostly self-explanatory.
 */

public abstract class CelestialBodies {
    protected String name;
    protected Vector position;
    protected Vector velocity;
    protected double mass;

    public CelestialBodies(String name, Vector position, Vector velocity, double mass) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
    }
    public abstract void update(Vector acceleration, double time);
    public String getName() {return name;}
    public Vector getPosition() {return position;}
    public Vector getVelocity() {return velocity;}
    public double getMass() {return mass;}
    public void setPosition(Vector position) {this.position = position;}
    public void setVelocity(Vector velocity) {this.velocity = velocity;}

    public void recordHistory(double time) {}

    public String toString() {
        return " [name=" + name + ", position=" + position + ", velocity=" + velocity + ", mass=" + mass + "]";
    }

}
