package com.ken10.Entities;
import com.ken10.Other.Vector;

public class Rocket extends CelestialBodies {
    private RocketState state;

    public Rocket(Vector position, Vector velocity, double mass) {
        super(position, velocity, mass);
        this.state = new GroundState();
    }
    public void setState(RocketState state) {
        this.state = state;
    }
    public void updateState() {
        state.handle(this);
    }

    @Override
    public void update(double time) {
        position = position.add(velocity.multiply(time));
    }
}
