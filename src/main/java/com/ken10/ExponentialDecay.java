package com.ken10;

import java.util.List;



public class ExponentialDecay implements OdeFunction{
    @Override
    public double[] evaluate (double time, double[] state){
        // state[0] = y(t)
        // ODE: dy/dt = -y

        double dydt = -state[0];
        return new double[] {dydt};
    }

    public static void main(String[] args) {
        compareSolvers();
    }

    public static void compareSolvers(){
        double[] stepSizes = {0.1, 0.05, 0.01, 0.005, 0.001, 0.0001, 0.00001, 0.000001};
        double startTime = 0.0;
        double endTime = 5.0;
        // we check just for 5 seconds, we can modify the endTime value

        //double[] initialState = {1.0};

        OdeFunction f = new ExponentialDecay();  // the test function is: dy/dt = -y

        System.out.println("StepSize\tSolver\tError\t\tRuntime(ms)");

        for (double h : stepSizes){
            // Euler

            double[] eulerInitialState = {1.0};
            ODESolver eulerSolver = new EulerSolver(f, eulerInitialState, startTime, endTime, h);
            long startEuler = System.nanoTime();
            List<ODESolver.TimeState> eulerResults = eulerSolver.solve();
            long endEuler = System.nanoTime();
            double eulerFinalY = eulerResults.get(eulerResults.size() - 1).state[0];
            double exact = Math.exp(-endTime);
            double eulerError = Math.abs(eulerFinalY - exact);
            double eulerRuntime = (endEuler - startEuler) / 1000000.0; //runtime in ms
            System.out.printf("%.5f  \tEuler\t%.6e\t%.3f\n", h, eulerError, eulerRuntime);

            // RK4
            double[] RK4InitialState = {1.0};
            ODESolver RK4Solver = new RungeKutta4Solver(f, RK4InitialState, startTime, endTime, h);
            long startRK4 = System.nanoTime();
            List<ODESolver.TimeState> RK4Results = RK4Solver.solve();
            long endRK4 = System.nanoTime();
            double RK4FinalY = RK4Results.get(RK4Results.size() - 1).state[0];
            double RK4Error = Math.abs(RK4FinalY - exact);
            double RK4Runtime = (endRK4 - startRK4) / 1000000.0; //runtime in ms
            System.out.printf("%.5f  \tRK4  \t%.6e\t%.3f\n", h, RK4Error, RK4Runtime);
        }
    }
}
