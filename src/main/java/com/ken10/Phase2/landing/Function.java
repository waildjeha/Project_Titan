package com.ken10.Phase2.landing;

import com.ken10.Phase2.SolarSystemModel.Vector;
import com.ken10.Phase2.StatesCalculations.Solver;

public interface Function {
    /**
     *
     * @param v1 positions (used like this in the {@link Solver} instances of the program)
     * @param v2 velocities (used like this in the {@link Solver} instances of the program)
     * @param v3 masses (used like this in the {@link Solver} instances of the program)
     * @return resulting Vector of the function
     */
    Vector f(StateVector state, Vector2 v3, double h, double t);
}
