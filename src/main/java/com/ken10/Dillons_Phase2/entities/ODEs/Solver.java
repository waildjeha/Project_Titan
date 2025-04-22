package com.ken10.Dillons_Phase2.entities.ODEs;

import com.ken10.Dillons_Phase2.entities.Other.Vector;
import com.ken10.Dillons_Phase2.entities.planet.CelestialBodies;
import com.ken10.Dillons_Phase2.entities.planet.SolarSystem;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

/**
 * ODE solver for uses of both Euler and RK4.
 * Computes planetary motion and different timestamps.
 */
public abstract class Solver implements ODE_Function {
    protected ArrayList<CelestialBodies> planetarySystem;
    //    protected ArrayList<Body> planetaryDerivatives;
    protected LocalDateTime time;
    protected LocalDateTime endTime;
    protected int stepSizeHours;
    protected final Hashtable<LocalDateTime, ArrayList<CelestialBodies>> history;

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
     * @param stepSizeHours Time step size
     */
    public Solver(ArrayList<CelestialBodies> planetarySystem, LocalDateTime startTime, LocalDateTime endTime, int stepSizeHours) {
        this.time = startTime;
        this.planetarySystem = planetarySystem;
        //this.planetaryDerivatives = planetarySystem.planetaryDerivatives;
        this.endTime = endTime;
        this.stepSizeHours = stepSizeHours;
        this.history = new Hashtable<>();
        recordState();
    }
    public Solver(int stepSizeHours) {
        this.time = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        this.endTime = LocalDateTime.of(2026, 3, 31, 23, 59, 59);
        this.stepSizeHours = stepSizeHours;
        this.planetarySystem = SolarSystem.CreatePlanets();
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
        LocalDateTime t = time;
        ArrayList<CelestialBodies> bodies = new ArrayList<>(planetarySystem);
        history.put(t, bodies);
    }


    public void printState(LocalDateTime time) {
    history.get(time).forEach(CelestialBodies::printBody);
    }
}