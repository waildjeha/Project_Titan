package com.ken10.Phase2.Missions;

import com.ken10.Phase2.OptimizationAlgorithms.LaunchData;
import com.ken10.Phase2.OptimizationAlgorithms.OptimalVelocityFinder;
import com.ken10.Phase2.OptimizationAlgorithms.RK4Probe;
import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;
import com.ken10.Phase2.StatesCalculations.RK4Solver;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class BackToEarth extends Orbit{
    private static final LocalDateTime endTimeMission = LocalDateTime.of(2027, 4,1,0, 0,0);
    private BackToEarth() {
        solve();
    }


    private void solve(){
        var t0 = historyEphemeris.getEndTime();
        var probePos = historyEphemeris.position(BodyID.SPACESHIP, t0);
        var titanPos = historyEphemeris.position(BodyID.TITAN, t0);
        double altitude = probePos.getDistance(titanPos);
        System.out.println("Probe is at altitude of: " + (altitude-R_TITAN) + "km above Titan surface, time " + t0); // Should be 2026-03-20T05:06:54
        ArrayList<CelestialBodies> bodies = new ArrayList<>(historyEphemeris.history.get(t0));
        if(bodies.size() == 12){bodies.removeLast();}
        LaunchData launchData = new LaunchData(BodyID.EARTH, BodyID.TITAN, probePos, t0, endTimeMission, bodies);
        OptimalVelocityFinder optimalVelocityFinder = new OptimalVelocityFinder(launchData);
        optimalVelocityFinder.solve();
        RK4Probe simulation = optimalVelocityFinder.getFoundData();
        var historyPlanets = launchData.getHistoryPlanets();
        var historyProbe = simulation.historyProbe;
        LocalDateTime t1 = simulation.getClosestDistTime();
        for (var t = t0; t.isBefore(t1)||t.isEqual(t1); t = t1.plusMinutes(2)) {
            ArrayList<CelestialBodies> state = historyPlanets.get(t);
            state.add(historyProbe.get(t));
            historyEphemeris.putHistory(t, state);
        }
    }

    public static EphemerisLoader getEphemerisLoader(){
        BackToEarth backToEarth = new BackToEarth();
        return backToEarth.historyEphemeris;
    }

    public static void main(String[] args) {
        EphemerisLoader loader = BackToEarth.getEphemerisLoader();
    }
}
