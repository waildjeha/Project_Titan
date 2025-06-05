package com.ken10.landing;

public class LandingSimulator {
    private static final double G_TITAN = 1.352e-3; // km/s²

    public LandingResult simulate(LandingState initialState, ThrustSchedule schedule) {
        LandingState currentState = initialState.copy();
        LandingResult result = new LandingResult();

        int step = 0;
        double dt = schedule.getTimeStep();

        int maxSteps = schedule.getSteps();
        double maxTime = maxSteps * dt;
        double timeLimit = maxTime * 2.0; // z. B. 200 % der geplanten Zeit

        while (currentState.position.y > 0 &&
                step < maxSteps &&
                currentState.time < timeLimit) {

            double u = schedule.getMainThrust(step);
            double v = schedule.getSideThrust(step);

            currentState = rungeKutta4Step(currentState, u, v, dt);
            step++;

            result.addTrajectoryPoint(currentState.copy());
        }

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

    private LandingState rungeKutta4Step(LandingState state, double u, double v, double dt) {
        // y0: [x, y, θ, vx, vy, vθ]
        double[] y0 = {
                state.position.x,
                state.position.y,
                state.theta,
                state.velocity.x,
                state.velocity.y,
                state.vtheta
        };

        double[] k1 = computeDerivatives(y0, u, v);

        double[] y1 = new double[6];
        for (int i = 0; i < 6; i++) y1[i] = y0[i] + 0.5 * dt * k1[i];
        double[] k2 = computeDerivatives(y1, u, v);

        double[] y2 = new double[6];
        for (int i = 0; i < 6; i++) y2[i] = y0[i] + 0.5 * dt * k2[i];
        double[] k3 = computeDerivatives(y2, u, v);

        double[] y3 = new double[6];
        for (int i = 0; i < 6; i++) y3[i] = y0[i] + dt * k3[i];
        double[] k4 = computeDerivatives(y3, u, v);

        double[] yNext = new double[6];
        for (int i = 0; i < 6; i++) {
            yNext[i] = y0[i] + (dt / 6.0) * (k1[i] + 2 * k2[i] + 2 * k3[i] + k4[i]);
        }

        Vector2D newPosition = new Vector2D(yNext[0], yNext[1]);
        Vector2D newVelocity = new Vector2D(yNext[3], yNext[4]);

        return new LandingState(
                newPosition,
                yNext[2], // θ
                newVelocity,
                yNext[5], // vθ
                state.time + dt
        );
    }

    private double[] computeDerivatives(double[] state, double u, double v) {
        double x = state[0];
        double y = state[1];
        double theta = state[2];
        double vx = state[3];
        double vy = state[4];
        double vtheta = state[5];

        double[] derivatives = new double[6];
        derivatives[0] = vx;
        derivatives[1] = vy;
        derivatives[2] = vtheta;
        derivatives[3] = u * Math.sin(theta);
        derivatives[4] = u * Math.cos(theta) - G_TITAN;
        derivatives[5] = v;

        return derivatives;
    }
}
