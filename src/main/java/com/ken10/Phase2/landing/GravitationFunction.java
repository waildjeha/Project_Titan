package com.ken10.Phase2.landing;

import com.ken10.Phase2.SolarSystemModel.Vector;

public class GravitationFunction implements Function {
        /**
     * gravitational constant
     */
    public static final double G = 6.6743e-20;
    public static final double TITAN_MASS = 1.3452e23; // kg
    public static final Vector2 TITAN_POSITION = new Vector2(0.0, 0.0); // fixed origin
    /**
     * Calculates all the gravitational forces that are working in the system.
     * Iterates over all the objects based on the masses vector.
     * <p>
     * Gravitational force of all the objects j pulling on object i are calculated with this formula:
     * <p>
     *  F = - sum( G * mi * mj * (pi - pj) / (||pi - pj||^3)
     * <p>
     * where: <br>
     * - mi and mj are the masses of the objects i and j    <br>
     * - pi and pj are the positions of the objects i and j <br>
     *
     * @param positions the positions of the objects (every 3 entries, the next object is referenced)
     * @param ignore this function does not use this vector in its calculations
     * @param masses the masses of the objects (same order as in the positions)
     * @return a vector of the gravitational forces working in the system
     */
    @Override
    public Vector f(StateVector state, Vector2 masses, double h, double t) {
        double x = state.getX();
        double y = state.getY();
        double z = 0;
        double[] result = new double[2];
        Vector2 landerPosition = new Vector2(x, y);
        double landerMass = 50000; // kg, mass of the lander

        Vector2 r = landerPosition; // Vector from Titan to lander
        double distance = r.getLength();
                Vector2 gravitationalForce = r.multiply(
            -G * TITAN_MASS * landerMass / Math.pow(distance, 3)
        );

        return new Vector(gravitationalForce.getX(), gravitationalForce.getY(), 0);
    }
        
}
