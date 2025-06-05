package com.ken10.Phase2.landing;

public class FeedbackTest {

    public static void main(String[] args) {

        double initialX = 50.0;
        double initialY = 1000.0;
        double initialTheta = 0.0;
        double initialVx = 0.0;      // No horizontal velocity
        double initialVy = -33.0;    // Reasonable descent velocity
        double initialOmega = 0.0;
        StateVector state = new StateVector(initialX, initialVx, initialY, initialVy, initialTheta, initialOmega);

        double dt = 0.1;
        int maxSteps = 1000;

    }
}