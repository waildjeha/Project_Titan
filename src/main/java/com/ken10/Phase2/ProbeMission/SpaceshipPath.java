package com.ken10.Phase2.ProbeMission;

import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Probe;
import com.ken10.Phase2.SolarSystemModel.SolarSystem;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class SpaceshipPath {

    public SpaceshipPath(){
        EphemerisLoader ephemerisLoader = new EphemerisLoader(1);
        ephemerisLoader.solve();
        EvolutionAlgorithm trajectory = new EvolutionAlgorithm();
        RK4Probe simulation = trajectory.optimizeTrajectory();
        int stepSize = simulation.getStepSizeMin();
        double distance = simulation.getClosestDistance();
        LocalDateTime time = simulation.getClosestDistTime();
        ArrayList<CelestialBodies> bodies = new ArrayList<>();
        while(distance<2575) {
            time.minusMinutes(stepSize);
            ArrayList<CelestialBodies> state = ephemerisLoader.history.get(time);
            CelestialBodies titan = state.get(BodyID.TITAN.index());
            Probe probe = simulation.historyProbe.get(time);
            if (titan.getPosition().getDistance(probe.getPosition()) < distance) {
                if (!bodies.isEmpty()) bodies.clear();
                distance = titan.getPosition().getDistance(probe.getPosition());
                bodies = state;
                state.add(probe);
            }
        }
    }
}
