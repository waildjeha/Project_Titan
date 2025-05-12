package com.ken10.Phase2.entities;

import com.ken10.Phase2.newtonCalculations.Vector;

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

    /**
     * Calculates the relative position vector from this body to the target body.
     * @param target The target celestial body
     * @return Vector pointing from this body to the target body
     */
    public Vector getRelativePosition(CelestialBodies target) {
        return target.getPosition().subtract(this.position);
    }

    /**
     * Calculates the relative velocity vector between this body and the target body.
     * @param target The target celestial body
     * @return Vector representing the relative velocity (target velocity - this velocity)
     */
    public Vector getRelativeVelocity(CelestialBodies target) {
        return target.getVelocity().subtract(this.velocity);
    }

    /**
     * Applies thrust to the celestial body, changing its velocity.
     * The thrust is applied as an impulse: F = ma, where a = F/m
     * The velocity change is calculated as: Δv = a * t
     * 
     * @param direction The direction of thrust (will be normalized)
     * @param magnitude The magnitude of thrust in Newtons
     * @param duration The duration of thrust in seconds
     */
    public void applyThrust(Vector direction, double magnitude, double duration) {
        // Normalize the direction vector
        direction = direction.normalize();
        
        // Calculate acceleration: a = F/m
        Vector acceleration = direction.multiply(magnitude / this.mass);
        
        // Calculate velocity change: Δv = a * t
        Vector velocityChange = acceleration.multiply(duration);
        
        // Update velocity
        this.velocity = this.velocity.add(velocityChange);
    }

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
        if(this instanceof PlanetModel) return new PlanetModel(getName(), getPosition().copy(),
                getVelocity().copy(), getMass());
        else return new Probe(getName(), getPosition().copy(),
                    getVelocity().copy());
    }
}
