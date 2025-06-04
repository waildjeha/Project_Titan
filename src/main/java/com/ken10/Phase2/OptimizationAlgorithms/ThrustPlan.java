package com.ken10.Phase2.OptimizationAlgorithms;

import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Vector;

import java.util.ArrayList;

public interface ThrustPlan {

    //
    public Vector findThrust(ArrayList<CelestialBodies> currentState);

}
