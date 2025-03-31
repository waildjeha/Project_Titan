package com.ken10.NumericalSolvers;

import java.util.List;
import java.util.ArrayList;
import com.ken10.Entities.Planets;
import com.ken10.Other.Vector;

public class RK4ForPlanets {
    double stepSize;
    double time;
    double startTime;
    double endTime;
    List<Planets> planets;

    public RK4ForPlanets(double stepSize, double time, double startTime, double endTime, List<Planets> planets) {
        this.stepSize = stepSize;
        this.time = time;
        this.startTime = startTime;
        this.endTime = endTime;
        this.planets = planets;
    }

    public void step() {
        double h = stepSize;

        // First stage
        List<Vector[]> k1 = LotkaVolterraForPlanets.computeDerivatives(planets);

        // Second stage
        List<Planets> y2 = updateState(planets, k1, h / 2);
        List<Vector[]> k2 = LotkaVolterraForPlanets.computeDerivatives(y2);

        // Third stage
        List<Planets> y3 = updateState(y2, k2, h / 2);
        List<Vector[]> k3 = LotkaVolterraForPlanets.computeDerivatives(y3);

        // Fourth stage
        List<Planets> y4 = updateState(y3, k3, h);
        List<Vector[]> k4 = LotkaVolterraForPlanets.computeDerivatives(y4);

        // Update state using RK4 formula
        for (int i = 0; i < planets.size(); i++) {
            Planets p = planets.get(i);

            Vector newVelocity = p.getVelocity().add(
                    k1.get(i)[1].multiply(h / 6)
                            .add(k2.get(i)[1].multiply(h / 3))
                            .add(k3.get(i)[1].multiply(h / 3))
                            .add(k4.get(i)[1].multiply(h / 6))
            );

            Vector newPosition = p.getPosition().add(
                    k1.get(i)[0].multiply(h / 6)
                            .add(k2.get(i)[0].multiply(h / 3))
                            .add(k3.get(i)[0].multiply(h / 3))
                            .add(k4.get(i)[0].multiply(h / 6))
            );

            planets.set(i, new Planets(p.getPlanetName(), newPosition, newVelocity, p.getMass()));
        }

        time += stepSize;
    }

    private List<Planets> updateState(List<Planets> original, List<Vector[]> k, double dt) {
        List<Planets> newState = new ArrayList<>();
        for (int i = 0; i < original.size(); i++) {
            Planets p = original.get(i);
            Vector newPosition = p.getPosition().add(k.get(i)[0].multiply(dt));
            Vector newVelocity = p.getVelocity().add(k.get(i)[1].multiply(dt));

            newState.add(new Planets(p.getPlanetName(), newPosition, newVelocity, p.getMass()));
        }
        return newState;
    }
}


