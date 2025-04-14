package com.ken10.NumericalSolvers;

import com.ken10.Entities.CelestialBodies;
import com.ken10.Entities.Planets.PlanetModel;
import com.ken10.ODEs.Solver;
import java.util.ArrayList;
import com.ken10.Entities.GravityCalc;
import com.ken10.Other.Vector;

public class RK4Solver extends Solver {

    public RK4Solver(ArrayList<CelestialBodies> planetarySystem, double startTime, double endTime, double stepSize) {
        super(planetarySystem, startTime, endTime, stepSize);
    }

    /**
     * Advance the solution by one step using RK4 method
     */
    @Override
    public void step() {
        // Calculate the derivatives at the current time step using GravityCalc
        ArrayList<PlanetModel> k1 = GravityCalc.computeDerivatives(time, convertToPlanetModels(planetarySystem));

        // Compute k2: Estimate the state at t + stepSize / 2
        ArrayList<CelestialBodies> k2 = new ArrayList<>();
        for (int i = 0; i < planetarySystem.size(); i++) {
            CelestialBodies body = planetarySystem.get(i);
            Vector newPosition = body.getPosition().add(k1.get(i).getVelocity().multiply(stepSize / 2));
            Vector newVelocity = body.getVelocity().add(k1.get(i).getVelocity().multiply(stepSize / 2));
            CelestialBodies newBody = createNewBody(body, newPosition, newVelocity);
            k2.add(newBody);
        }
        ArrayList<PlanetModel> k2Derivatives = GravityCalc.computeDerivatives(time + stepSize / 2, convertToPlanetModels(k2));

        // Compute k3: Estimate the state at t + stepSize / 2 (using k2)
        ArrayList<CelestialBodies> k3 = new ArrayList<>();
        for (int i = 0; i < planetarySystem.size(); i++) {
            CelestialBodies body = planetarySystem.get(i);
            Vector newPosition = body.getPosition().add(k2Derivatives.get(i).getVelocity().multiply(stepSize / 2));
            Vector newVelocity = body.getVelocity().add(k2Derivatives.get(i).getVelocity().multiply(stepSize / 2));
            CelestialBodies newBody = createNewBody(body, newPosition, newVelocity);
            k3.add(newBody);
        }
        ArrayList<PlanetModel> k3Derivatives = GravityCalc.computeDerivatives(time + stepSize / 2, convertToPlanetModels(k3));

        // Compute k4: Estimate the state at t + stepSize (using k3)
        ArrayList<CelestialBodies> k4 = new ArrayList<>();
        for (int i = 0; i < planetarySystem.size(); i++) {
            CelestialBodies body = planetarySystem.get(i);
            Vector newPosition = body.getPosition().add(k3Derivatives.get(i).getVelocity().multiply(stepSize));
            Vector newVelocity = body.getVelocity().add(k3Derivatives.get(i).getVelocity().multiply(stepSize));
            CelestialBodies newBody = createNewBody(body, newPosition, newVelocity);
            k4.add(newBody);
        }
        ArrayList<PlanetModel> k4Derivatives = GravityCalc.computeDerivatives(time + stepSize, convertToPlanetModels(k4));

        // Update the planetary system's state using the RK4 formula
        for (int i = 0; i < planetarySystem.size(); i++) {
            CelestialBodies body = planetarySystem.get(i);

            // Update position using velocity derivatives
            Vector positionUpdate = k1.get(i).getVelocity().add(k2Derivatives.get(i).getVelocity().multiply(2))
                    .add(k3Derivatives.get(i).getVelocity().multiply(2))
                    .add(k4Derivatives.get(i).getVelocity()).multiply(stepSize / 6);

            // Update velocity using acceleration derivatives
            Vector velocityUpdate = k1.get(i).getVelocity().add(k2Derivatives.get(i).getVelocity().multiply(2))
                    .add(k3Derivatives.get(i).getVelocity().multiply(2))
                    .add(k4Derivatives.get(i).getVelocity()).multiply(stepSize / 6);

            body.setPosition(body.getPosition().add(positionUpdate));
            body.setVelocity(body.getVelocity().add(velocityUpdate));
        }

        // Increment time by step size
        time += stepSize;
        recordState();
    }

    private ArrayList<PlanetModel> convertToPlanetModels(ArrayList<CelestialBodies> bodies) {
        ArrayList<PlanetModel> planetModels = new ArrayList<>();
        for (CelestialBodies body : bodies) {
            if (body instanceof PlanetModel) {
                planetModels.add((PlanetModel) body);
            } else {
                // Create a temporary PlanetModel for the rocket
                planetModels.add(new PlanetModel(body.getName(), body.getPosition(), body.getVelocity(), body.getMass()));
            }
        }
        return planetModels;
    }

    private CelestialBodies createNewBody(CelestialBodies original, Vector position, Vector velocity) {
        if (original instanceof PlanetModel) {
            return new PlanetModel(original.getName(), position, velocity, original.getMass());
        } else {
            // For non-PlanetModel bodies (like Rocket), create a new instance of the same type
            return new PlanetModel(original.getName(), position, velocity, original.getMass());
        }
    }
}
