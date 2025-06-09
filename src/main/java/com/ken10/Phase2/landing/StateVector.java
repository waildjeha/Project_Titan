package com.ken10.Phase2.landing;

/**
 * Represents the state of the landing module for the Titan landing simulation.
 * Includes position, angle, and velocity information.
 */
public class StateVector {

    // Position
    public double x;       // Horizontal position
    public double y;       // Vertical position

    // Rotation
    public double theta;   // Rotation angle in radians

    // Velocities
    public double vx;      // Horizontal velocity
    public double vy;      // Vertical velocity
    public double omega;   // Angular velocity

    public StateVector() {
        this(0, 0, 0, 0, 0, 0);
    }

    public StateVector(double x, double y, double theta, double vx, double vy, double omega) {
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.vx = vx;
        this.vy = vy;
        this.omega = omega;
    }

    /**
     * Converts this object to a flat array representation for use in ODE solvers.
     */
    public double[] toArray() {
        return new double[]{x, y, theta, vx, vy, omega};
    }

    /**
     * Creates a LandingModuleState from an array of 6 doubles.
     * @param state array with length 6: [x, y, θ, vx, vy, ω]
     */
    public static StateVector fromArray(double[] state) {
        if (state.length != 6) {
            throw new IllegalArgumentException("State array must have length 6.");
        }
        return new StateVector(state[0], state[1], state[2], state[3], state[4], state[5]);
    }

    @Override
    public String toString() {
        return String.format("x=%.3f, y=%.3f, θ=%.3f rad, vx=%.3f, vy=%.3f, ω=%.3f",
                x, y, theta, vx, vy, omega);
    }

    public double getX() {
        return x;
    }
    public double getY() {
        return y;
    }
    public double getTheta() {
        return theta;
    }
    public double getVx() {
        return vx;
    }
    public double getVy() {
        return vy;
    }
    public double getOmega() {
        return omega;
    }
    public void setX(double x) {
        this.x = x;
    }
    public void setY(double y) {
        this.y = y;
    }
    public void setTheta(double theta) {
        this.theta = theta;
    }
    public void setVx(double vx) {
        this.vx = vx;
    }
    public void setVy(double vy) {
        this.vy = vy;
    }
    public void setOmega(double omega) {
        this.omega = omega;
    }
    
    public StateVector copy() {
        return new StateVector(x, y, theta, vx, vy, omega);
    }

    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    public Vector2 getVelocity() {
        return new Vector2(vx, vy);
    }
}
