package com.ken10.NumericalSolvers;
import com.ken10.Other.Vector;
import com.ken10.Entities.Planets;
import java.util.ArrayList;
import java.util.List;

class LotkaVolterraForPlanets {
    static final double G = 6.6743e-20;

    public static Vector computeAcceleration(List<Planets> planets, int planetIndex) {
        Vector acceleration = new Vector(0.0, 0.0, 0.0);
        Planets currentPlanet = planets.get(planetIndex);

        for (int i = 0; i < planets.size(); i++) {
            if (i == planetIndex) continue;

            Planets nextPlanet = planets.get(i);
            Vector v = currentPlanet.getPosition().subtract(nextPlanet.getPosition());
            double distanceSquared = v.magnitude() * v.magnitude();
            double distance = Math.sqrt(distanceSquared);

            if (distance == 0) continue;

            double forceMagnitude = -G * currentPlanet.getMass() * nextPlanet.getMass() / (distance * distance * distance);
            acceleration = acceleration.add(v.multiply(forceMagnitude));
        }

        return acceleration;
    }

    public static List<Vector[]> computeDerivatives(List<Planets> planets) {
        List<Vector[]> derivatives = new ArrayList<>();

        for (int i = 0; i < planets.size(); i++) {
            Vector velocity = planets.get(i).getVelocity();
            Vector acceleration = computeAcceleration(planets, i);
            derivatives.add(new Vector[]{velocity, acceleration});
        }

        return derivatives;
    }
}
