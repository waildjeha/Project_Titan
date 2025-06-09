package com.ken10.Phase2.landing;

import java.lang.Thread.State;
import java.util.List;
import java.util.function.Function;

import com.ken10.Phase1.ODESolver.TimeState;
import com.ken10.Phase1.OdeFunction;
import com.ken10.Phase1.RungeKutta4Solver;
import com.ken10.Phase2.SolarSystemModel.Vector;

public class LandingSolver {
/**
     * Wraps your landing dynamics into the OdeFunction interface.
     * At each time-step, it:
     *  - Reconstructs the StateVector
     *  - Lets the controller pick the thrust
     *  - Computes translational + rotational accelerations
     *  - Returns the 6 derivatives for RK4
     */
    public static class LandingOdeFunction implements OdeFunction {
        private final LandingModule module;
        private final LandingController controller;
        private final LandingGravitationFunction dynamics;
        private final double stepSize;

        public LandingOdeFunction(LandingModule module,
                                  LandingController controller,
                                  double stepSize) {
            this.module     = module;
            this.controller = controller;
            this.dynamics   = new LandingGravitationFunction();
            this.stepSize   = stepSize;
        }

        @Override
        public double[] evaluate(double time, double[] stateArray) {
            // 1) Reconstruct full state
            StateVector state = StateVector.fromArray(stateArray);
            module.state = state;

            // 2) Let controller compute thrust based on current state
            int currentStep = (int) Math.round(time / stepSize);
            controller.execute(module, currentStep, stepSize);

            // 3) Compute accelerations (ax, ay, alphä) including gravity + thrust
            Vector2 thrust = module.getThrust();
            Vector accelRot = dynamics.f(state, thrust, stepSize, time);

            // 4) Pack derivatives: [vx, vy, ω, ax, ay, α̈]
            return new double[] {
                state.getVx(),                      // dx/dt
                state.getVy(),                      // dy/dt
                state.getOmega(),                   // dθ/dt
                accelRot.getX(),                    // dvx/dt
                accelRot.getY(),                    // dvy/dt
                accelRot.getZ()                     // dω/dt
            };
        }
    }

    public static void main(String[] args) {
        // -- 1) Define your initial conditions --
        double x0     =  0.0;    // start directly above pad
        double y0     = 160.0;   // 160 km altitude
        double theta0 =  0.0;    // upright
        double vx0    =  0.0;    // no horizontal speed
        double vy0    = -0.1;    // small downward speed
        double omega0 =  0.0;    // no rotation

        StateVector initial = new StateVector(x0, y0, theta0, vx0, vy0, omega0);
        double[] initArr = initial.toArray();

        // -- 2) Build your module & controller --
        LandingModule module     = new LandingModule("TitanLander",
                                                     new Vector2(x0, y0),
                                                     new Vector2(vx0, vy0));
        module.state = initial;  // seed the module
        LandingController controller = new LandingController();

        // -- 3) Create the RK4 solver --
        double t0        = 0.0;
        double tEnd      = 500.0;   // total sim time
        double stepSize  = 0.01;     // time step
        OdeFunction ode = new LandingOdeFunction(module, controller, stepSize);

        RungeKutta4Solver solver = new RungeKutta4Solver(
            ode, initArr, t0, tEnd, stepSize
        ); // :contentReference[oaicite:0]{index=0}

        // -- 4) Run and print results --
        List<TimeState> history = solver.solve();                // :contentReference[oaicite:1]{index=1}
        for (TimeState ts : history) {
            StateVector sv = StateVector.fromArray(ts.state);
            System.out.printf("t=%.2f → %s%n", ts.time, sv);
        }
    }


    
}
