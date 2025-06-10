package com.ken10.Phase2.landing;

import com.ken10.Phase2.SolarSystemModel.Vector;


/**
 * This class implements the gravitational function for the landing phase of the spacecraft.
 * It calculates the resulting vector based on the current state, engine thrust, and gravitational acceleration.
 */
public class LandingGravitationFunction implements Function {

    public static final double GRAVITATIONAL_ACCELERATION = 0.001352;

    /**
     * Calculates the gravitational force vector based on the current state, engine thrust, and time step.
     *
     * @param state         The current state of the spacecraft, including position and velocity.
     * @param engineThrust  The thrust vector from the spacecraft's engines.
     * @param h             The time step for the calculation.
     * @param t             The current time (not used in this function).
     * @return A vector representing the gravitational force acting on the spacecraft.
     */
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