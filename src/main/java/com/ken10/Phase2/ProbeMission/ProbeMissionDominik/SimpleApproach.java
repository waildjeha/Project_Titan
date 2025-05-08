package com.ken10.Phase2.ProbeMission.ProbeMissionDominik;

import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Probe;
import com.ken10.Phase2.SolarSystemModel.Vector;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;
import com.ken10.Phase2.StatesCalculations.RK4Probe;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;

import static com.ken10.Phase2.SolarSystemModel.Vector.getDistance;

public class SimpleApproach {
    //// History always preloaded
    private static final Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyPlanets;
    //// Step size always defined to be the same for Rk4Probe and EphemerisLoader
    private static final int stepSizeMins = 2;
    //// Deadline to finish our mission
    private static final LocalDateTime endTime = LocalDateTime.of(2026, 4, 1, 0, 0, 0);
    //// Probe that we select to be the best fit
    private Probe initialProbe;
    //// How close we get to Titan using the selected Probe
    private double closestDistance;
    //// At what date and time do we get to Titan
    private LocalDateTime closestDistanceTime;
    //// I think we might need it for the GUI
    private Hashtable<LocalDateTime, Probe> historyProbe;
    //// Time to keep track, also defines when the simulation begins
    private LocalDateTime time = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
    //// Number of degrees we increment by when we look for the all possible positions on the equator
    /// we can start from
    private double positionStepSize = 1;
    //// Velocity cap, we break current loop when we exceed the given velocity
    private static final double V_MAX = 60; //km/s


    static {
        EphemerisLoader eph = new EphemerisLoader(stepSizeMins);
        eph.solve();
        historyPlanets = eph.history;
    }

    public SimpleApproach() {
        this.closestDistance = Double.MAX_VALUE;
        this.closestDistanceTime = LocalDateTime.of(2025, 4, 1, 0, 0, 0);
    }

    public void solve() {
    List<Vector> startingPos = new EarthPositions(positionStepSize).getPositions();

        for(Vector v : startingPos) {
//            System.out.println(v.toString());
            for(double x = 0 ; x <= 60 ; x=x+0.01) {
//                System.out.println(x);
                for(double y = 0 ; y <= 60 ; y=y+0.01) {
//                    System.out.println(y);
                    if(Math.sqrt(x*x+y*y) > V_MAX) {break;}
                        for(double z = 0 ; z <= 60 ; z=z+0.01) {
                            if(Math.sqrt(x*x+y*y+z*z) > V_MAX) {break;}
                            Probe probe = new Probe("dominik", v, new Vector(x,y,z));
                            RK4Probe rk4Probe = new RK4Probe(probe,historyPlanets, 2);
                            rk4Probe.solve();
                            double closestDistanceTmp = rk4Probe.getClosestDistance();
                            Probe initialProbeTmp = rk4Probe.historyProbe.get(LocalDateTime.of(2025, 4, 1, 0, 0, 0));
                            LocalDateTime closestDistTime = rk4Probe.getClosestDistTime();
                            if(closestDistanceTmp<closestDistance) {
                                closestDistance = closestDistanceTmp;
                                closestDistanceTime = closestDistTime;
                                initialProbe = initialProbeTmp;
                                System.out.println(closestDistTime.toString());
                                System.out.println(initialProbe.toString());
                                System.out.println(closestDistance);
                            }

                        }
                }
            }
        }
    }

    public static void main(String[] args) {
        SimpleApproach sa = new SimpleApproach();
        sa.solve();
    }





}
