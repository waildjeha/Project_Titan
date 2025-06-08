package com.ken10.Phase2.SolarSystemModel;

/**
 * Abstract base class representing celestial bodies in the solar system.
 * Uses the Vector class to hold blueprints for creating planets and spacecraft.
 * Contains common properties like position, velocity, mass, and scaling factors.
 */
public abstract class CelestialBodies {
    protected String name;
    protected Vector position;
    protected Vector velocity;
    protected double mass;
    protected double distFromOrigin;
    protected double relativeScalingFactor;
    protected double size;

    /**
     * Creates a celestial body with basic properties.
     * 
     * @param name the name of the celestial body
     * @param position the initial position vector in 3D space
     * @param velocity the initial velocity vector
     * @param mass the mass of the body in kilograms
    */
    public CelestialBodies(String name, Vector position, Vector velocity, double mass) {
        this.name = name;
        this.position = position;
        this.velocity = velocity;
        this.mass = mass;
        this.distFromOrigin = position.magnitude();
        this.relativeScalingFactor = 1.0;
    }

    /**
     * Creates a celestial body with additional scaling and size properties.
     * 
     * @param name the name of the celestial body
     * @param position the initial position vector in 3D space
     * @param velocity the initial velocity vector
     * @param mass the mass of the body in kilograms
     * @param scaling the relative scaling factor for relative distance to the sun in the visualization
     * @param size the size of the body for rendering purposes in the visualization
    */
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

    /**
     * Prints detailed information about this celestial body to the console.
     * Includes position, velocity, and distance from origin with formatted output.
    */
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

    /**
     * Creates a deep copy of this celestial body.
     * Returns the appropriate subclass instance with copied vectors and properties.
     * 
     * @return a new instance that is a deep copy of this celestial body
    */
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

    /**
     * Returns the radius of this celestial body.
     * Default implementation returns 0; should be overridden by subclasses.
     * 
     * @return the radius in kilometers
    */
    public double getRadius() {
        return 0; // Placeholder, should be overridden by subclasses
    }

}
