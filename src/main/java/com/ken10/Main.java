package com.ken10;
import com.ken10.Phase2.OptimizationAlgorithms.RK4Probe;
import com.ken10.Phase2.SolarSystemModel.*;
import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;

public class Main {
    public static Probe probe = new Probe("probe", new Vector(-1.4664541859104577E8, -2.8949304626334388E7, 2241.9186033698497), new Vector(62.37766685559131, -32.92234163864975, -15.852587264581345));
    public static void main(String[] args) {
        EphemerisLoader h = new EphemerisLoader(2, probe, 1);
        h.solve();
        System.out.println(h.getEndTime());
    }
    }








//EphemerisLoader: simulation = ----------------------------------------------------------
//Initial probe position and velocity: (-1.4664541859104577E8, -2.8949304626334388E7, 2241.9186033698497) (63.289186302398114, -33.49000052271595, -15.073058267640539)
//Velocity magnitude relative to earth: 60.06507025221596
//Closest Distance to Titan: 3253.5191042454117
//Date of closest approach: 2026-03-20T02:36

//----------------------------------------------------------
//Initial probe position and velocity: (-1.4664541859104577E8, -2.8949304626334388E7, 2241.9186033698497) (62.37766685559131, -32.92234163864975, -15.852587264581345)
//Velocity magnitude relative to earth: 59.35185082621291
//Closest Distance to Titan: 2545.9443054973567
//Date of closest approach: 2026-03-17T20:52



//        Probe probe = new Probe("dominik", Earth.EARTH_INITIAL_POSITION.addX(Earth.RADIUS), new Vector (63.289186302398114, -33.49000052271595, -15.073058267640539));
//        EphemerisLoader eph = new EphemerisLoader(2,probe, 1);
//        eph.solve();
//        System.out.println(eph.history.get(eph.getEndTime()).get(BodyID.TITAN.index()).getPosition()
//                .getDistance(eph.history.get(eph.getEndTime()).get(BodyID.SPACESHIP.index()).getPosition()));