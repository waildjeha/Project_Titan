package com.ken10.Phase2.StatesCalculations;


import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.SolarSystem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

/**
 * ODE solver for uses of both Euler and RK4.
 * Computes planetary motion and different timestamps.
 */
public abstract class Solver implements ODE_Function {
    protected ArrayList<CelestialBodies> planetarySystem;
    protected LocalDateTime time;
    protected LocalDateTime endTime;
    protected int stepSizeMins;
    protected static final LocalDateTime START_TIME = LocalDateTime.of(2025, 4, 1, 0,0);
    public Hashtable<LocalDateTime, ArrayList<CelestialBodies>> history;

    /**
     * Initialize the ODE solver
     * <p>
     * //@param derivativeFunction Function that calculates derivatives: dx/dt =
     * f(x,t)
     * //@param initialState       Initial state vector (up to 10 dimensions): dx/dt,
     * dy/dt, dz/dt .......
     *
     * @param startTime     Initial time
     * @param endTime       End time for simulation
     * @param stepSizeMins Time step size
     */
    public Solver(ArrayList<CelestialBodies> planetarySystem, LocalDateTime startTime, LocalDateTime endTime, int stepSizeMins) {
        this.time = startTime;
        this.planetarySystem = planetarySystem;
        this.endTime = endTime;
        this.stepSizeMins = stepSizeMins;
        this.history = new Hashtable<>();
        recordState();
    }
    public Solver(int stepSizeMins) {
        this.time = START_TIME;
        this.endTime = LocalDateTime.of(2026, 3, 31, 23, 59, 59);
        this.stepSizeMins = stepSizeMins;
        this.planetarySystem = SolarSystem.createPlanets();
        this.history = new Hashtable<>();
        recordState();
    }

    /**
     * Advance the solution by one step
     * This is declared as abstract because of course Euler and RK4 have different
     * ways of calculating the next step
     */
    public abstract void step();

    /**
     * Run the simulation until endTime
     */
    public void solve() {
        while (time.isBefore(endTime)) {
            step();
            recordState();
        }
    }

    /**
     * Record current state to history
     */
    protected void recordState() {
        ArrayList<CelestialBodies> snapshot = new ArrayList<>();
        for (CelestialBodies body : planetarySystem) {
            snapshot.add(body.deepCopy()); // Deep copy constructor
        }
        history.put(time, snapshot);
    }

    public void printState(ArrayList<CelestialBodies> planetarySystem, LocalDateTime time) {
        System.out.println("Time : " + time.toString());
        for (CelestialBodies c : planetarySystem) {
            c.printBody();
        }

    }
}