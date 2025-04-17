package com.ken10.entities.probe;

import com.ken10.NumericalSolvers.RK4Solver;
import com.ken10.entities.CelestialBodies;
import com.ken10.Other.Vector;
import com.ken10.entities.SolarSystem;
import java.util.ArrayList;

/**
 * Class for Probe mission.
 * implements multiple useful methods for titan mission.
 */
public class Probe extends CelestialBodies {
    private double fuelLevel;
    private boolean isActive;
    private double distanceToTarget;
    private Vector targetPosition;
    private Vector targetVelocity;

    public Probe(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
        this.isActive = true;
    }
    
    public double getFuelLevel() {return fuelLevel;}
    public void setFuelLevel(double fuelLevel) {this.fuelLevel = fuelLevel;}
    public boolean isActiveMission() {return isActive;}
    public void launch(){
        if(!isActiveMission()){
            this.isActive = true;
        }
    }
    public double getDistanceToTarget() {return distanceToTarget;}

    /**
     * Can select the target body to visit
     * @param target any celestial body at which you want to visit.
     */
    public void setTargetBody(CelestialBodies target) {
        this.targetPosition = target.getPosition();
        this.targetVelocity = target.getVelocity();
        updateDistanceToTarget();
    }
    public void setTargetPosition(Vector target) {
        this.targetPosition = target;
    }

    /**
     * position of probe relative to the target currently.
     * @return relative position.
     */
    public Vector getRelativePosition(CelestialBodies target) {
        return target.getPosition().subtract(getPosition());
    }

    /**
     * Velocity of probe relative to the target currently.
     * @return relative velocity.
     */
    public Vector getRelativeVelocity(CelestialBodies target) {
        return target.getVelocity().subtract(getVelocity());
    }

    /**
     * Calculates the target Vector of the probe for the best path to intercept.
     * Uses RK4 to predict future positions.
     * for each step of the rk4 the min distance is replaced if it is less than the previous.
     *
     * @param probe probe used.
     * @param target titan.
     * @param startTime
     * @param endTime
     * @param stepSize
     * @return target vector for the probe
     */
    public Vector calculateInterceptPoint(Probe probe, CelestialBodies target, double startTime, double endTime, double stepSize) {
        ArrayList<CelestialBodies> solarSystem = SolarSystem.CreatePlanets();
        Vector interceptPoint = null;
        Vector targetPosition;
        Vector probePosition;

        RK4Solver solver = new RK4Solver(solarSystem,startTime,endTime,stepSize);

        double minDistance = Double.MAX_VALUE;
        double time = 0;
         while (time < endTime){
             solver.step();
             time += stepSize;

             targetPosition = target.getPosition();
             probePosition = probe.getPosition();

             Vector thisDistance = targetPosition.subtract(probePosition);
             double distance = thisDistance.magnitude();

             if (distance < minDistance){
                 minDistance = distance;
                 interceptPoint = thisDistance;
             }
         }
         return interceptPoint;
    }

    /**
     * Relative distance to target.
     */
    private void updateDistanceToTarget() {
            distanceToTarget = getPosition().subtract(targetPosition).magnitude();
    }

    /**
     * Starting position & velocity for a probe at the surface of launchPlanet.
     * Currently only x-axis. Equator*
     * TODO can start anywhere on launchPlanet's surface.
     *
     * @param launchPlanet planet where the rocket is launched from.
     * @param radius radius of that planet.
     * @return a probe containing starting pos & vel relative to the planet with mass 50000kg
     */
    public Probe setStartingProbe(CelestialBodies launchPlanet, double radius) {
        Vector earthPos = launchPlanet.getPosition();
        Vector earthVel = launchPlanet.getVelocity();

        Vector startPos = earthPos.add(new Vector(radius,0,0));


        return new Probe(getName(),startPos, earthVel,50000);

    }


    /**
     * Applies thrust to the probe in a specific direction.
     * @param direction Direction vector (will be normalized).
     * @param magnitude Magnitude of the thrust force (N).
     * @param duration Duration of thrust (s).
     */

    public void applyThrust(Vector direction, double magnitude, double duration) {
        if (!isActive || fuelLevel <= 0)
            return;
//        it needs to be normalized so it does to represent length, but the direction
        direction = direction.normalize();

//        use formula a = F / m
        Vector acceleration = direction.multiply(magnitude / getMass());
//        set velocity needs to be implemented
        setVelocity(getVelocity().add(acceleration.multiply(duration)));

        double fuelBurned = magnitude * duration * 0.05;

//        discuss what should be our constant
//        how many kg per NÂ·s
        fuelLevel = Math.max(0, fuelLevel - fuelBurned);
    }
}
