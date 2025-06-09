package com.ken10.Phase2.landing;

import com.ken10.Phase2.*;
import com.ken10.Phase1.ODESolver;
import com.ken10.Phase1.OdeFunction;
import com.ken10.Phase1.RungeKutta4Solver;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 * Open-Loop Landing Simulation that automatically stops printing
 * only when vertical velocity is effectively zero at the ground.
 */
public class OpenLoopController {

    // Titan gravity (km/s²)
    private static final double G      = LandingGravitationFunction.GRAVITATIONAL_ACCELERATION;
    // Max thrust acceleration magnitude (km/s²)
    private static final double UMAX   = 0.01352;
    // vertical net decel during brake (km/s²)
    private static final double A2     = UMAX - G;
    // tolerance for “landed” speeds
    private static final double V_EPS  = 1e-5;

    /**
     * Computes the free-fall time t1 such that, once you brake at full thrust,
     * the lander reaches y = 0 with zero vertical speed.
     */
    private static double findBurnStart(double y0, double vy0) {
        double lo = 0;
        double hi = Math.sqrt(2*y0/G) + 10;
        for (int i = 0; i < 50; i++) {
            double mid = 0.5*(lo + hi);
            double vy1 = vy0 - G * mid;
            double y1  = y0 + vy0 * mid - 0.5 * G * mid * mid;
            double tBurn = -vy1 / A2;
            double y2 = y1 + vy1 * tBurn + 0.5 * A2 * tBurn * tBurn;
            if (y2 > 0) lo = mid; else hi = mid;
        }
        return 0.5*(lo + hi);
    }

    /**
     * OdeFunction for 2D motion: vertical thrust in three phases,
     * and horizontal braking in one phase so vx->0 at t2.
     */
    public static class MultiPhaseOde implements OdeFunction {
        private final double t1, t2, t_h1;
        public MultiPhaseOde(double t1, double t2, double t_h1) {
            this.t1 = t1;
            this.t2 = t2;
            this.t_h1 = t_h1;
        }
        @Override
        public double[] evaluate(double t, double[] s) {
            // s = [x,y,θ,vx,vy,ω]
            // vertical thrust schedule
            double u;
            if (t < t1)       u = 0.0;
            else if (t < t2)  u = UMAX;
            else              u = G;   // hover-compensate
            double ay = u - G;

            // horizontal brake schedule
            double ax;
            if (t < t_h1)     ax = 0.0;
            else if (t < t2)  ax = -UMAX;
            else              ax = 0.0;

            return new double[]{
                s[3],   // dx/dt = vx
                s[4],   // dy/dt = vy
                0.0,    // dθ/dt = ω
                ax,     // dvx/dt = ax
                ay,     // dvy/dt = ay
                0.0     // dω/dt = 0
            };
        }
    }

    public static void main(String[] args) {
        // Initial conditions
        double y0   = 185.0;                  // km
        double vy0  = -0.1;                   // km/s downward
        double vx0  = 1.7973910345639597;     // km/s horizontal

        // Compute vertical burn times
        double t1    = findBurnStart(y0, vy0);
        double vy1   = vy0 - G * t1;
        double tBurn = -vy1 / A2;
        double t2    = t1 + tBurn;
        // Compute horizontal brake start so vx->0 at t2
        double t_hbrake = vx0 / UMAX;
        double t_h1     = t2 - t_hbrake;

        System.out.printf(
          "Burn vert: t1=%.3f s to t2=%.3f s; " +
          "Burn horiz start at t=%.3f s%n",
          t1, t2, t_h1
        );

        try (PrintWriter csv = new PrintWriter(new FileWriter("landing_profile.csv"))) {
            csv.println("t,y,vy,vx");

            OdeFunction ode = new MultiPhaseOde(t1, t2, t_h1);
            double[] init = {0.0, y0, 0.0, vx0, vy0, 0.0};
            double t0 = 0.0, tEnd = t2 + 200, h = 0.001;

            RungeKutta4Solver solver =
              new RungeKutta4Solver(ode, init, t0, tEnd, h);
            List<ODESolver.TimeState> history = solver.solve();

            for (ODESolver.TimeState ts : history) {
                double t  = ts.time;
                double y  = ts.state[1];
                double vy = ts.state[4];
                double vx = ts.state[3];
                String phase = (t < t1 ? "FREEFALL"
                                : t < t2 ? "BRAKE"
                                :           "HOVER");

                System.out.printf(
                  "t=%7.2f s => y=%8.3f km, vy=%8.6f km/s, vx=%8.6f km/s, phase=%s%n",
                  t, y, vy, vx, phase
                );
                csv.printf("%.6f,%.6f,%.6f,%.6f%n", t, y, vy, vx);

                if (Math.abs(vy) < V_EPS && Math.abs(vx) < V_EPS) {
                    System.out.printf(
                      ">>> Landed at t=%.3f s, vy≈%.6f km/s, vx≈%.6f km/s%n",
                      t, vy, vx
                    );
                    break;
                }
            }

        } catch (IOException e) {
            System.err.println("Error writing CSV: " + e.getMessage());
        }
    }
}
