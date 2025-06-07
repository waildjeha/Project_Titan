package com.ken10.Phase2.Missions;

import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Titan;
import com.ken10.Phase2.SolarSystemModel.Vector;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class Orbit extends ToTitan{
    public final Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyOrbit;
    private final ArrayList<CelestialBodies> stateJustBeforeEnteringOrbit;
    public final List<LocalDateTime> timeStatesSortedOrbit;
    public Orbit() {
        stateJustBeforeEnteringOrbit = calculateInitialState();
        historyOrbit = populateHistoryOrbit();
        timeStatesSortedOrbit = sortTimeStates();
    }

    private ArrayList<CelestialBodies> calculateInitialState() {
        var t0 = timeStatesSorted.getLast();
        var initialState = historyToTitan.get(t0);
        return new ArrayList<>(initialState);
    }

    private Hashtable<LocalDateTime, ArrayList<CelestialBodies>> populateHistoryOrbit() {
        Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyOrbit = new Hashtable<>();
        var t0 = timeStatesSorted.getLast();
        LocalDateTime t1;
        if (isSeconds) t1 = t0.plusSeconds(stepSizeToTitan);
        else t1 = t0.plusMinutes(stepSizeToTitan);
        EphemerisLoader ephemerisLoader = new EphemerisLoader(stateJustBeforeEnteringOrbit, t0, t1, 1, true);
        ephemerisLoader.solve();
        double lowOrbit = Titan.RADIUS + 100;
        double distance;
        LocalDateTime time = t0;
        while(time.isBefore(t1)) {
            distance = ephemerisLoader.position(BodyID.SPACESHIP, time).getDistance(ephemerisLoader.position(BodyID.TITAN, time));
            if(distance<lowOrbit) {/*historyOrbit.put(time, ephemerisLoader.history.get(time));*/break;}
            historyOrbit.put(time, ephemerisLoader.history.get(time));
            System.out.println("Distance between spaceship and titan: " + distance + ", time: " + time);
            time = time.plusSeconds(1);
        }
        return historyOrbit;
    }

    private List<LocalDateTime> sortTimeStates() {
        return historyOrbit.keySet().stream().sorted().toList();
    }

    public static void main(String[] args) {
        VerifyOrbit verifyOrbit = new VerifyOrbit();



    }
}
