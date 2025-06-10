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

    /**
     * Constructs a StateVector with specified position, angle, and velocities.
     *
     * @param x      Horizontal position
     * @param y      Vertical position
     * @param theta  Rotation angle in radians
     * @param vx     Horizontal velocity
     * @param vy     Vertical velocity
     * @param omega  Angular velocity
     */
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

    /**
     * Returns a string representation of the state vector.
     * The format is: "x=..., y=..., θ=... rad, vx=..., vy=..., ω=..."
     */
    @Override
    public String toString() {
        return String.format("x=%.3f, y=%.3f, θ=%.3f rad, vx=%.3f, vy=%.3f, ω=%.3f",
                x, y, theta, vx, vy, omega);
    }

    /**
     * Returns the horizontal position.
     */
    public double getX() {
        return x;
    }

    /**
     * Returns the vertical position.
     */
    public double getY() {
        return y;
    }

    /**
     * Returns the rotation angle in radians.
     */
    public double getTheta() {
        return theta;
    }

    /**
     * Returns the horizontal velocity.
     */
    public double getVx() {
        return vx;
    }

    /**
     * Returns the vertical velocity.
     */
    public double getVy() {
        return vy;
    }

    /**
     * Returns the angular velocity.
     */
    public double getOmega() {
        return omega;
    }

    /**
     * Sets the horizontal position.
     */
    public void setX(double x) {
        this.x = x;
    }

    /**
     * Sets the vertical position.
     */
    public void setY(double y) {
        this.y = y;
    }

    /**
     * Sets the rotation angle in radians.
     */
    public void setTheta(double theta) {
        this.theta = theta;
    }

    /**
     * Sets the horizontal velocity.
     */
    public void setVx(double vx) {
        this.vx = vx;
    }

    /**
     * Sets the vertical velocity.
     */
    public void setVy(double vy) {
        this.vy = vy;
    }

    /**
     * Sets the angular velocity.
     */
    public void setOmega(double omega) {
        this.omega = omega;
    }

    /**
     * Creates a copy of this StateVector.
     *
     * @return A new StateVector with the same values.
     */
    public StateVector copy() {
        return new StateVector(x, y, theta, vx, vy, omega);
    }

    /**
     * Returns the position as a Vector2 object.
     *
     * @return A Vector2 representing the position (x, y).
     */
    public Vector2 getPosition() {
        return new Vector2(x, y);
    }

    /**
     * Returns the velocity as a Vector2 object.
     *
     * @return A Vector2 representing the velocity (vx, vy).
     */
    public Vector2 getVelocity() {
        return new Vector2(vx, vy);
    }
}