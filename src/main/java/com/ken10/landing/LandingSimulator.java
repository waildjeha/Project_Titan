package com.ken10.landing;

/**
 * Simulates the landing module physics using Runge-Kutta 4 integration
 * Based on the differential equations from the manual:
 * ẍ = u*sin(θ)
 * ÿ = u*cos(θ) - g
 * θ̈ = v
 */
public class LandingSimulator {
    private static final double G_TITAN = 1.352e-3; // km/s² (gravity on Titan)

    /**
     * Simulate landing with given initial state and thrust schedule
     */
    public LandingResult simulate(LandingState initialState, ThrustSchedule schedule) {
        LandingState currentState = initialState.copy();
        LandingResult result = new LandingResult();

        int step = 0;
        double dt = schedule.getTimeStep();

        // Simulate until landing or crash or timeout
        while (currentState.y > 0 && step < schedule.getSteps() && currentState.time < 1000) {
            // Get thrust values for current step
            double u = schedule.getMainThrust(step);
            double v = schedule.getSideThrust(step);

            // Integrate
            currentState = rungeKutta4Step(currentState, u, v, dt);
            step++;

            // for storing the trajectory point
            result.addTrajectoryPoint(currentState.copy());
        }

        // Evaluating the final result
        result.setFinalState(currentState);
        result.setFuelUsed(schedule.getFuelConsumption());

        if (currentState.isSuccessfulLanding()) {
            result.setLandingStatus(LandingResult.LandingStatus.SUCCESS);
        } else if (currentState.hasCrashed()) {
            result.setLandingStatus(LandingResult.LandingStatus.CRASH);
        } else {
            result.setLandingStatus(LandingResult.LandingStatus.TIMEOUT);
        }

        return result;
    }

    /**
     * Single integration step
     */
    private LandingState rungeKutta4Step(LandingState state, double u, double v, double dt) {
        // State vector: [x, y, θ, vx, vy, vθ]
        double[] y0 = {state.x, state.y, state.theta, state.vx, state.vy, state.vtheta};

        // RK4 coefficients
        double[] k1 = computeDerivatives(y0, u, v);

        double[] y1 = new double[6];
        for (int i = 0; i < 6; i++) {
            y1[i] = y0[i] + 0.5 * dt * k1[i];
        }
        double[] k2 = computeDerivatives(y1, u, v);

        double[] y2 = new double[6];
        for (int i = 0; i < 6; i++) {
            y2[i] = y0[i] + 0.5 * dt * k2[i];
        }
        double[] k3 = computeDerivatives(y2, u, v);

        double[] y3 = new double[6];
        for (int i = 0; i < 6; i++) {
            y3[i] = y0[i] + dt * k3[i];
        }
        double[] k4 = computeDerivatives(y3, u, v);

        // Combine results
        double[] yNext = new double[6];
        for (int i = 0; i < 6; i++) {
            yNext[i] = y0[i] + (dt / 6.0) * (k1[i] + 2*k2[i] + 2*k3[i] + k4[i]);
        }

        return new LandingState(yNext[0], yNext[1], yNext[2],
                yNext[3], yNext[4], yNext[5],
                state.time + dt);
    }

    /**
     * Compute derivatives according to the differential equations
     */
    private double[] computeDerivatives(double[] state, double u, double v) {
        double x = state[0];
        double y = state[1];
        double theta = state[2];
        double vx = state[3];
        double vy = state[4];
        double vtheta = state[5];

        // Derivatives: [ẋ, ẏ, θ̇, v̇x, v̇y, v̇θ]
        double[] derivatives = new double[6];

        derivatives[0] = vx;                    // ẋ = vx
        derivatives[1] = vy;                    // ẏ = vy
        derivatives[2] = vtheta;                // θ̇ = vθ
        derivatives[3] = u * Math.sin(theta);   // ẍ = u*sin(θ)


        
        derivatives[4] = u * Math.cos(theta) - G_TITAN; // ÿ = u*cos(θ) - g
        derivatives[5] = v;                     // θ̈ = v

        return derivatives;
    }
}