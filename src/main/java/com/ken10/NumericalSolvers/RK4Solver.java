package com.ken10.NumericalSolvers;
import com.ken10.entities.*;
import com.ken10.entities.planet.PlanetModel;
import com.ken10.entities.SolarSystem;
import com.ken10.entities.probe.Probe;
import com.ken10.entities.rocket.Rocket;
import com.ken10.ODEs.GravityCalc;
import com.ken10.ODEs.Solver;
import java.util.ArrayList;
import com.ken10.Other.Vector;

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
     * @param stepSize stepsize -_-
     */
    public RK4Solver(ArrayList<CelestialBodies> planetarySystem, double startTime, double endTime, double stepSize) {
        super(planetarySystem, startTime, endTime, stepSize);
    }

    private void printState(double time) {
        System.out.println("\nTime: " + time/2628000);
        System.out.println("----------------------------------------");
        for (CelestialBodies body : planetarySystem) {
            Vector pos = body.getPosition();
            Vector vel = body.getVelocity();
            System.out.printf("%s:\n", body.getName());
            System.out.printf("  Position: (%.2f, %.2f, %.2f)\n", pos.getX(), pos.getY(), pos.getZ());
            System.out.printf("  Velocity: (%.2f, %.2f, %.2f)\n", vel.getX(), vel.getY(), vel.getZ());
            System.out.printf("  Distance from origin: %.2f\n", pos.magnitude());
            System.out.println("----------------------------------------");
        }
    }

    /**
     * Advance the solution by one step using RK4 method
     */
    @Override
    public void step() {
        // Calculate the derivatives at the current time step using GravityCalc
        double h = stepSize;
        double t = time;
        double t2 = t + h/2;
        double t4 = t + h;
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
                    .add(k4Body.getPosition())
                    .multiply(h / 6);
            body.setPosition(body.getPosition().add(positionUpdate));

            Vector velocityUpdate = k1Body.getVelocity()
                    .add(k2Body.getVelocity().multiply(2))
                    .add(k3Body.getVelocity().multiply(2))
                    .add(k4Body.getVelocity())
                    .multiply(h / 6);
            body.setVelocity(body.getVelocity().add(velocityUpdate));
        }

        time += stepSize;
        printState(time);
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
            return new PlanetModel(original.getName(), position, velocity, original.getMass());
        } else if (original instanceof Rocket){
            return new Rocket(original.getName(), position, velocity, original.getMass());
        }else {
            return new Probe(original.getName(), position, velocity, original.getMass());
        }
    }

    /**
     * temp main for testing.
     * @param args
     */
    public static void main(String[] args) {
        ArrayList<CelestialBodies> planetarySystem = SolarSystem.CreatePlanets();
        // for each month
        RK4Solver rk4 = new RK4Solver(planetarySystem, 0, 31557600, 2628000);
        rk4.solve();

//        rk4.solve();
    }
}
