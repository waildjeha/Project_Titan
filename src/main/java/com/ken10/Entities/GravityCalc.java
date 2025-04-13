package com.ken10.Entities;

import com.ken10.Entities.Planets.PlanetModel;
import com.ken10.Other.*;

import java.util.List;

public class GravityCalc {
public static final double g = 6.6743e-20;

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

            double forceMagnitude = g * nextPlanet.getMass() / (distance * distance * distance);
            acceleration = acceleration.add(v.multiply(forceMagnitude));
        }

        return acceleration;
    }
    public static Derivative computeDerivative(PlanetModel planet, List<PlanetModel> planets) {
        Vector velocity = planet.getVelocity();
        Vector acceleration = computeAcceleration(planets, planets.indexOf(planet));

        return new Derivative(velocity, acceleration);
    }


}
