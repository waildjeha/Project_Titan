package com.ken10.ProbeMission;

import com.ken10.NumericalSolvers.RK4Solver;
import com.ken10.Other.Vector;
import com.ken10.entities.CelestialBodies;
import com.ken10.entities.SolarSystem;
import com.ken10.entities.probe.Probe;

import java.util.ArrayList;

public class Miss {
    public static void main(String[] args) {
        System.out.println("Start");


        ArrayList<CelestialBodies> solarSystem = SolarSystem.CreatePlanets();
        CelestialBodies earth = solarSystem.get(3);
        CelestialBodies titan = solarSystem.get(8);

        System.out.println("Earth position: " + earth.getPosition());
        System.out.println("Titan position: " + titan.getPosition());
        Probe probe = new Probe("Sad Probe", new Vector(0, 0, 0), new Vector(0, 0, 0), 50000);
        Vector initialVelocity = new Vector(60,0,0);
        Vector earthToTitan = titan.getPosition().subtract(earth.getPosition()).normalize();
        double boostMagnitude = 12.0;
        Vector initialBoost = earthToTitan.multiply(boostMagnitude);
        Vector startPos = earth.getPosition().add(new Vector(6370, 0, 0));
        Vector startVel = earth.getVelocity().add(initialBoost);
        Probe theProbe=probe.setStartingProbe(earth,6370 ,startVel);
        RK4Solver rk4Solver=new RK4Solver(solarSystem,0, 31557600,700);
        theProbe.simulateProbePath(probe,titan, rk4Solver,0,31557600,700);



        {

        }
    }
}