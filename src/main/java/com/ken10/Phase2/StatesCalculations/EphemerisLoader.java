package com.ken10.Phase2.StatesCalculations;


import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.SolarSystem;
import com.ken10.Phase2.SolarSystemModel.Vector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//To run this code(load all the states in a specified time frame) you create an instance of
//EphemerisLoader and then call EphemerisLoader.getPlanetStates()
// and get all states in the timeframe you specify the start date, end date and then call
public final class EphemerisLoader extends RK4Solver implements EphemerisProvider {
    //TODO i can edit to also specify Solver choice in the constructor
//public LocalDateTime startTime;// = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
//public LocalDateTime endTime;// = LocalDateTime.of(2026, 3, 31, 23, 59, 59);
//public int StepSizeHours;
public final ArrayList<CelestialBodies> initialState;

    //I fell like we need to create an interface where we declare startTime, endTime, stepSize
    //to make sure our solvers work on the same settings
    public EphemerisLoader(ArrayList<CelestialBodies> planetarySystem, LocalDateTime startTime, LocalDateTime endTime, int stepSizeMins) {
        super(deepCopyList(planetarySystem), startTime, endTime, stepSizeMins);
        this.initialState = planetarySystem;

    }
    public EphemerisLoader(int stepSizeMins) {
        super(SolarSystem.CreatePlanets(),
                LocalDateTime.of(2025, 4, 1, 0, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59),
                stepSizeMins);
        this.initialState = SolarSystem.CreatePlanets();
    }

    private static ArrayList<CelestialBodies> deepCopyList(
            List<CelestialBodies> src) {
        ArrayList<CelestialBodies> copy = new ArrayList<>(src.size());
        for (CelestialBodies b : src) copy.add(b.deepCopy());
        return copy;
    }



    @Override
    public Vector position(BodyID body, LocalDateTime currentTime) {
        ArrayList<CelestialBodies> currentState = history.get(currentTime);
        CelestialBodies cb = currentState.get(body.index());
        return cb.getPosition();
        //We need to find the position of the planets in the current state
        //Where do we store them? class to store the current solar system state
    }

    @Override
    public Vector velocity(BodyID body, LocalDateTime currentTime) {
        ArrayList<CelestialBodies> currentState = history.get(currentTime);
        CelestialBodies cb = currentState.get(body.index());
        return cb.getVelocity();
        //We need to find the velocity of the planets in the current state
    }



    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 0, 0);
        EphemerisLoader ephemeris = new EphemerisLoader(SolarSystem.CreatePlanets(), startTime, startTime.plusMonths(12), 1);
        ephemeris.solve();
        ArrayList<CelestialBodies> startState = ephemeris.history.get(startTime);
        if(startState == null) System.out.println("No history found");
        for (CelestialBodies c : startState) {
            c.printBody();
        }
    }
}
