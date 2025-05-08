package com.ken10.Phase2.SolarSystemModel;


import java.util.ArrayList;

import static com.ken10.Phase2.SolarSystemModel.Vector.getDistance;


/**
 * Class for Probe mission.
 * implements multiple useful methods for titan mission.
 */
public class Probe extends CelestialBodies {
    private boolean isActive = false;
    private double distanceToTarget;
    public static final double MASS = 50000;
//    private Vector position;
//    private Vector velocity;


    public Probe(String name, Vector position, Vector velocity) {
        super(name, position, velocity, MASS);
//        this.position = position;
//        this.velocity = velocity;
    }

    private void getDistanceToTarget(ArrayList<CelestialBodies> state) {
        distanceToTarget = getDistance(position, state.get(BodyID.TITAN.index()).getPosition());
    }
//    public Vector getPosition() {
//        return position;
//    }
//    public void setPosition(Vector position) {
//        this.position = position;
//    }
//    public Vector getVelocity() {
//        return velocity;
//    }
//    public void setVelocity(Vector velocity) {
//        this.velocity = velocity;
//    }

    public Vector getRelativeVelocity(CelestialBodies target) {
        return target.getVelocity().subtract(getVelocity());
    }

    public void launch(){
        isActive = true;
    }

    public double getDistanceToTarget() {
        return distanceToTarget;
    }

    @Override
    public String toString() {
        return position.toString() + " " + velocity.toString();
    }


}
