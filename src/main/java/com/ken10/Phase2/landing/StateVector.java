package com.ken10.Phase2.landing;

public class StateVector {
    private double x;
    private double vx;
    private double y;
    private double vy;
    private  double theta;
    private double omega;

    public StateVector(double x, double vx, double y, double vy, double theta, double omega) {
        this.x = x;
        this.vx = vx;
        this.y = y;
        this.vy = vy;
        this.theta = theta;
        this.omega = omega;
    }

    public void setFromArray(double[] arr) {
        if (arr == null || arr.length != 6) {
            throw new IllegalArgumentException("Input array must have exactly 6 elements.");
        }

        this.x = arr[0];
        this.vx = arr[1];
        this.y = arr[2];
        this.vy = arr[3];
        this.theta = arr[4];
        this.omega = arr[5];
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
        return 1e-4;
    }

    public static double getLandingToleranceY() {
        return 0.0;
    }

    public static double getLandingToleranceVelocityX() {
        return 1e-4;
    }

    public static double getLandingToleranceVelocityY() {
        return 1e-4;
    }

    public static double getLandingToleranceTheta() {
        return 0.02;
    }

    public static double getLandingToleranceAngularVelocity() {
        return 0.01;
    }

    @Override
    public String toString() {
        return String.format("StateVector[x=%.5f, vx=%.5f, y=%.5f, vy=%.5f, θ=%.5f, ω=%.5f]",
                x, vx, y, vy, theta, omega);
    }

    public double[] toArray() {
        return new double[] {
                x,
                vx,
                y,
                vy,
                theta,
                omega
        };
    }
}