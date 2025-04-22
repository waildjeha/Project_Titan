package com.ken10.Dillons_Phase2.entities.astro.Emhemeris;

import com.ken10.Dillons_Phase2.entities.NumericalSolvers.RK4Solver;
import com.ken10.Dillons_Phase2.entities.Other.Vector;
import com.ken10.Dillons_Phase2.entities.planet.BodyID;
import com.ken10.Dillons_Phase2.entities.planet.CelestialBodies;
import com.ken10.Dillons_Phase2.entities.planet.SolarSystem;
import java.time.LocalDateTime;
import java.util.ArrayList;


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
    public EphemerisLoader(ArrayList<CelestialBodies> planetarySystem, LocalDateTime startTime, LocalDateTime endTime, int stepSizeHours) {
        super(planetarySystem, startTime, endTime, stepSizeHours );
        this.initialState = planetarySystem;

    }
    public EphemerisLoader(int stepSizeHours) {
        super(SolarSystem.CreatePlanets(),
                LocalDateTime.of(2025, 4, 1, 0, 0),
                LocalDateTime.of(2026, 3, 31, 23, 59),
                stepSizeHours);
        this.initialState = this.planetarySystem;
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

    //TODO we can choose our solver here


    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 0, 0);
        var ephemeris = new EphemerisLoader(SolarSystem.CreatePlanets(), startTime, startTime.plusMonths(1), 1);
        ephemeris.solve();
        ArrayList<CelestialBodies> startState = ephemeris.history.get(startTime);
        if(startState == null) System.out.println("No history found");
        for (CelestialBodies c : startState) {
            c.printBody();
        }
    }
}
