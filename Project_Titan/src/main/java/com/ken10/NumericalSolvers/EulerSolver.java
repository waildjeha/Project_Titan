package com.ken10.NumericalSolvers;

import com.ken10.ODEs.ODESolver;
import com.ken10.ODEs.OdeFunction;

public class EulerSolver extends ODESolver {

    public EulerSolver(OdeFunction derivativeFunction, double[] initialState,
                       double startTime, double endTime, double stepSize) {
        super(derivativeFunction, initialState, startTime, endTime, stepSize);
    }

    @Override
    public void step() {
        double[] derivatives = derivativeFunction.evaluate(time, state); // The ODE that we want to solve

        // Update each dimension of state
        for (int i = 0; i < state.length; i++) {
            state[i] = state[i] + stepSize * derivatives[i]; // the index i refered to the different componenets of our
                                                             // state vector
        }

        time += stepSize;
    }
}
