package com.ken10.Phase2.SolarSystemModel;


import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Hashtable;


import static com.ken10.Phase2.SolarSystemModel.Vector.getDistance;


/**
 * Class for Probe mission.
 * implements multiple useful methods for titan mission.
 */
public class Probe extends CelestialBodies {
    private boolean isActive = false;
    private double distanceToTarget;
    public static final double MASS = 50000;
    private Hashtable<LocalDateTime, Probe> probeHistory = new Hashtable<>();
    private final Vector initialPosition;
    private final Vector initialVelocity;
    private static final LocalDateTime startTime = LocalDateTime.of(2025,4,1,0,0,0);


    public Probe(String name, Vector position, Vector velocity) {
        super(name, position, velocity, MASS);
        this.initialPosition = position;
        this.initialVelocity = velocity;
        probeHistory.put(startTime, this);
    }

    private void getDistanceToTarget(ArrayList<CelestialBodies> state) {
        distanceToTarget = getDistance(position, state.get(BodyID.TITAN.index()).getPosition());
    }
    public Vector getInitialPosition() {
        return initialPosition;
    }
    public Vector getInitialVelocity() {
        return initialVelocity;
    }

    public Hashtable<LocalDateTime, Probe> getProbeHistory() {
        return probeHistory;
    }
    public void putProbeHistory(LocalDateTime time, Probe probe) {
        probeHistory.put(time, probe);
    }

    public Probe copy(){
        return new Probe(getName(), getPosition().copy(), getVelocity().copy());
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

    public static void main(String[] args) {
        EphemerisLoader EPH = new EphemerisLoader(2);
        EPH.solve();

        Vector earthPosition = EPH.history.get(LocalDateTime.of(2025, 4, 1, 0, 0, 0))
                .get(BodyID.EARTH.index()).getPosition();
        Vector titanPosition = EPH.history.get(LocalDateTime.of(2025, 4, 1, 0, 0, 0))
                .get(BodyID.TITAN.index()).getPosition();

// Get direction vector from Earth to Titan
        Vector earthToTitan = titanPosition.subtract(earthPosition);

// Normalize and scale by Earth's radius
        Vector surfaceOffset = earthToTitan.normalize().multiply(6370);

// Get the position on Earth's surface pointing toward Titan
        Vector pointedAtTitan = earthPosition.add(surfaceOffset).multiply(-1);
        System.out.println(pointedAtTitan.normalize().multiply(60));
    }

}
