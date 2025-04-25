package com.ken10.Dillons_Phase2.entities;

import com.ken10.Dillons_Phase2.entities.Other.Vector;
import com.ken10.Dillons_Phase2.entities.astro.Emhemeris.EphemerisLoader;
import com.ken10.Dillons_Phase2.entities.planet.BodyID;
import com.ken10.Dillons_Phase2.entities.planet.CelestialBodies;
import com.ken10.Dillons_Phase2.entities.planet.SolarSystem;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.ken10.Dillons_Phase2.entities.Other.Vector.getDistance;

public class Main {
    public static void main(String[] args) {
        var eph = new EphemerisLoader(2);
        eph.solve();
        var t0 = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
        var state = eph.initialState;
        var earth = state.get(BodyID.EARTH.index()).getPosition();
        var titan = state.get(BodyID.TITAN.index()).getPosition();
        System.out.println(getDistance(earth, titan)/1.496e8);
        for (int i = 0; i < 365; i++) {
            LocalDateTime t = t0.plusDays(i);
            state = eph.history.get(t);
            if (state == null) {
                System.out.printf("No record for %s%n", t);
                continue;                     // or break;
            }
            earth = state.get(BodyID.EARTH.index()).getPosition();
            titan = state.get(BodyID.TITAN.index()).getPosition();
//            eph.printState(state);
            System.out.println(getDistance(earth, titan)/1.496e8);
        }
    }
}
