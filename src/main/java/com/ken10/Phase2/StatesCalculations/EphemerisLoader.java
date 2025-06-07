package com.ken10.Phase2.StatesCalculations;


import com.ken10.Phase2.OptimizationAlgorithms.RK4Probe;
import com.ken10.Phase2.SolarSystemModel.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;


//To run this code(load all the states in a specified time frame) you create an instance of
//EphemerisLoader and then call EphemerisLoader.getPlanetStates()
// and get all states in the timeframe you specify the start date, end date and then call
public final class EphemerisLoader extends RK4Solver implements EphemerisProvider {

public final ArrayList<CelestialBodies> initialState;
private RK4Probe simulation;

    public EphemerisLoader(ArrayList<CelestialBodies> planetarySystem, LocalDateTime startTime, LocalDateTime endTime, int stepSize, boolean isSeconds) {
        super(planetarySystem, startTime, endTime, stepSize, isSeconds);
        this.initialState = planetarySystem;
    }
    public EphemerisLoader(int stepSizeMins) {
        super(SolarSystem.createPlanets(),
                LocalDateTime.of(2025, 4, 1, 0, 0),
                LocalDateTime.of(2026, 4, 1, 0, 0),
                stepSizeMins, false);
        this.initialState = planetarySystem;
    }
    public EphemerisLoader(int stepSizeMins, int durationYears) {
        super(SolarSystem.createPlanets(),
                LocalDateTime.of(2025, 4, 1, 0, 0),
                LocalDateTime.of(2025+durationYears, 4, 1, 0, 0),
                stepSizeMins, false);
        this.initialState = planetarySystem;
    }

    public EphemerisLoader(int stepSizeMins, Probe probe, int duration) {
        super(SolarSystem.createPlanets(), START_TIME, START_TIME.plusYears(duration), stepSizeMins, false);
        this.initialState = planetarySystem;
        loadHistory(probe, duration);
    }

    private void loadHistory(Probe probe, int duration) {
        stepSize = stepSize /2;
        solve();
        stepSize = stepSize *2;
        RK4Probe simulation = new RK4Probe(probe, history, stepSize);
        simulation.solve();
        this.simulation = simulation;
        endTime = simulation.getClosestDistTime();
        System.out.println("EphemerisLoader: simulation = " + simulation);
        for (LocalDateTime time = START_TIME; time.isBefore(endTime) || time.isEqual(endTime); time = time.plusMinutes(stepSize))
        {
            ArrayList<CelestialBodies> currentState = history.get(time);
            currentState.add(simulation.historyProbe.get(time));
            history.replace(time, currentState);
            if(time.plusMinutes(stepSize /2).isBefore(endTime))
                history.remove(time.plusMinutes(stepSize /2));
        }

        for(var time = endTime.plusMinutes(stepSize /2); time.isBefore(START_TIME.plusYears(duration))||time.isEqual(START_TIME.plusYears(duration)); time = time.plusMinutes(stepSize /2)){
            history.remove(time);
        }
    }

    public LocalDateTime getEndTime() {
        return endTime;
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

    public int getStepSize() {
        return stepSize;
    }
    public boolean isSeconds() {
        return isSeconds;
    }

    public static void main(String[] args) {
        LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 0, 0);
        Vector earthPosition = new Vector(-1.4664541859104577E8, -2.8949304626334388E7, 2241.9186033698497);
        Vector initialVelocity = new Vector(61.065619043674, -33.10614766691787, -15.82637073951113);
        Probe probe = new Probe("dominik", earthPosition, initialVelocity);
        EphemerisLoader eph = new EphemerisLoader(2, probe, 1);
        eph.solve();
        var history = eph.history;
        List<LocalDateTime> keys = history.keySet().stream().sorted().toList();

            ArrayList<CelestialBodies> currentState = history.get(keys.getFirst());
            CelestialBodies p = currentState.get(BodyID.SPACESHIP.index());
            CelestialBodies t = currentState.get(BodyID.TITAN.index());
            System.out.println("Dist at date: " + keys.getFirst() + " is equal to " + p.getPosition().getDistance(t.getPosition()));

        ArrayList<CelestialBodies> currentState1 = history.get(keys.getLast());
        CelestialBodies p1 = currentState1.get(BodyID.SPACESHIP.index());
        CelestialBodies t1 = currentState1.get(BodyID.TITAN.index());
        System.out.println("Dist at date: " + keys.getLast() + " is equal to " + p1.getPosition().getDistance(t1.getPosition()));





    }
    }

