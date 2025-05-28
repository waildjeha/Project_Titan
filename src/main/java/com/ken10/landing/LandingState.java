package com.ken10.landing;

/**
 * Represents the state of the landing module at any given time
 */
public class LandingState {
    public double x;        // horizontal position (km)
    public double y;        // vertical position (km)
    public double theta;    // rotation angle (rad)
    public double vx;       // horizontal velocity (km/s)
    public double vy;       // vertical velocity (km/s)
    public double vtheta;   // angular velocity (rad/s)
    public double time;     // current time (s)

    public LandingState(double x, double y, double theta, double vx, double vy, double vtheta, double time) {
        this.x = x;
        this.y = y;
        this.theta = theta;
        this.vx = vx;
        this.vy = vy;
        this.vtheta = vtheta;
        this.time = time;
    }

    public LandingState copy() {
        return new LandingState(x, y, theta, vx, vy, vtheta, time);
    }

    /**
     * Check if landing is successful based on tolerance values from manual
     */
    public boolean isSuccessfulLanding() {
        double DELTA_X = 1e-4;      // 0.1 m in km
        double DELTA_THETA = 0.02;   // rad
        double EPSILON_X = 1e-4;     // 0.1 m/s in km/s
        double EPSILON_Y = 1e-4;     // 0.1 m/s in km/s
        double EPSILON_THETA = 0.01; // rad/s

        return Math.abs(y) < 1e-6 &&  // landed (y = 0)
                Math.abs(x) <= DELTA_X &&
                Math.abs(theta % (2 * Math.PI)) <= DELTA_THETA &&
                Math.abs(vx) <= EPSILON_X &&
                Math.abs(vy) <= EPSILON_Y &&
                Math.abs(vtheta) <= EPSILON_THETA;
    }

    /**
     * Check if module has crashed (hit ground with bad conditions)
     */
    public boolean hasCrashed() {
        return y <= 0 && !isSuccessfulLanding();
    }

    @Override
    public String toString() {
        return String.format("State[x=%.6f, y=%.6f, θ=%.3f, vx=%.6f, vy=%.6f, ω=%.3f, t=%.1f]",
                x, y, theta, vx, vy, vtheta, time);
    }
}