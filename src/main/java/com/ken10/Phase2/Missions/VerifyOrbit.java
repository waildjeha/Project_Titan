package com.ken10.Phase2.Missions;


import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.stream.Collectors;

public class VerifyOrbit {
    protected final Orbit orbit;

    public VerifyOrbit() {
        this.orbit = new Orbit();
    }

    private ArrayList<CelestialBodies> getInitialState() {
        return orbit.historyOrbit.get(orbit.timeStatesSorted.getLast());
    }

    private void solve(){
        Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyOrbit = orbit.historyOrbit;
        Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyInOrbit = new Hashtable<>();
        for(LocalDateTime t : orbit.timeStatesSorted) {
            List<CelestialBodies> states = historyOrbit.get(t);
            Vector spaceshipPosition = states.get(BodyID.SPACESHIP.index()).getPosition();
            Vector titanPosition = states.get(BodyID.TITAN.index()).getPosition();
            double distance = spaceshipPosition.getDistance(titanPosition);
            if(distance < Titan.RADIUS+100) break;
            if(distance < Titan.RADIUS+300) historyInOrbit.put(t, historyOrbit.get(t));
        }
        List<LocalDateTime> historyInOrbitTimes = historyInOrbit.keySet().stream().sorted().toList();
        for(LocalDateTime t : historyInOrbitTimes) {
            ArrayList<CelestialBodies> states = historyInOrbit.get(t);
            HillClimbingOrbit optimization = new HillClimbingOrbit(states, t);
            Vector bestVector = optimization.getBestVector();
        }
        return;
    }

    private void verifyOrbit() {
        var t0 = orbit.timeStatesSorted.getLast();

        EphemerisLoader eph = new EphemerisLoader(getInitialState(), t0, t0.plusMinutes(10), 1, true);
        eph.solve();

        for (LocalDateTime t = t0; t.isBefore(t0.plusMinutes(10)); t = t.plusSeconds(1)) {
            var titanPosVector = eph.position(BodyID.TITAN, t);
            var probePosVector = eph.position(BodyID.SPACESHIP, t);
            System.out.println(t + " Distance between Titan and Spaceship: " + titanPosVector.getDistance(probePosVector));
        }
    }




    public static void main(String[] args) {
    Orbit orbit = new Orbit();
    ArrayList<CelestialBodies> initialState = orbit.historyOrbit.get(orbit.timeStatesSortedOrbit.getLast());
    Vector gravity = GravityCalc.computeAcceleration(initialState, BodyID.SPACESHIP.index());
    Vector orthogonal = gravity.findOrthogonalVector();
    orthogonal = orthogonal.normalize().multiply(gravity.magnitude());
    initialState.get(11).setVelocity(orthogonal);




//        Vector bodyPosition,
//        Vector planetPosition,
//        Vector planetVelocity,
//        double planetMass

    }
}
