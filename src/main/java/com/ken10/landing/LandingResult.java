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
            // Basis-Punkte für erfolgreiche Landung
            score = 1000;

            // Bonus für sparsamen Treibstoffverbrauch
            score += Math.max(0, 100 - fuelUsed);

            // Bonus für Nähe zur Zielposition
            double distanceFromTarget = Math.abs(finalState.position.x);
            score += Math.max(0, 50 - distanceFromTarget * 1000); // km → m

        } else if (landingStatus == LandingStatus.CRASH) {
            // Teilpunkte, wenn wir sehr nah dran waren
            if (finalState.position.y >= -0.001) {
                score = 100;

                double horizontalError = Math.abs(finalState.position.x);
                score += Math.max(0, 20 - horizontalError * 1000);

                double velocityMagnitude = Math.sqrt(
                        finalState.velocity.x * finalState.velocity.x +
                                finalState.velocity.y * finalState.velocity.y
                );

                score += Math.max(0, 10 - velocityMagnitude * 1000);
            } else {
                score = 10;
            }
        } else if (landingStatus == LandingStatus.TIMEOUT) {
            score = 5;

            double distanceFromTarget = Math.sqrt(
                    finalState.position.x * finalState.position.x +
                            finalState.position.y * finalState.position.y);
            score += Math.max(0, 300 - distanceFromTarget * 100);

            double velocityMagnitude = Math.sqrt(
                    finalState.velocity.x * finalState.velocity.x +
                            finalState.velocity.y * finalState.velocity.y);
            score += Math.max(0, 200 - velocityMagnitude * 100);
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