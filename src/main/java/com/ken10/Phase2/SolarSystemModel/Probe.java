package com.ken10.Phase2.SolarSystemModel;


import java.util.ArrayList;

/**
 * Class for Probe mission.
 * implements multiple useful methods for titan mission.
 */
public class Probe extends CelestialBodies {
    private boolean isActive;
    private double distanceToTarget;
    private Vector targetPosition;
    private Vector initialVelocity;
    public static final double MASS = 50000;

    public Probe(String name, Vector position, Vector velocity) {
        super(name, position, velocity, MASS);
        this.isActive = false;
    }
    /**
     * Can select the target body to visit
     * @param target any celestial body at which you want to visit.
     */
//    public void setTargetBody(CelestialBodies target) {
//        this.targetPosition = target.getPosition();
//        this.targetVelocity = target.getVelocity();
//        updateDistanceToTarget();
//    }
    public void setTargetPosition(Vector target) {
        this.targetPosition = target;
    }

    public void launch(){
        isActive = true;
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
        solarSystem.add(probe);
        Vector interceptPoint = null;
        Vector targetPosition;
        Vector probePosition;


       // RK4Solver solver = new RK4Solver(solarSystem,startTime,endTime,stepSize);

        double minDistance = Double.MAX_VALUE;
        double time = 0;
         while (time < endTime){
          //   solver.step();
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

        Vector startPos = earthPos.add(new Vector(radius, 0, 0));


        return new Probe(getName(), startPos, earthVel);

    }
}
