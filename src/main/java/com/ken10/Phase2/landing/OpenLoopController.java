package com.ken10.Phase2.landing;

import com.ken10.Phase2.landing.ControlOutput;

public class OpenLoopController extends FeedbackController {

    private final double g = 1.352;

    @Override
    public ControlOutput compute(StateVector state, double time) {
        final double g = 1.352;
        double thrust;

        if (state.getY() < -120) {
            thrust = 0.0;
        } else if (state.getY() < -100) {
            thrust = 1 * g;
        } else if (state.getY() < -80) {
            thrust = 2 * g;
        } else if (state.getY() < -40) {
            thrust = 3 * g;
        } else {
            thrust = 4 * g;
        }

        double theta = state.getTheta();
        double thrustX = thrust * Math.sin(theta);
        double thrustY = thrust * Math.cos(theta);
        double torque = 0.0;

        return new ControlOutput(thrustX, thrustY, torque);
    }

}
