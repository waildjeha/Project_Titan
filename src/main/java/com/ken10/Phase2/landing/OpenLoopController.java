package com.ken10.Phase2.landing;

import com.ken10.Phase2.*;
import com.ken10.Phase1.ODESolver;
import com.ken10.Phase1.OdeFunction;
import com.ken10.Phase1.RungeKutta4Solver;

import java.util.List;

/**
 * Open-Loop Landing Simulation that automatically stops printing
 * only when vertical velocity is effectively zero at the ground.
 */
public class OpenLoopController {

 // Titan gravity (km/s²)
    private static final double G    = LandingGravitationFunction.GRAVITATIONAL_ACCELERATION;
    // Max upward thrust acceleration (km/s²)
    private static final double UMAX = 0.01352;
    // vertical acceleration during brake burn
    private static final double A2   = UMAX - G;
    // tolerance for “landed” vy
    private static final double VY_EPS = 1e-5;


    /**
     * Computes the free-fall time t1 such that, once you brake at full thrust,
     * the lander reaches y = 0 with zero vertical speed.
     *
     * We use the bisection method: pick a low/high guess for t1, then
     * repeatedly halve the interval until the computed final altitude
     * (after free-fall and braking) is within tolerance of zero.
    */
    private static double findBurnStart(double y0, double vy0) {
        double lo = 0;
        // upper bound: time to fall from y0 under gravity alone
        double hi = Math.sqrt(2*y0/G) + 10;
        for (int i = 0; i < 50; i++) {
            double mid = 0.5*(lo + hi);

            // state after free-fall
            double vy1 = vy0 - G * mid;
            double y1  = y0 + vy0 * mid - 0.5 * G * mid * mid;

            // time to brake vy1 => 0 under A2
            double tBurn = -vy1 / A2;

            // altitude change during burn
            double y2 = y1 + vy1 * tBurn + 0.5 * A2 * tBurn * tBurn;

            if (y2 > 0) {
                lo = mid;  // still above ground => need longer free-fall
            } else {
                hi = mid;  // undershot => shorten free-fall
            }
        }
        return 0.5*(lo + hi);
    }

    /**  
     * OdeFunction that applies u(t) in three phases:
     *   0 ≤ t < t1      => u = 0
     *   t1 ≤ t < t2     => u = UMAX
     *   t ≥ t2          => u = G   (hover-compensate)
     */
    public static class ThreePhaseOde implements OdeFunction {
        private final double t1, t2;
        public ThreePhaseOde(double t1, double t2) {
            this.t1 = t1;
            this.t2 = t2;
        }
        @Override
        public double[] evaluate(double t, double[] s) {
            // s = [x,y,θ,vx,vy,ω]
            double u;
            if (t < t1)            u = 0.0;
            else if (t < t2)       u = UMAX;
            else                   u = G;   // net a=0

            double ay = u - G;
            return new double[]{
                s[3],   // dx/dt = vx
                s[4],   // dy/dt = vy
                0.0,    // dθ/dt = ω
                0.0,    // dvx/dt = 0
                ay,     // dvy/dt
                0.0     // dω/dt = 0
            };
        }
    }

    public static void main(String[] args) {
        //–– 1) initial altitude & speed
        double y0  = 300.0;   // km
        double vy0 = -0.1;    // km/s (negative downward)

        //–– 2) compute t1 via bisection so that piecewise model lands at y=0
        double t1 = findBurnStart(y0, vy0);

        //–– 3) compute t2 so that vy(t2)=0 under A2
        double vy1   = vy0 - G*t1;
        double tBurn = -vy1 / A2;
        double t2    = t1 + tBurn;

        System.out.printf(
          "Open-loop burn start at t1=%.3f s, end at t2=%.3f s%n",
          t1, t2
        );

        //–– 4) set up and run RK4
        OdeFunction ode = new ThreePhaseOde(t1, t2);
        double[] init = {0.0, y0, 0.0, 0.0, vy0, 0.0};
        double t0   = 0.0, tEnd = t2 + 200, h = 0.001; // h = step size

        RungeKutta4Solver solver =
          new RungeKutta4Solver(ode, init, t0, tEnd, h);
        List<ODESolver.TimeState> history = solver.solve();

        //–– 5) print until landed (|vy| small)
        for (ODESolver.TimeState ts : history) {
            double t  = ts.time;
            double y  = ts.state[1];
            double vy = ts.state[4];
            String phase = (t < t1 ? "FREEFALL"
                            : t < t2 ? "BRAKE"
                            :           "HOVER");

            System.out.printf(
              "t=%7.2f s => y= %8.3f km, vy= %8.6f km/s, phase= %s%n",
              t, y, vy, phase
            );

            if (Math.abs(vy) < VY_EPS) {
                System.out.printf(
                  ">>> Landed! at t=%.3f s, vy≈%.6f km/s%n",
                  t, vy
                );
                break;
            }
        }
    }
}
