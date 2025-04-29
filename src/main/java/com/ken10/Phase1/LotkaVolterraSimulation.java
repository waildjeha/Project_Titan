package com.ken10.Phase1;

import java.util.List;

public class LotkaVolterraSimulation {

    /**
     * Defines the OdeFunction (derivatives) for the Lotka-Volterra predator-prey
     * model
     */
    public static class LotkaVolterraDerivatives implements OdeFunction {
        private final double alpha; // prey growth rate
        private final double beta; // predation rate
        private final double gamma; // predator death rate
        private final double delta; // predator growth rate from consuming prey

        /**
         * Constructor with model parameters
         * 
         * @param alpha prey growth rate
         * @param beta  predation rate
         * @param gamma predator death rate
         * @param delta predator growth from consuming prey
         */
        public LotkaVolterraDerivatives(double alpha, double beta, double gamma, double delta) {
            this.alpha = alpha;
            this.beta = beta;
            this.gamma = gamma;
            this.delta = delta;
        }

        @Override
        public double[] evaluate(double time, double[] state) {
            // state[0] = prey population (x)
            // state[1] = predator population (y)

            double[] derivatives = new double[state.length];

            // Prey equation: dx/dt = αx - βxy
            derivatives[0] = alpha * state[0] - beta * state[0] * state[1];

            // Predator equation: dy/dt = -γy + δxy
            derivatives[1] = -gamma * state[1] + delta * state[0] * state[1];

            return derivatives;
        }
    }

    /**
     * Demonstrate simulation using Lotka-Volterra model
     */
    public static void main(String[] args) {
        // Model parameters
        double alpha = 0.1; // prey growth rate
        double beta = 0.02; // predation rate
        double gamma = 0.3; // predator death rate
        double delta = 0.01; // predator growth from consuming prey

        // Initial populations
        double[] initialState = new double[2];
        initialState[0] = 40.0; // initial prey population
        initialState[1] = 9.0; // initial predator population

        double startTime = 0.0;
        double endTime = 200.0; // run simulation for 200 time units
        double stepSize = 0.1; // time units per step

        OdeFunction derivatives = new LotkaVolterraDerivatives(alpha, beta, gamma, delta);

        // Create solvers
        ODESolver eulerSolver = new EulerSolver(derivatives, initialState, startTime, endTime, stepSize);
        ODESolver rk4Solver = new RungeKutta4Solver(derivatives, initialState, startTime, endTime, stepSize);

        // Run simulations
        System.out.println("Running Euler solver...");
        List<ODESolver.TimeState> eulerResults = eulerSolver.solve();

        System.out.println("Running Runge-Kutta 4 solver...");
        List<ODESolver.TimeState> rk4Results = rk4Solver.solve();

        // Print results at specific time points
        System.out.println("\nEuler Method Results:");
        printResults(eulerResults, 0, 50, 100, 150, 200);

        System.out.println("\nRunge-Kutta 4 Method Results:");
        printResults(rk4Results, 0, 50, 100, 150, 200);

        // Compare final states
        System.out.println("\nSimulation complete!");
        System.out.println("Euler final state: Prey=" +
                eulerResults.get(eulerResults.size() - 1).state[0] +
                ", Predator=" + eulerResults.get(eulerResults.size() - 1).state[1]);
        System.out.println("RK4 final state: Prey=" +
                rk4Results.get(rk4Results.size() - 1).state[0] +
                ", Predator=" + rk4Results.get(rk4Results.size() - 1).state[1]);
    }

    /**
     * Helper method to print results at specific time points
     */
    private static void printResults(List<ODESolver.TimeState> results, double... times) {
        System.out.println("Time\tPrey\tPredator");
        System.out.println("-----------------------------");

        for (double targetTime : times) {
            // Find the closest time point in results
            ODESolver.TimeState closest = null;
            double minDiff = Double.MAX_VALUE;

            for (ODESolver.TimeState ts : results) {
                double diff = Math.abs(ts.time - targetTime);
                if (diff < minDiff) {
                    minDiff = diff;
                    closest = ts;
                }
            }

            if (closest != null) {
                System.out.printf("%.1f\t%.2f\t%.2f\n",
                        closest.time, closest.state[0], closest.state[1]);
            }
        }
    }
}
