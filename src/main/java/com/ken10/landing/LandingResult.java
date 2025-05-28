package com.ken10.landing;

import java.util.ArrayList;
import java.util.List;

/**
 * Contains the results of a landing simulation
 */
public class LandingResult {
    public enum LandingStatus {
        SUCCESS,    // Successful landing within tolerances
        CRASH,      // Crashed (hit ground outside tolerances)
        TIMEOUT     // Simulation timed out
    }

    private LandingStatus landingStatus;
    private LandingState finalState;
    private double fuelUsed;
    private List<LandingState> trajectory;

    public LandingResult() {
        this.trajectory = new ArrayList<>();
    }

    /**
     * Calculate fitness score for optimization
     * Higher score = better result
     */
    public double getFitnessScore() {
        double score = 0;

        if (landingStatus == LandingStatus.SUCCESS) {
            // the base score for successful landing?? unsure!
            score = 1000;

            // Bonus for fuel efficiency => less fuel = higher score
            score += Math.max(0, 100 - fuelUsed);

            // Bonus for accuracy (closer to target = higher score)
            double distanceFromTarget = Math.abs(finalState.x);
            score += Math.max(0, 50 - distanceFromTarget * 1000); // convert km to m

        } else if (landingStatus == LandingStatus.CRASH) {
            // Partial score based on how close we got
            if (finalState.y >= -0.001) { // very close to ground
                score = 100;

                // Small bonus for being close to target horizontally
                double horizontalError = Math.abs(finalState.x);
                score += Math.max(0, 20 - horizontalError * 1000);

                // Small bonus for low velocity
                double velocityMagnitude = Math.sqrt(finalState.vx*finalState.vx + finalState.vy*finalState.vy);
                score += Math.max(0, 10 - velocityMagnitude * 1000);
            } else {
                score = 10; // crashed far from surface
            }
        } else {
            // Timeout - very low score
            score = 1;
        }

        return score;
    }

    public void addTrajectoryPoint(LandingState state) {
        trajectory.add(state);
    }

    // Getters and setters
    public LandingStatus getLandingStatus() {
        return landingStatus;
    }

    public void setLandingStatus(LandingStatus landingStatus) {
        this.landingStatus = landingStatus;
    }

    public LandingState getFinalState() {
        return finalState;
    }

    public void setFinalState(LandingState finalState) {
        this.finalState = finalState;
    }

    public double getFuelUsed() {
        return fuelUsed;
    }

    public void setFuelUsed(double fuelUsed) {
        this.fuelUsed = fuelUsed;
    }

    public List<LandingState> getTrajectory() {
        return trajectory;
    }

    @Override
    public String toString() {
        return String.format("LandingResult[status=%s, fuel=%.4f, fitness=%.1f, final=%s]",
                landingStatus, fuelUsed, getFitnessScore(), finalState);
    }
}