package com.ken10.Phase1;

public class RungeKutta4Solver extends ODESolver {

    public RungeKutta4Solver(OdeFunction derivativeFunction, double[] initialState,
                             double startTime, double endTime, double stepSize) {
        super(derivativeFunction, initialState, startTime, endTime, stepSize);
    }

    @Override
    public void step() {
        double h = stepSize;
        double t = time;
        double[] y = state;
        int n = state.length;

        // First stage
        double[] k1 = derivativeFunction.evaluate(t, y);

        // Second stage
        double t2 = t + h / 2;
        double[] y2 = new double[n];
        for (int i = 0; i < n; i++) {
            y2[i] = y[i] + (h / 2) * k1[i];
        }
        double[] k2 = derivativeFunction.evaluate(t2, y2);

        // Third stage
        double t3 = t + h / 2;
        double[] y3 = new double[n];
        for (int i = 0; i < n; i++) {
            y3[i] = y[i] + (h / 2) * k2[i];
        }
        double[] k3 = derivativeFunction.evaluate(t3, y3);

        // Fourth stage
        double t4 = t + h;
        double[] y4 = new double[n];
        for (int i = 0; i < n; i++) {
            y4[i] = y[i] + h * k3[i];
        }
        double[] k4 = derivativeFunction.evaluate(t4, y4);

        // Combine stages to update state
        for (int i = 0; i < n; i++) {
            state[i] = y[i] + (h / 6) * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]);
        }

        time += stepSize;
    }
}
