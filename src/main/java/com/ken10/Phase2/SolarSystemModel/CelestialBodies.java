package com.ken10.Phase2.SolarSystemModel;

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
    protected double relativeScalingFactor;
    protected double size;

    public CelestialBodies(String name, Vector position, Vector velocity, double mass) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
        this.distFromOrigin = position.magnitude();
        this.relativeScalingFactor = 1.0;
    }
    public CelestialBodies(String name, Vector position, Vector velocity, double mass, double scaling, double size){
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
        this.distFromOrigin = position.magnitude();
        this.relativeScalingFactor = scaling;
        this.size = size;
    }
    public String getName() {return name;}
    public Vector getPosition() {return position;}
    public Vector getVelocity() {return velocity;}
    public double getMass() {return mass;}
    public void setMass(double mass) {this.mass = mass;}
    public void setPosition(Vector position) {this.position = position;}
    public void setVelocity(Vector velocity) {this.velocity = velocity;}
    public void setSize(double newSize){this.size = newSize;}
    public double getSize(){return this.size;}
    public double getDistFromOrigin() {return distFromOrigin;}
    public double getRelativeScalingFactor(){return this.relativeScalingFactor;}

    public void printBody(){
        Vector pos = getPosition();
        Vector vel = getVelocity();
        System.out.printf("%s:\n", getName());
        System.out.printf("  Position: (%.2f, %.2f, %.2f)\n", pos.getX(), pos.getY(), pos.getZ());
        System.out.printf("  Velocity: (%.2f, %.2f, %.10f)\n", vel.getX(), vel.getY(), vel.getZ());
        System.out.printf("  Distance from origin: %.2f\n", pos.magnitude());
        System.out.println("----------------------------------------");
    }
    @Override
    public String toString(){
        return getName() + ", \nposition" + getPosition() + ", \nvelocity" + getVelocity() + ", \nmagnitude" + getPosition().magnitude() ;
    }

    public CelestialBodies deepCopy(){
        if(this instanceof PlanetModel) return new PlanetModel(getName(), getPosition().copy(),
                getVelocity().copy(), getMass(), getRelativeScalingFactor(), getSize());
        else if (this instanceof Earth) return new Earth(getName(), getPosition().copy(),
                getVelocity().copy(), getMass(), getRelativeScalingFactor(), getSize());
        else if (this instanceof Titan) return new Titan(getName(), getPosition().copy(),
                getVelocity().copy(), getMass(), getRelativeScalingFactor(), getSize());
        else return new Probe(getName(), getPosition().copy(),
                    getVelocity().copy(), getRelativeScalingFactor(), getSize());
    }

}
