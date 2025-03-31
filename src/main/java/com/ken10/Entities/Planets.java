package com.ken10.Entities;
import com.ken10.Other.Vector;

public class Planets extends CelestialBodies {
    private String planetName;

    public Planets(String planetName, Vector position,Vector velocity, double mass) {
        super(position,velocity,mass);
        this.planetName = planetName;
    }
    public String getPlanetName() {
        return planetName;
    }
    public double getMass() {return mass;}
    public Vector getPosition() {return position;}
    public Vector getVelocity() {return velocity;}
    @Override
    public void update(double time) {
    }
    public String toString(){
        return planetName + position + velocity + mass;
    }
}
