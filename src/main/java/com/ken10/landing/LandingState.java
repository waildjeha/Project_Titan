package com.ken10.landing;

/**
 * Represents the state of the landing module at any given time
 */
public class LandingState {
    public Vector2D position;  // position.x = x, position.y = y
    public Vector2D velocity;  // velocity.x = vx, velocity.y = vy
    public double theta;    // rotation angle (rad)
    public double vtheta;   // angular velocity (rad/s)
    public double time;     // current time (s)

    public LandingState(Vector2D position, double theta, Vector2D velocity, double vtheta, double time) {
        this.position = position;
        this.theta = theta;
        this.velocity = velocity;
        this.vtheta = vtheta;
        this.time = time;

}

    public LandingState copy() {
        return new LandingState(
                new Vector2D(position.x, position.y),
                theta,
                new Vector2D(velocity.x, velocity.y),
                vtheta,
                time);
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

        return Math.abs(position.y) < 1e-6 &&  // landed (y = 0)
                Math.abs(position.x) <= DELTA_X &&
                Math.abs(theta % (2 * Math.PI)) <= DELTA_THETA &&
                Math.abs(velocity.x) <= EPSILON_X &&
                Math.abs(velocity.y) <= EPSILON_Y &&
                Math.abs(vtheta) <= EPSILON_THETA;
    }

    /**
     * Check if module has crashed (hit ground with bad conditions)
     */
    public boolean hasCrashed() {
        return position.y <= 0 && !isSuccessfulLanding();

    }

    @Override
    public String toString() {
        return String.format("State[x=%.6f, y=%.6f, θ=%.3f, vx=%.6f, vy=%.6f, ω=%.3f, t=%.1f]",
                position.x, position.y, theta, velocity.x, velocity.y, vtheta, time);


    }
}