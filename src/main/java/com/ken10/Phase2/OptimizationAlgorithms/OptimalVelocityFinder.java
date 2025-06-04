package com.ken10.Phase2.OptimizationAlgorithms;

import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.Earth;
import com.ken10.Phase2.SolarSystemModel.Vector;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;

public class OptimalVelocityFinder{
LaunchData launchData;
RK4Probe foundData;


    public OptimalVelocityFinder(LaunchData launchData) {
        this.launchData = launchData;
    }

    public void solve(){
        foundData = findLaunchVelocity();
    }

    public RK4Probe getFoundData(){
        return foundData;
    }

    private RK4Probe findLaunchVelocity() {
        EvolutionAlgorithm initialGuess = new EvolutionAlgorithm(launchData);
        initialGuess.solve();
        RK4Probe currentGuess = initialGuess.getEvolutionAlgorithmInitialGuess();
        HillClimbing finalGuess = new HillClimbing(currentGuess, launchData);
        finalGuess.solve();
        return finalGuess.getBestSimulation();
    }

    public static void main(String[] args) {
        LocalDateTime launchTime = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        LaunchData launchData = new LaunchData(BodyID.TITAN, Earth.EARTH_INITIAL_POSITION.addX(6370), launchTime);
        OptimalVelocityFinder optimalVelocityFinder = new OptimalVelocityFinder(launchData);
        optimalVelocityFinder.solve();
        System.out.println(optimalVelocityFinder.getFoundData());
    }
}

//----------------------------------------------------------
//Initial probe position and velocity: (-1.4664541859104577E8, -2.8949304626334388E7, 2241.9186033698497) (62.25493222046365, -33.2301274780465, -15.851610868521927)
//Velocity magnitude relative to earth: 59.252937842857804
//Closest Distance to Titan: 2467.6639546830183
//Date of closest approach: 2026-03-26T09:20