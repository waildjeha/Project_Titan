package com.ken10.Phase1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public abstract class ODESolver {
    protected OdeFunction derivativeFunction;
    protected double[] state;
    protected double time;
    protected double endTime;
    protected double stepSize;
    protected List<TimeState> history;

    /**
     * Initialize the ODE solver
     *
     * @param derivativeFunction Function that calculates derivatives: dx/dt =
     *                           f(x,t)
     * @param initialState       Initial state vector (up to 10 dimensions): dx/dt,
     *                           dy/dt, dz/dt .......
     * @param startTime          Initial time
     * @param endTime            End time for simulation
     * @param stepSize           Time step size
     */
    public ODESolver(OdeFunction derivativeFunction, double[] initialState,
            double startTime, double endTime, double stepSize) {
        this.derivativeFunction = derivativeFunction;
        this.state = Arrays.copyOf(initialState, initialState.length);
        this.time = startTime;
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
     * 
     * @return History of states over time
     */
    public List<TimeState> solve() {
        while (time < endTime) {
            step();
            recordState();
        }
        return history;
    }

    /**
     * Record current state to history
     */
    protected void recordState() {
        history.add(new TimeState(time, Arrays.copyOf(state, state.length)));
    }

    /**
     * Inner class to store time and state together
     */
    public static class TimeState {
        public final double time;
        public final double[] state;

        public TimeState(double time, double[] state) {
            this.time = time;
            this.state = state;
        }
    }
}