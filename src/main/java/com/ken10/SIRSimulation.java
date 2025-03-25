package com.ken10;

import java.util.List;

public class SIRSimulation {

    /**
     * Defines the SIR model differential equations.
     */
    public static class SIRModel implements OdeFunction {
        private final double kappa; //infection rate
        private final double gamma; //recovery rate
        private final double mu; //birth/death rate

        /**
         * Constructor with model parameters
         *  @param kappa infection rate
         *  @param gamma recovery rate
         * @param mu birth/death rate
         */
        public SIRModel(double kappa, double gamma, double mu) {
            this.kappa = kappa;
            this.gamma = gamma;
            this.mu = mu;
        }

        @Override
        public double[] evaluate(double time, double[] state) {
            double[] derivatives = new double[state.length];

            double S = state[0]; //susceptible population
            double I = state[1]; //infected population
            double R = state[2]; //recovered population

            // SIR equations:
            derivatives[0] = -kappa * S * I + mu * (1 - S);  // dS/dt
            derivatives[1] = kappa * S * I - (gamma + mu) * I;  // dI/dt
            derivatives[2] = gamma * I - mu * R;  // dR/dt

            return derivatives;
        }
    }

    public static void main(String[] args) {

        double kappa = 0.2;  //infection rate
        double gamma = 0.1;  //recovery rate
        double mu = 0.03;    //birth/death rate

        // Initial populations
        double[] initialState = {0.99, 0.01, 0.0}; // 99% Susceptible, 1% Infected, 0% Recovered

        double startTime = 0.0;
        double endTime = 100.0; // Simulate for 100 time units (e.g., days)
        double stepSize = 0.1;  // Step size

        OdeFunction sirModel = new SIRModel(kappa, gamma, mu);

        // Create solvers
        ODESolver eulerSolver = new EulerSolver(sirModel, initialState, startTime, endTime, stepSize);
        ODESolver rk4Solver = new RungeKutta4Solver(sirModel, initialState, startTime, endTime, stepSize);

        // Run simulations
        System.out.println("Running Euler solver...");
        List<ODESolver.TimeState> eulerResults = eulerSolver.solve();

        System.out.println("Running Runge-Kutta 4 solver...");
        List<ODESolver.TimeState> rk4Results = rk4Solver.solve();

        // Print results
        System.out.println("\nEuler Method Results:");
        printResults(eulerResults, 0, 25, 50, 75, 100);

        System.out.println("\nRunge-Kutta 4 Method Results:");
        printResults(rk4Results, 0, 25, 50, 75, 100);

        // Compare final states
        System.out.println("\nSimulation complete!");
        System.out.printf("Euler final state: S=%.4f, I=%.4f, R=%.4f\n",
                eulerResults.get(eulerResults.size() - 1).state[0],
                eulerResults.get(eulerResults.size() - 1).state[1],
                eulerResults.get(eulerResults.size() - 1).state[2]);

        System.out.printf("RK4 final state: S=%.4f, I=%.4f, R=%.4f\n",
                rk4Results.get(rk4Results.size() - 1).state[0],
                rk4Results.get(rk4Results.size() - 1).state[1],
                rk4Results.get(rk4Results.size() - 1).state[2]);
    }

    /**
     * Helper method to print results at specific time points
     */
    private static void printResults(List<ODESolver.TimeState> results, double... times) {
        System.out.println("Time\tS\t\tI\t\tR");
        System.out.println("-------------------------------------");

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
                System.out.printf("%.1f\t%.4f\t%.4f\t%.4f\n",
                        closest.time, closest.state[0], closest.state[1], closest.state[2]);
            }
        }
    }
}
