package com.ken10.Phase2.StatesCalculations;

import com.ken10.Phase2.SolarSystemModel.*;

import java.time.LocalDateTime;
import java.util.ArrayList;

/**
 * Uses Rk4 method to model planetary motion.
 * extends solver which holds the step method.
 * Works very accurately.
 */
public class RK4Solver extends Solver {
    /**
     * Initializes a RK4 solver.
     *
     * @param planetarySystem planets and rocket in space.
     * @param startTime initial time
     * @param endTime Finish time, probably will be a year.
     * @param stepSizeMins stepsize -_-
     */
    public RK4Solver(ArrayList<CelestialBodies> planetarySystem, LocalDateTime startTime, LocalDateTime endTime, int stepSizeMins, boolean isSeconds) {
        super(planetarySystem, startTime, endTime, stepSizeMins, isSeconds);
    }


    /**
     * Advance the solution by one step using RK4 method
     */
    @Override
    public void step() {
        double h = isSeconds ? stepSize : stepSize*60;
//        if(isSeconds) h = stepSize;
//        else h = stepSize*60;
        long hNano = safeDoubleToInt(h) * 1000000000L;
        // Calculate the derivatives at the current time step using GravityCalc

        LocalDateTime t = time;
        LocalDateTime t2 = t.plusNanos(hNano/2);
        LocalDateTime t4 = t.plusNanos(hNano);
        ArrayList<CelestialBodies> y1 = planetarySystem;
        int n = y1.size();
        ArrayList<CelestialBodies> k1 = GravityCalc.computeDerivatives(time, y1);

        // Compute y2: Estimate the state at t + stepSize / 2
        ArrayList<CelestialBodies> y2 = new ArrayList<>();
        for (int i = 0; i < n ; i++) {
            CelestialBodies original = y1.get(i);
            CelestialBodies k1Body = k1.get(i);

            Vector newPosition = original.getPosition().add(k1Body.getPosition().multiply(h / 2));
            Vector newVelocity = original.getVelocity().add(k1Body.getVelocity().multiply(h / 2));

            CelestialBodies newBody = createNewBody(original, newPosition, newVelocity);
            y2.add(newBody);
        }
        ArrayList<CelestialBodies> k2 = GravityCalc.computeDerivatives(t2, y2);

        // Compute y3: Estimate the state at t + stepSize / 2 (using y2)
        ArrayList<CelestialBodies> y3 = new ArrayList<>();
        for (int i = 0; i < n ; i++) {
            CelestialBodies original = y2.get(i);
            CelestialBodies k2Body = k2.get(i);

            Vector newPosition = original.getPosition().add(k2Body.getPosition().multiply(h / 2));
            Vector newVelocity = original.getVelocity().add(k2Body.getVelocity().multiply(h / 2));

            CelestialBodies newBody = createNewBody(original, newPosition, newVelocity);
            y3.add(newBody);
        }
        ArrayList<CelestialBodies> k3 = GravityCalc.computeDerivatives(t2, y3);


        // Compute y4: Estimate the state at t + stepSize (using y3)
        ArrayList<CelestialBodies> y4 = new ArrayList<>();
        for (int i = 0; i < n ; i++) {
            CelestialBodies original = y3.get(i);
            CelestialBodies k3Body = k3.get(i);

            Vector newPosition = original.getPosition().add(k3Body.getPosition().multiply(h));
            Vector newVelocity = original.getVelocity().add(k3Body.getVelocity().multiply(h));

            CelestialBodies newBody = createNewBody(original, newPosition, newVelocity);
            y4.add(newBody);
        }
        ArrayList<CelestialBodies> k4 = GravityCalc.computeDerivatives(t4, y4);

        // Update the planetary system's state using the RK4 formula
        for (int i = 0; i < n ; i++) {
           CelestialBodies body = y1.get(i);
           CelestialBodies k1Body = k1.get(i);
           CelestialBodies k2Body = k2.get(i);
           CelestialBodies k3Body = k3.get(i);
           CelestialBodies k4Body = k4.get(i);

            Vector positionUpdate = k1Body.getPosition()
                    .add(k2Body.getPosition().multiply(2))
                    .add(k3Body.getPosition().multiply(2))
                    .add(k4Body.getPosition()).multiply(h / 6);
            body.setPosition(body.getPosition().add(positionUpdate));

            Vector velocityUpdate = k1Body.getVelocity()
                    .add(k2Body.getVelocity().multiply(2))
                    .add(k3Body.getVelocity().multiply(2))
                    .add(k4Body.getVelocity())
                    .multiply(h / 6);
            body.setVelocity(body.getVelocity().add(velocityUpdate));

        }


        time = time.plusNanos(hNano);
        //printState();
        recordState();

    }


    /**
     * Creates a temporary celestial body for cases of planet or rocket.
     *
     * @param original
     * @param position
     * @param velocity
     * @return a new planet or rocket instance.
     */
    private CelestialBodies createNewBody(CelestialBodies original, Vector position, Vector velocity) {
        if (original instanceof PlanetModel) {
            return new PlanetModel(original.getName(), position, velocity, original.getMass(), original.getRelativeScalingFactor(), original.getSize());
        }
        else if (original instanceof Earth) {
            return new Earth(original.getName(), position, velocity, original.getMass(), original.getRelativeScalingFactor(), original.getSize());
        }
        else if (original instanceof Titan) {
            return new Titan(original.getName(), position, velocity, original.getMass(), original.getRelativeScalingFactor(), original.getSize());
        }
        else
            return new Probe(original.getName(), position, velocity, original.getRelativeScalingFactor(), original.getSize());
    }

    public static int safeDoubleToInt(double value) {
        double tolerance = 10e-9;
        double rounded = Math.round(value);
        if (Math.abs(value - rounded) > tolerance) {
            throw new IllegalArgumentException("Value " + value + " is not close to an integer.");
        }
        return (int) rounded;
    }


    public static void main(String[] args) {
        double i = 8-10e-10;
       System.out.println((int)Math.round(i));

    }
}
