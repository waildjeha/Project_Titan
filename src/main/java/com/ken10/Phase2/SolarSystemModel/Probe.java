package com.ken10.Phase2.SolarSystemModel;

import com.ken10.Phase2.StatesCalculations.EphemerisLoader;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.ken10.Phase2.SolarSystemModel.Vector.getDistance;

public class Probe extends CelestialBodies {
    private double distanceToTarget;
    public static final double MASS = 50000;
    private static final double EARTH_RADIUS = 6370; // km
    private static final double MAX_THRUST = 3e7;
    private static final double FUEL_CONSUMPTION_RATE = 1.0;

    private Vector initialPosition;
    private Vector initialVelocity;
    private double totalFuelUsed = 0;
    private Vector currentThrust = new Vector(0, 0, 0);
    private Vector targetVelocity;
    private double burnStartTime = 0;
    private double burnDuration = 0;
    private boolean isActive = false;
    private LocalDateTime titanArrivalTime = null;
    private LocalDateTime earthReturnTime = null;
    private boolean isEngine = false;

    public Probe(String name, Vector position, Vector velocity) {
        super(name, position, velocity, MASS);
        this.initialPosition = position;
        this.initialVelocity = velocity;
        this.currentThrust = new Vector(0, 0, 0);
        this.isEngine = false;
        this.totalFuelUsed = 0;
        this.burnStartTime = 0;
        this.burnDuration = 0;
        this.isActive = false;
        this.titanArrivalTime = null;
        this.earthReturnTime = null;
    }
    public Probe(String name, Vector position, Vector velocity, double mass, double scaling, double size){
        super(name, position, velocity, mass, scaling, size);
    }

    public Probe(String name, Vector position, Vector velocity, boolean isNuclearEngine) {
        super(name, position, velocity, MASS);
        this.initialPosition = position;
        this.initialVelocity = velocity;
        this.currentThrust = new Vector(0, 0, 0);
        this.isEngine = isNuclearEngine;
        this.totalFuelUsed = 0;
        this.burnStartTime = 0;
        this.burnDuration = 0;
        this.isActive = false;
        this.titanArrivalTime = null;
        this.earthReturnTime = null;
    }

    public Probe(String name, Vector position, Vector velocity, double relativeScalingFactor, double size) {
        super(name, position, velocity, MASS);
    }

    /**
     * Use for quick maneuvers like path correction or deceleration
     * for orbital insertion
     * @param impulse momentum change desired
     */
    public void applyImpulse(Vector impulse) {
        if (!isActive) return;
        // v + I/M
        Vector newVelocity = getVelocity().add(impulse.multiply(1.0 / MASS));
        setVelocity(newVelocity);
        double fuelUsed = impulse.magnitude() * FUEL_CONSUMPTION_RATE;
        totalFuelUsed += fuelUsed;
    }

    /**
     * Setup for a thrust
     * @param targetVelocity optimized velocity goal for after thrust
     * @param currentTime time
     */
    public void planThrustManeuver(Vector targetVelocity, double currentTime) {
        if (!isActive) return;

        this.targetVelocity = targetVelocity;
        this.burnStartTime = currentTime;

        Vector velocityChange = targetVelocity.subtract(getVelocity());
        double vMagnitude = velocityChange.magnitude();

        double maxAcceleration = MAX_THRUST / MASS;
        this.burnDuration = vMagnitude / maxAcceleration;
    }

    /**
     * updates velocity over a time step
     * @param currentTime
     * @param timeStep
     */
    public void updateThrust(double currentTime, double timeStep) {
        if (!isActive || currentTime < burnStartTime) return;

        double timeIntoBurn = currentTime - burnStartTime;
        if (timeIntoBurn >= burnDuration) {
            currentThrust = new Vector(0, 0, 0);
            return;
        }
        Vector deltaV = targetVelocity.subtract(getVelocity());
        Vector thrustDirection = deltaV.normalize();
        currentThrust = thrustDirection.multiply(MAX_THRUST);

        Vector impulse = currentThrust.multiply(timeStep);
        Vector newVelocity = getVelocity().add(impulse.multiply(1.0 / MASS));
        setVelocity(newVelocity);
        double fuelUsed = currentThrust.magnitude() * timeStep * FUEL_CONSUMPTION_RATE;
        totalFuelUsed += fuelUsed;
    }

    public void launch() {
        isActive = true;
        burnStartTime = 0;
        //Todo target velocity here
        planThrustManeuver(targetVelocity, 0);
    }

    public void resetThrust() {
        currentThrust = new Vector(0, 0, 0);
        targetVelocity = getVelocity();
        burnDuration = 0;
    }

    private void getDistanceToTarget(ArrayList<CelestialBodies> state) {
        distanceToTarget = getDistance(position, state.get(BodyID.TITAN.index()).getPosition());
    }

    public Probe copy() {
        return new Probe(getName(), getPosition().copy(), getVelocity().copy());
    }

    public Vector getRelativeVelocity(CelestialBodies target) {
        return target.getVelocity().subtract(getVelocity());
    }

    public double getDistanceToTarget() {
        return distanceToTarget;
    }

    public double getTotalFuelUsed() {
        return totalFuelUsed;
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

    /**
     * method to correct position along course
     * @param targetPosition
     * @param currentTime
     */
    public void performCourseCorrection(Vector targetPosition, double currentTime) {
        if (!isActive) return;
        Vector positionError = targetPosition.subtract(getPosition());
        Vector velocityCorrection = new Vector(0, 0, 0); //Todo change to suitable

        Vector correctionImpulse = velocityCorrection.multiply(MASS);
        applyImpulse(correctionImpulse);
    }

}

