package com.ken10.Phase2.Missions;

import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

public class ToTitan {

    protected Hashtable<LocalDateTime, ArrayList<CelestialBodies>> history;

    public ToTitan() {
        EphemerisLoader ephemerisLoader = new EphemerisLoader(2, false,1);
        history = ephemerisLoader.history;
    }
}
