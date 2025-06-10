package com.ken10.Phase2.Missions;

import com.ken10.Phase2.OptimizationAlgorithms.LaunchData;
import com.ken10.Phase2.OptimizationAlgorithms.OptimalVelocityFinder;
import com.ken10.Phase2.OptimizationAlgorithms.RK4Probe;
import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Probe;
import com.ken10.Phase2.SolarSystemModel.Vector;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * This class simulates the return journey of a space probe from Titan to Earth
 * after it has been successfully inserted into orbit around Titan Orbit.
 *
 */
public class BackToEarth extends Orbit{
    private static final Probe probe3 = new Probe("probe", new Vector(1.4152356646242814E9, 1.0276702269962315E8, -5.760974061398743E7),
    new Vector(-54.91999240303898, -8.25004904249947, 5.026601360046181), 1.0, 11.0);
    private final boolean optimize;
    private static final LocalDateTime endTimeMission = LocalDateTime.of(2027, 4,1,0, 0,0);

    private BackToEarth(boolean optimize) {
        this.optimize = optimize;
        solve();
    }


    private void solve(){
        var t0 = historyEphemeris.getEndTime();
        var probePos = historyEphemeris.position(BodyID.SPACESHIP, t0);
        var titanPos = historyEphemeris.position(BodyID.TITAN, t0);
        double altitude = probePos.getDistance(titanPos);
        System.out.println("Probe is at altitude of: " + (altitude-R_TITAN) + "km above Titan surface, time " + t0); // Should be 2026-03-20T05:06:54
        ArrayList<CelestialBodies> bodies = new ArrayList<>(historyEphemeris.history.get(t0));
        evaluateFuel(bodies, historyEphemeris.velocity(BodyID.SPACESHIP, t0));
        System.out.println("Total fuel consumption with hardcoded data is " + totalFuelConsumption);
        if(bodies.size() == 12){bodies.removeLast();}
        LaunchData launchData = new LaunchData(BodyID.EARTH, BodyID.TITAN, probePos, t0, endTimeMission, bodies);
        RK4Probe simulation;
        if(optimize) {
            OptimalVelocityFinder optimalVelocityFinder = new OptimalVelocityFinder(launchData);
            optimalVelocityFinder.solve();
            simulation = optimalVelocityFinder.getFoundData();
        }
        else{
            simulation = new RK4Probe(probe3, launchData);
            simulation.solve();
        }
        var historyPlanets = launchData.getHistoryPlanets();
        var historyProbe = simulation.historyProbe;
        LocalDateTime t1 = simulation.getClosestDistTime();
        System.out.println("Probe gets to Earth at " + t1);
        System.out.println("Distance at final time equals : " + historyPlanets.get(t1).get(BodyID.EARTH.index()).getPosition().getDistance(historyProbe.get(t1).getPosition()));
        for (var t = t0; t.isBefore(t1)||t.isEqual(t1); t = t.plusMinutes(2)) {
            ArrayList<CelestialBodies> state = historyPlanets.get(t);
            state.add(historyProbe.get(t));
            historyEphemeris.putHistory(t, state);
        }
    }

    public static EphemerisLoader getEphemerisLoader(boolean optimize){
        BackToEarth backToEarth = new BackToEarth(optimize);
        return backToEarth.historyEphemeris;
    }

    public static void main(String[] args) {
        EphemerisLoader loader = BackToEarth.getEphemerisLoader(false);
    }
}
