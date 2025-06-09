package com.ken10.Phase2.StatesCalculations;

import com.ken10.Phase2.OptimizationAlgorithms.RK4Probe;
import com.ken10.Phase2.SolarSystemModel.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;


//To run this code(load all the states in a specified time frame) you create an instance of
//EphemerisLoader and then call EphemerisLoader.getPlanetStates()
// and get all states in the timeframe you specify the start date, end date and then call

/**
 * <p>EphemerisLoader uses the RK4Solver method to calculate future positions and velocities of
 * all Celestial bodies included in the simulation. It then stores a history of these values
 * for future access, useful for simulation of both missions required in the project.
 * </p>
 * The main parameters used in the constructors include:
 * List of bodies
 * start time/end time
 * step size
 */
public final class EphemerisLoader extends RK4Solver implements EphemerisProvider {

public final ArrayList<CelestialBodies> initialState;

private static final Probe probe1 = new Probe("probe", Earth.EARTH_INITIAL_POSITION.addX(Earth.RADIUS), new Vector(63.29024702239812, -33.49000052271595, -15.072908267640539), 1.0, 11.0);
private static final Probe probe2 = new Probe("probe", new Vector(1.415144880720923E9, 1.0259423273646048E8, -5.7603837655609496E7), new Vector(-55.397539484098616, -1.6245451071670916, -1.301803152493976), 1.0, 11.0);
private static final LocalDateTime startTime = LocalDateTime.of(2025, 4, 1, 0,0,0);


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

    public EphemerisLoader(boolean isMission) {
        super(SolarSystem.createPlanets(), startTime, startTime.plusYears(isMission ? 2 : 1), 2, false);
        this.initialState = planetarySystem;
        loadHistory(isMission ? 2 : 1);
    }

    /**
     * simulates and updates a probe’s flight history over time,
     * RK4 integration
     * @param duration represents stages of mission 1 being earth to titan, 2 the journey back.
     */
    private void loadHistory(int duration) {
        stepSize = stepSize/2;
        solve();

        stepSize = stepSize*2;
        RK4Probe simulation = new RK4Probe(probe1, history, stepSize, T_0, T_0.plusYears(1), BodyID.TITAN, BodyID.EARTH);
        simulation.solve();
        Hashtable<LocalDateTime, ArrayList<CelestialBodies>> hashtableToUse = new Hashtable<>();
        LocalDateTime endTimeTmp = endTime;
        for (LocalDateTime time = T_0; time.isBefore(simulation.getClosestDistTime()); time = time.plusMinutes(stepSize)) {
            ArrayList<CelestialBodies> currentState = history.get(time);
            currentState.add(simulation.historyProbe.get(time));
            hashtableToUse.put(time, currentState);
            endTimeTmp = time;
        }
        if(duration == 1) {
            history.clear();
            endTime = endTimeTmp;
            history = hashtableToUse;
        }
        else if(duration == 2) {
            var startTime2 = simulation.getClosestDistTime().minusMinutes(simulation.getStepSize());
            RK4Probe simulation2 = new RK4Probe(probe2, history, 2, startTime2, startTime.plusYears(2), BodyID.EARTH, BodyID.TITAN);
            simulation2.solve();
            for(var time = startTime2; time.isBefore(simulation2.getClosestDistTime()); time = time.plusMinutes(stepSize)) {
                var currentState = history.get(time);
                currentState.add(simulation2.historyProbe.get(time));
                hashtableToUse.put(time, currentState);
            }
            history.clear();
            history = hashtableToUse;
        }
        else System.out.println("Not a valid duration. Type in 1 or 2");
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public Vector position(BodyID body, LocalDateTime currentTime) {
        ArrayList<CelestialBodies> currentState = history.get(currentTime);
        CelestialBodies cb = currentState.get(body.index());
        return cb.getPosition();
    }
    @Override
    public Vector velocity(BodyID body, LocalDateTime currentTime) {
        ArrayList<CelestialBodies> currentState = history.get(currentTime);
        CelestialBodies cb = currentState.get(body.index());
        return cb.getVelocity();
    }
    public int getStepSize() {
        return stepSize;
    }
    public void putHistory(LocalDateTime time, ArrayList<CelestialBodies> state) {
        history.put(time, state);
        if(time.isAfter(endTime)) {endTime = time;}
        }
    }

