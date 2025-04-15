package com.ken10.ODEs;

import com.ken10.Entities.CelestialBodies;
import com.ken10.Entities.Planets.PlanetModel;
import com.ken10.Entities.Rocket.Rocket;
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
     *
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

            double forceMagnitude = (g * currentBody.getMass() * otherBody.getMass())/ Math.pow(distance, 3);
            Vector unitDirection = differenceVector.multiply(1.0/distance);
            Vector forceVector = unitDirection.multiply(forceMagnitude);
            Vector bodyAcceleration = forceVector.multiply(1.0/currentBody.getMass());

            acceleration = acceleration.add(bodyAcceleration);
        }

        return acceleration;
    }

    /**
     * Calculates derivatives at certain times to model planet movement.
     *
     * @param time time -_-
     * @param bodies planets and rocket.
     * @return derivatives at the time.
     */
    public static ArrayList<CelestialBodies> computeDerivatives(double time, ArrayList<CelestialBodies> bodies) {
        ArrayList<CelestialBodies> derivatives = new ArrayList<>();

        for (int i = 0; i < bodies.size(); i++) {
            CelestialBodies original = bodies.get(i);

            // 1) Derivative of position = velocity (as Vector)
            Vector positionDerivative = original.getVelocity();

            // 2) Derivative of velocity = computed acceleration (as Vector)
            Vector acceleration = computeAcceleration(bodies, i);

            // 3) Create a new Body for the derivatives
            CelestialBodies derivativeBody;
            if (original instanceof PlanetModel) {
                derivativeBody = new PlanetModel(original.getName(), positionDerivative, acceleration, original.getMass());
            } else {
                derivativeBody = new Rocket(original.getName(), positionDerivative, acceleration, original.getMass());
            }

            derivatives.add(derivativeBody);
        }

        // If the Sun is stationary, we use zeroed-out derivatives
        CelestialBodies sunDerivative = new PlanetModel("Sun", new Vector(0, 0, 0), new Vector(0, 0, 0), bodies.get(0).getMass());
        derivatives.set(0, sunDerivative);

        return derivatives;
    }
}
