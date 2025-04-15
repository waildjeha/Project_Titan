package com.ken10.entities.rocket;
import com.ken10.entities.CelestialBodies;
import com.ken10.Other.Vector;

/**
 * Uses properties of celestial body.
 * isn't the exact same as a planet so had to create a new object.
 * Rk4 can still model tho.
 */
public class Rocket extends CelestialBodies {
    private RocketState state;
    private boolean hasLanded = false;
    private double planetRadius;
    private double fuel;

    public Rocket(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
        this.state = new GroundState();
    }

    public void setState(RocketState state) {
        this.state = state;
    }

    public void updateState() {
        state.handle(this);
    }

    public boolean hasLanded() {
        return hasLanded;
    }

    public void checkLanding(Vector planetPosition) {
        double distance = position.subtract(planetPosition).magnitude();
        if (distance <= planetRadius) {
            hasLanded = true;
            setState(new GroundState()); // Change state to landed
        }
    }

    public double getFuel(){return fuel;}
}
