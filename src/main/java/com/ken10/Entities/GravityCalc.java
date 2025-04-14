package com.ken10.Entities;

import com.ken10.Entities.Planets.PlanetModel;
import com.ken10.Other.*;

import java.util.ArrayList;
import java.util.List;

public class GravityCalc {
    public static final double g = 6.6743e-20; // Gravitational constant in km^3/kg/s^2

    public static Vector computeAcceleration(List<PlanetModel> planets, int planetIndex) {
        Vector acceleration = new Vector(0.0, 0.0, 0.0);
        PlanetModel currentPlanet = planets.get(planetIndex);

        for (int i = 0; i < planets.size(); i++) {
            if (i == planetIndex) continue;

            PlanetModel nextPlanet = planets.get(i);
            Vector v = nextPlanet.getPosition().subtract(currentPlanet.getPosition());
            double distanceSquared = v.magnitude() * v.magnitude();
            double distance = Math.sqrt(distanceSquared);

            if (distance == 0) continue;

            double forceMagnitude = g * nextPlanet.getMass() / distanceSquared;
            acceleration = acceleration.add(v.multiply(forceMagnitude));
        }

        return acceleration;
    }

    public static ArrayList<PlanetModel> computeDerivatives(double time, ArrayList<PlanetModel> planetarySystem) {
        ArrayList<PlanetModel> derivatives = new ArrayList<>();

        for (int i = 0; i < planetarySystem.size(); i++) {
            PlanetModel original = planetarySystem.get(i);

            // 1) Derivative of position = velocity (as Vector)
            Vector positionDerivative = original.getVelocity();

            // 2) Derivative of velocity = computed acceleration (as Vector)
            Vector acceleration = computeAcceleration(planetarySystem, i);
            Vector velocityDerivative = acceleration;

            // 3) Create a new Body for the derivatives
            PlanetModel derivativeBody = new PlanetModel(
                    original.getName(),
                    positionDerivative,
                    velocityDerivative,
                    original.getMass()
            );

            derivatives.add(derivativeBody);
        }

        // If the Sun is stationary, we use zeroed-out derivatives
        PlanetModel sunDerivative = new PlanetModel("Sun", new Vector(0, 0, 0), new Vector(0, 0, 0), planetarySystem.get(0).getMass());
        derivatives.set(0, sunDerivative);

        return derivatives;
    }
}
