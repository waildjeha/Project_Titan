package com.ken10.Phase2.Missions;

import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Probe;
import com.ken10.Phase2.SolarSystemModel.Vector;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

public class ToTitan {
    private final Probe probe = new Probe("probe", new Vector(-1.4664541859104577E8, -2.8949304626334388E7, 2241.9186033698497), new Vector(63.29024702239812, -33.49000052271595, -15.072908267640539), 1.0, 11.0);
    public Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyToTitan;
    public List<LocalDateTime> timeStatesSorted;
    public boolean isSeconds;
    public int stepSizeToTitan;
    public ToTitan() {
        var eph = new EphemerisLoader(2, probe, 1);
        timeStatesSorted = loadTimeStates(eph);
        historyToTitan = loadHistory(eph);//It's history up to a moment just before a step size of getting into orbit of Titan (within 300km from the surface)
        isSeconds = eph.isSeconds();
        stepSizeToTitan = eph.getStepSize();

    }
    public ToTitan(Probe probe){
        var eph = new EphemerisLoader(2, probe, 1);
        timeStatesSorted = loadTimeStates(eph);
        historyToTitan = loadHistory(eph);
    }

    private Hashtable<LocalDateTime, ArrayList<CelestialBodies>> loadHistory(EphemerisLoader eph) {
        Hashtable<LocalDateTime, ArrayList<CelestialBodies>> history = new Hashtable<>(eph.history);
        if(eph.isSeconds()) history.remove(timeStatesSorted.getLast().plusSeconds(eph.getStepSize()));
        else history.remove(timeStatesSorted.getLast().plusMinutes(eph.getStepSize()));
        return history;
    }

    private List<LocalDateTime> loadTimeStates(EphemerisLoader ephemerisLoader) {
        ephemerisLoader.solve();
        var times = new ArrayList(ephemerisLoader.history.keySet().stream().sorted().toList());
        times.removeLast();
        return times;
    }


    public static void main(String[] args) {
    ToTitan toTitan = new ToTitan();

    }
}
