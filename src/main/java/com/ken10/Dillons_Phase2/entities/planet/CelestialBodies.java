package com.ken10.Dillons_Phase2.entities.planet;
import com.ken10.Dillons_Phase2.entities.Other.Vector;
import com.ken10.Dillons_Phase2.entities.probe.Probe;
import com.ken10.Dillons_Phase2.entities.rocket.Rocket;

/**
 * Uses the Vector class to hold blueprints for creating Planets and the rocket.
 * Mostly self-explanatory.
 */

public abstract class CelestialBodies {
    protected String name;
    protected Vector position;
    protected Vector velocity;
    protected double mass;
    protected double distFromOrigin;

    public CelestialBodies(String name, Vector position, Vector velocity, double mass) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
        this.distFromOrigin = position.magnitude();
    }
    public String getName() {return name;}
    public Vector getPosition() {return position;}
    public Vector getVelocity() {return velocity;}
    public double getMass() {return mass;}
    public void setMass(double mass) {this.mass = mass;}
    public void setPosition(Vector position) {this.position = position;}
    public void setVelocity(Vector velocity) {this.velocity = velocity;}
    public double getDistFromOrigin() {return distFromOrigin;}

    public void printBody(){
        Vector pos = getPosition();
        Vector vel = getVelocity();
        System.out.printf("%s:\n", getName());
        System.out.printf("  Position: (%.2f, %.2f, %.2f)\n", pos.getX(), pos.getY(), pos.getZ());
        System.out.printf("  Velocity: (%.2f, %.2f, %.10f)\n", vel.getX(), vel.getY(), vel.getZ());
        System.out.printf("  Distance from origin: %.2f\n", pos.magnitude());
        System.out.println("----------------------------------------");
    }

    public CelestialBodies deepCopy(){
        return (this instanceof PlanetModel)
                ? new PlanetModel(getName(), getPosition().copy(),
                getVelocity().copy(), getMass())
                : (this instanceof Rocket)
                ? new Rocket(getName(), getPosition().copy(),
                getVelocity().copy(), getMass())
                : new Probe(getName(), getPosition().copy(),
                getVelocity().copy());
    }

}
