package com.ken10.Phase2.landing;

import java.util.function.Function;

import com.ken10.Phase2.SolarSystemModel.Vector;

public abstract class LandingDifferentialEquation {


    /**
     * this method calculates the result of the differential equation for positions and velocities
     * @param f function to calculate the derivative of the velocities
     * @param positions vector of all the positions
     * @param velocities vector of all the velocities
     * @param masses masses of the objects
     */
    public static Vector[] solve(LandingGravitationFunction f, Vector positions, Vector velocities, Vector masses, double h, double t) {
        // Vector[] result = new Vector[2];

        // result[0] = velocities;
        // result[1] = f.f(positions, velocities, masses, h, t);

        return null;
    }
}
