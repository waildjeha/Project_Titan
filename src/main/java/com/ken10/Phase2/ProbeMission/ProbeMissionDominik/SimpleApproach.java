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
            for(double Vx = 0 ; Vx <= 60 ; Vx=Vx+1) {
                for(double Vy = 0 ; Vy <= 60 ; Vy=Vy+1) {
                    if(Math.sqrt(Vx*Vx+Vy*Vy) > V_MAX) {break;}
                        for(double Vz = 0 ; Vz <= 60 ; Vz=Vz+1) {
                            if(Math.sqrt(Vx*Vx+Vy*Vy+Vz*Vz) > V_MAX) {break;}
                            Probe probe = new Probe("dominik", v, new Vector(Vx,Vy,Vz));
                            RK4Probe rk4Probe = new RK4Probe(probe,historyPlanets, 2);
                            rk4Probe.solve();
                            double closestDistanceTmp = rk4Probe.getClosestDistance();
                            if(closestDistanceTmp<closestDistance) {
                                closestDistance = closestDistanceTmp;
                                closestDistanceTime = rk4Probe.getClosestDistTime();
                                initialProbe = rk4Probe.getInitialProbe();
                                System.out.println(closestDistanceTime);
                                System.out.println(initialProbe);
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
