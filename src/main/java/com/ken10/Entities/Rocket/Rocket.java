package com.ken10.Entities.Rocket;
import com.ken10.Entities.CelestialBodies;
import com.ken10.Other.Vector;

public class Rocket extends CelestialBodies {
    private static final double TitanRadius = 2575.0; // km
    private RocketState state;
    private boolean hasLanded = false;
    private double closestApproachToTitan = Double.MAX_VALUE;
    private double timeOfClosestApproach = 0;

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

    @Override
    public void update(Vector acceleration, double dt) {
        velocity = velocity.add(acceleration.multiply(dt));
        position = position.add(velocity.multiply(dt));
    }

    public boolean hasLanded() {
        return hasLanded;
    }

    public void checkLanding(Vector titanPosition) {
        double distance = position.subtract(titanPosition).magnitude();
        if (distance <= TitanRadius) {
            hasLanded = true;
            setState(new GroundState()); // Change state to landed
        }
        if (distance < closestApproachToTitan) {
            closestApproachToTitan = distance;
        }
    }

    public double getClosestApproachToTitan() {
        return closestApproachToTitan;
    }

    public double getTimeOfClosestApproach() {
        return timeOfClosestApproach;
    }

    public void setTimeOfClosestApproach(double time) {
        timeOfClosestApproach = time;
    }
}
