package com.ken10.Phase2.landing;

public class ControlOutput {
    private final double thrustX;
    private final double thrustY;
    private final double torque;

    public ControlOutput(double thrustX, double thrustY, double torque) {
        this.thrustX = thrustX;
        this.thrustY = thrustY;
        this.torque = torque;
    }

    public double getThrustX() {
        return thrustX;
    }

    public double getThrustY() {
        return thrustY;
    }

    public double getTorque() {
        return torque;
    }

    @Override
    public String toString() {
        return String.format("ControlOutput[thrustX=%.4f, thrustY=%.4f, torque=%.4f]",
                thrustX, thrustY, torque);
    }
}