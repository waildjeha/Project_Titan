package com.ken10.entities.probe;

import com.ken10.entities.CelestialBodies;
import com.ken10.Other.Vector;

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
    public void setDistanceToTarget(double distance) {this.distanceToTarget = distance;}
    public double getDistanceToTarget() {return distanceToTarget;}

    /**
     * Can select the target body to visit
     * @param target any celestial body at which you want to visit.
     */
    public void setTarget(CelestialBodies target) {
        this.targetPosition = target.getPosition();
        this.targetVelocity = target.getVelocity();
        updateDistanceToTarget();
    }

    /**
     * position of probe relative to the target currently.
     * @return relative position.
     */
    public Vector getRelativePosition() {
        return targetPosition.subtract(getPosition());
    }

    /**
     * Velocity of probe relative to the target currently.
     * @return relative velocity.
     */
    public Vector getRelativeVelocity() {
        return targetVelocity.subtract(getVelocity());
    }

    /**
     * Defines a time at which the probe and body are closest.
     * Takes a function of relative position & velocity w.r.t.
     * As we want to minimize the distance we square it, becoming a dot product.
     * Expanding it becomes a quadratic of the form: At^2 + Bt + C
     * A = relativeVel dot relativeVel
     * B = 2 * (relativePos dot relativeVel)
     * C = relativePos dot relativePos
     * to find the minimum we can take the derivative = 2At + B = 0.
     * this simplifies to t = -B/2A = timeToClosest.
     *
     * @return intercept Vector
     */
    public Vector calculateInterceptPoint() {
        Vector relativePos = getRelativePosition();
        Vector relativeVel = getRelativeVelocity();
        
        double timeToClosest = -relativePos.dot(relativeVel) / relativeVel.dot(relativeVel);
        if (timeToClosest < 0) timeToClosest = 0;

        Vector futureTargetPos = targetPosition.add(targetVelocity.multiply(timeToClosest));
        Vector futureProbePos = getPosition().add(getVelocity().multiply(timeToClosest));

        return futureTargetPos.subtract(futureProbePos);
    }

    private void updateDistanceToTarget() {
        if (targetPosition != null) {
            distanceToTarget = getPosition().subtract(targetPosition).magnitude();
        }
    }
}
