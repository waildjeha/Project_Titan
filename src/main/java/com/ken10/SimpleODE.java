package com.ken10;

import java.util.List;

public class SimpleODE implements OdeFunction {
    @Override
    public double[] evaluate(double time, double[] state) {
        // state[0] = y(t)
        // ODE: dy/dt = t + y
        double dydt = time + state[0];
        return new double[] {dydt};
    }

    private double analyticalSolution(double t) {
        // y(t) = 2e^t - t - 1
        return 2 * Math.exp(t) - t - 1;
    }

    public static void main(String[] args) {
        compareSolvers();
    }

    public static void compareSolvers() {
        double[] stepSizes = {0.2, 0.1, 0.05, 0.025, 0.0125, 0.00625};
        double startTime = 0.0;
        double endTime = 1.0;

        OdeFunction f = new SimpleODE();


        System.out.println("# StepSize\tEulerError\tEulerTime(ms)\tRK4Error\tRK4Time(ms)");

        for (double h : stepSizes) {
            double[] eulerInitialState = {1.0};
            ODESolver eulerSolver = new EulerSolver(f, eulerInitialState, startTime, endTime, h);
            long eulerStartTime = System.nanoTime();
            List<ODESolver.TimeState> eulerResults = eulerSolver.solve();
            long eulerEndTime = System.nanoTime();
            double eulerTime = (eulerEndTime - eulerStartTime) / 1_000_000.0;

            // Calculate max error for Euler
            double eulerMaxError = 0.0;
            for (ODESolver.TimeState state : eulerResults) {
                double exact = ((SimpleODE)f).analyticalSolution(state.time);
                double error = Math.abs(state.state[0] - exact);
                eulerMaxError = Math.max(eulerMaxError, error);
            }

            // RK4
            double[] RK4InitialState = {1.0};
            RungeKutta4Solver RK4Solver = new RungeKutta4Solver(f, RK4InitialState, startTime, endTime, h);
            RK4Solver.setDebugOutput(false);
            long rk4StartTime = System.nanoTime();
            List<ODESolver.TimeState> RK4Results = RK4Solver.solve();
            long rk4EndTime = System.nanoTime();
            double rk4Time = (rk4EndTime - rk4StartTime) / 1_000_000.0;

            // Calculate max error for RK4
            double rk4MaxError = 0.0;
            for (ODESolver.TimeState state : RK4Results) {
                double exact = ((SimpleODE)f).analyticalSolution(state.time);
                double error = Math.abs(state.state[0] - exact);
                rk4MaxError = Math.max(rk4MaxError, error);
            }

            // Print the result
            System.out.printf("%.6f\t%.2e\t%.2f\t%.2e\t%.2f\n",
                    h, eulerMaxError, eulerTime, rk4MaxError, rk4Time);
        }
    }
}