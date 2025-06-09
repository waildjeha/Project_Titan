package com.ken10.Phase2.SolarSystemModel;




/**
 * Class for Probe mission.
 * implements multiple useful methods for titan mission.
 */
public class Probe extends CelestialBodies {
    public static final double MASS = 50000;
    private final Vector initialPosition;
    private final Vector initialVelocity;


    public Probe(String name, Vector position, Vector velocity) {
        super(name, position, velocity, MASS);
        this.initialPosition = position;
        this.initialVelocity = velocity;
    }
    public Probe(String name, Vector position, Vector velocity, double scaling, double size) {
        super(name, position, velocity, MASS, scaling, size);
        this.initialPosition = position;
        this.initialVelocity = velocity;
    }

    public Probe copy(){
        return new Probe(getName(), getPosition().copy(), getVelocity().copy());
    }

    @Override
    public String toString() {
        String output =  "\nPosition: " + getPosition() + "\n";
        output +=  "Velocity: " + getVelocity() + "\n";
        return output;
    }
}
