package com.ken10.Phase2.landing;

import com.ken10.Phase2.SolarSystemModel.Probe;
import com.ken10.Phase2.landing.StateVector;

import javafx.scene.paint.Color;

public class LandingModule {

    private String name;
    private Vector2 position;
    private Vector2 velocity;
    public StateVector state;
    private static final double MASS = 50000; // Mass in kg
    private Vector2 thrust = new Vector2(0, 0);

    

    public LandingModule (String name, Vector2 initialPos, Vector2 initialVel) {
        this.name = name;
        this.position = initialPos;
        this.velocity = initialVel;
    }

    public double getX() {
        return state.getX();
    }

    public double getY() {
        return state.getY();
    }

    public double getRotationAngle() {
        return state.getTheta();
    }

    public Vector2 getThrust() {
        return thrust;
    }

    public double getTotalSpeed() {
        return new Vector2(state.getVx(), state.getVy()).getLength();
    }

    public void setThrust(Vector2 thrust) {
        this.thrust = thrust;
    }
}
