package com.ken10.Phase2.ProbeMission.ProbeMissionDominik;

import com.ken10.Phase2.SolarSystemModel.CelestialBodies;
import com.ken10.Phase2.SolarSystemModel.Probe;
import com.ken10.Phase2.SolarSystemModel.Vector;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;

public class Grid {
    private final double minX;
    private final double minY;
    private final double minZ;
    private final double maxX;
    private final double maxY;
    private final double maxZ;
    private RK4Probe rk4Probe;

    public Grid(double minX, double minY, double minZ, double maxX, double maxY, double maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public Grid(){
        this.minX = -60;
        this.maxX = 60;
        this.minY = -60;
        this.maxY = 60;
        this.minZ = -60;
        this.maxZ = 60;
    }


    public Vector getRandomPosition(){
        double x = minX + (Math.random() * (maxX - minX));
        double y = minY + (Math.random() * (maxY - minY));
        double z = minZ + (Math.random() * (maxZ - minZ));
        return new Vector(x, y, z);
    }

    public void evaluate(int iterations, Vector initialPosition, int stepSizeMins, Hashtable<LocalDateTime, ArrayList<CelestialBodies>> historyPlanets) {
        double closestDistance = Double.MAX_VALUE;
        RK4Probe bestSimulation = null;
        for (int i = 0; i <= iterations; i++) {
            Probe probe = new Probe("dominik", initialPosition, getRandomPosition());
            RK4Probe simulation = new RK4Probe(probe, historyPlanets, stepSizeMins);
            simulation.solve();
            if (simulation.getClosestDistance() < closestDistance) {
                closestDistance = simulation.getClosestDistance();
                bestSimulation = simulation;
            }
        }
        rk4Probe = bestSimulation;
    }

    public double getGridBest(){
        return rk4Probe.getClosestDistance();
    }

    public RK4Probe getRk4Probe() {
        return rk4Probe;
    }

    public double getMinX() {
        return minX;
    }
    public double getMinY() {
        return minY;
    }
    public double getMinZ() {
        return minZ;
    }
    public double getMaxX(){
        return maxX;
    }
    public double getMaxY(){
        return maxY;
    }
    public double getMaxZ(){
        return maxZ;
    }

    public static void main(String[] args) {

    }
}
