package com.ken10.ODEs;

import com.ken10.entities.CelestialBodies;
import com.ken10.entities.planet.PlanetModel;
import com.ken10.entities.rocket.Rocket;
import com.ken10.Other.*;

import java.util.ArrayList;
import java.util.List;

/**
 * Calculations for computing derivatives and acceleration.
 */
public class GravityCalc {
    public static final double g = 6.6743e-20; // Gravitational constant in km^3/kg/s^2

    /**
     * Calculates acceleration Vectors of each celestial body in space.
     * Uses newtons laws of universal gravity.
     * Uses Vector operations to form the equation as it's not possible normally. lines 34-42.
     *
     * @param bodies planets and rocket.
     * @param bodyIndex position of celestial body in list.
     * @return acceleration.
     */
    public static Vector computeAcceleration(List<CelestialBodies> bodies, int bodyIndex) {
        Vector acceleration = new Vector(0.0, 0.0, 0.0);
        CelestialBodies currentBody = bodies.get(bodyIndex);

        for (int i = 0; i < bodies.size(); i++) {
            if (i == bodyIndex) continue;

            CelestialBodies otherBody = bodies.get(i);
            Vector differenceVector = currentBody.getPosition().subtract(otherBody.getPosition());
            double distance = differenceVector.magnitude();

            if (distance == 0) continue;

            double forceMagnitude = (-g * currentBody.getMass() * otherBody.getMass())/ Math.pow(distance, 2);
            Vector unitDirection = differenceVector.multiply(1.0/distance);
            Vector forceVector = unitDirection.multiply(forceMagnitude);
            Vector bodyAcceleration = forceVector.multiply(1.0/currentBody.getMass());

            acceleration = acceleration.add(bodyAcceleration);
        }

        return acceleration;
    }

    /**
     * Calculates derivatives at certain times to model planet movement.
     * Creates new bodies for instances of body objects to hold derivatives.
     * sun remains at the origin.
     *
     * @param time time -_-
     * @param bodies planets and rocket.
     * @return derivatives at the time.
     */
    public static ArrayList<CelestialBodies> computeDerivatives(double time, ArrayList<CelestialBodies> bodies) {
        ArrayList<CelestialBodies> derivatives = new ArrayList<>();

        for (int i = 0; i < bodies.size(); i++) {
            CelestialBodies original = bodies.get(i);

            // ds/dt = current velocity
            Vector positionDerivative = original.getVelocity();

            Vector acceleration = computeAcceleration(bodies, i);

            //Create a new Body for the derivatives
            CelestialBodies derivativeBody;
            if (original instanceof PlanetModel) {
                derivativeBody = new PlanetModel(original.getName(), positionDerivative, acceleration, original.getMass());
            } else {
                derivativeBody = new Rocket(original.getName(), positionDerivative, acceleration, original.getMass());
            }

            derivatives.add(derivativeBody);
        }

        // If the Sun is stationary, use zeroed-out derivatives
        CelestialBodies sunDerivative = new PlanetModel("Sun", new Vector(0, 0, 0), new Vector(0, 0, 0), bodies.get(0).getMass());
        derivatives.set(0, sunDerivative);

        return derivatives;
    }
}
