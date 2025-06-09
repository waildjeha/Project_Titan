package com.ken10.Phase2.landing;

import com.ken10.Phase2.SolarSystemModel.Vector;

public class LandingGravitationFunction implements Function {

    public static final double GRAVITATIONAL_ACCELERATION = 0.001352;

    @Override
    public Vector f(StateVector state, Vector2 engineThrust, double h, double t) {
        double u = Math.min(0.01352, Math.abs(engineThrust.getX()));
        double v;
        if (engineThrust.getY() >= 0) {
            v = Math.min(1, engineThrust.getY());
        } else {
            v = Math.max(-1, engineThrust.getY());
        }

        Vector result = new Vector(
                u * Math.sin(Math.toRadians(state.getTheta())),
                u * Math.cos(Math.toRadians(state.getTheta())) - GRAVITATIONAL_ACCELERATION,
                v);
        return result;
    }
}
