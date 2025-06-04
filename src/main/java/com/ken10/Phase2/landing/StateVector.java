package com.ken10.Phase2.landing;

public class StateVector {
    private final double x;
    private final double vx;
    private final double y;
    private final double vy;
    private final double theta;
    private final double omega;

    public StateVector(double x, double vx, double y, double vy, double theta, double omega) {
        this.x = x;
        this.vx = vx;
        this.y = y;
        this.vy = vy;
        this.theta = theta;
        this.omega = omega;
    }

    public double getX() { return x; }
    public double getVx() { return vx; }
    public double getY() { return y; }
    public double getVy() { return vy; }
    public double getTheta() { return theta; }
    public double getOmega() { return omega; }

    public boolean hasLandedSafely(StateVector goal) {
        return Math.abs(this.x - goal.x) <= getLandingToleranceX()
                && Math.abs(this.y - goal.y) <= getLandingToleranceY()
                && Math.abs(this.vx - goal.vx) <= getLandingToleranceVelocityX()
                && Math.abs(this.vy - goal.vy) <= getLandingToleranceVelocityY()
                && Math.abs(this.theta - goal.theta) <= getLandingToleranceTheta()
                && Math.abs(this.omega - goal.omega) <= getLandingToleranceAngularVelocity();
    }

    // Tolerance values
    public static double getLandingToleranceX() {
        return 1e-4; // km
    }

    public static double getLandingToleranceY() {
        return 1e-4; // km
    }

    public static double getLandingToleranceVelocityX() {
        return 1e-4; // km/s
    }

    public static double getLandingToleranceVelocityY() {
        return 1e-4; // km/s
    }

    public static double getLandingToleranceTheta() {
        return 0.02; // radians
    }

    public static double getLandingToleranceAngularVelocity() {
        return 0.01; // rad/s
    }

    @Override
    public String toString() {
        return String.format("StateVector[x=%.5f, vx=%.5f, y=%.5f, vy=%.5f, θ=%.5f, ω=%.5f]",
                x, vx, y, vy, theta, omega);
    }
}