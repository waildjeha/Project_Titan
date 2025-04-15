package com.ken10.ODEs;

import com.ken10.entities.CelestialBodies;

import java.util.ArrayList;
import java.util.List;

/**
 * ODE solver for uses of both Euler and RK4.
 * Computes planetary motion and different timestamps.
 */
public abstract class Solver implements ODE_Function {
    protected ArrayList<CelestialBodies> planetarySystem;
    //    protected ArrayList<Body> planetaryDerivatives;
    protected double time;
    protected double endTime;
    protected double stepSize;
    protected List<Solver.TimeState> history;

    /**
     * Initialize the ODE solver
     *
     * //@param derivativeFunction Function that calculates derivatives: dx/dt =
     *                           f(x,t)
     * //@param initialState       Initial state vector (up to 10 dimensions): dx/dt,
     *                           dy/dt, dz/dt .......
     * @param startTime          Initial time
     * @param endTime            End time for simulation
     * @param stepSize           Time step size
     */
    public Solver(ArrayList<CelestialBodies> planetarySystem, double startTime, double endTime, double stepSize) {
        this.time = startTime;
        this.planetarySystem = planetarySystem;
        //this.planetaryDerivatives = planetarySystem.planetaryDerivatives;
        this.endTime = endTime;
        this.stepSize = stepSize;
        this.history = new ArrayList<>();
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
        while (time < endTime) {
            step();
            recordState();
        }
    }

    /**
     * Record current state to history
     */
    protected void recordState() {
        history.add(new Solver.TimeState(time, new ArrayList<>(planetarySystem)));
    }

    public void printState(){
        for (CelestialBodies b : planetarySystem) {
            System.out.println(b.toString());
        }
    }

    /**
     * Inner class to store time and state together
     */
    public static class TimeState {
        public final double TIME;
        public final ArrayList<CelestialBodies> STATE;

        public TimeState(double time, ArrayList<CelestialBodies> state) {
            this.TIME = time;
            this.STATE = state;
        }
    }
}