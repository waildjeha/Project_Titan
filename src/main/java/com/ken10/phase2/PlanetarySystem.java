package com.ken10.phase2;

import com.ken10.OdeFunction;

import java.util.ArrayList;
import java.util.Arrays;

public class PlanetarySystem {
    ArrayList<Body> planetarySystem;
    double time;
    static final double G = 6.6743e-20;
    public PlanetarySystem() {
        Object [][] initialState = hardCodedDataset();
    }

    public double getTime() {
        return time;
    }

    public static ArrayList<Body> evaluateDerivatives(double time, ArrayList<Body> planetarySystem) {
        ArrayList<Body> derivatives = new ArrayList<>();

        for (int i = 0; i < planetarySystem.size(); i++) {
            Body original = planetarySystem.get(i);

            // 1) Derivative of position = current velocity
            double dx  = original.getVx();
            double dy  = original.getVy();
            double dz  = original.getVz();

            // 2) Derivative of velocity = computed acceleration
            double[] accel = computeAcceleration(planetarySystem, i);
            double dvx = accel[0];
            double dvy = accel[1];
            double dvz = accel[2];

            // 3) Make a new Body that holds these derivative values
            //    Because we only need 7 numeric fields for a Body, do:
            Body derivativeBody = new Body(
                    original.getName(),
                    dx,   // x-derivative
                    dy,   // y-derivative
                    dz,   // z-derivative
                    dvx,  // Vx-derivative
                    dvy,  // Vy-derivative
                    dvz,  // Vz-derivative
                    original.getMass() // keep mass the same or 0 if you consider derivative of mass
            );

            derivatives.add(derivativeBody);
        }
        derivatives.set(0, planetarySystem.get(0));
        return derivatives;
    }

    static Object[][] hardCodedDataset() {
        Object[][] hardcodedPlanets = new Object[11][8];

        // Sun
        hardcodedPlanets[0] = new Object[] {
                "Sun",
                "0.00E+00",    // x
                "0.00E+00",    // y
                "0.00E+00",    // z
                "0.00E+00",    // vx
                "0.00E+00",    // vy
                "0.00E+00",    // vz
                "1.99E+30"     // mass
        };

        // Mercury
        hardcodedPlanets[1] = new Object[] {
                "Mercury",
                "-5.67E+07",   // x
                "-3.23E+07",   // y
                "2.58E+06",    // z
                "1.39E+01",    // vx
                "-4.03E+01",   // vy
                "-4.57E+00",   // vz
                "3.30E+23"     // mass
        };

        // Venus
        hardcodedPlanets[2] = new Object[] {
                "Venus",
                "-1.04E+08",
                "-3.19E+07",
                "5.55E+06",
                "9.89E+00",
                "-3.37E+01",
                "-1.03E+00",
                "4.87E+24"
        };

        // Earth
        hardcodedPlanets[3] = new Object[] {
                "Earth",
                "-1.47E+08",
                "-2.97E+07",
                "2.75E+04",
                "5.31E+00",
                "-2.93E+01",
                "6.69E-04",
                "5.97E+24"
        };

        // Moon
        hardcodedPlanets[4] = new Object[] {
                "Moon",
                "-1.47E+08",
                "-2.95E+07",
                "5.29E+04",
                "4.53E+00",
                "-2.86E+01",
                "6.73E-02",
                "7.35E+22"
        };

        // Mars
        hardcodedPlanets[5] = new Object[] {
                "Mars",
                "-2.15E+08",
                "1.27E+08",
                "7.94E+06",
                "-1.15E+01",
                "-1.87E+01",
                "-1.11E-01",
                "6.42E+23"
        };

        // Jupiter
        hardcodedPlanets[6] = new Object[] {
                "Jupiter",
                "5.54E+07",
                "7.62E+08",
                "-4.40E+06",
                "-1.32E+01",
                "1.29E+01",
                "5.22E-02",
                "1.90E+27"
        };

        // Saturn
        hardcodedPlanets[7] = new Object[] {
                "Saturn",
                "1.42E+09",
                "-1.91E+08",
                "-5.33E+07",
                "7.48E-01",
                "9.55E+00",
                "-1.96E-01",
                "5.68E+26"
        };

        // Titan
        hardcodedPlanets[8] = new Object[] {
                "Titan",
                "1.42E+09",
                "-1.92E+08",
                "-5.28E+07",
                "5.95E+00",
                "7.68E+00",
                "2.54E-01",
                "1.35E+23"
        };

        // Uranus
        hardcodedPlanets[9] = new Object[] {
                "Uranus",
                "1.62E+09",
                "2.43E+09",
                "-1.19E+07",
                "-5.72E+00",
                "3.45E+00",
                "8.70E-02",
                "8.68E+25"
        };

        // Neptune
        hardcodedPlanets[10] = new Object[] {
                "Neptune",
                "4.47E+09",
                "-5.31E+07",
                "-1.02E+08",
                "2.87E-02",
                "5.47E+00",
                "-1.13E-01",
                "1.02E+26"
        };

        return hardcodedPlanets;
    }


    static double[] computeAcceleration(ArrayList<Body> state, int planetIndex) {
        // Create an array to store the acceleration in x, y, and z directions
        double[] acceleration = new double[3];
        Arrays.fill(acceleration, 0.0);// Initialize to 0 for each direction

        // state[0] = planet name
        // state[1] = x coord
        // state[2] = y coord
        // state[3] = z coord
        // state[4] = Vx
        // state[5] = Vy
        // state[6] = Vz
        // state[7] = planet mass
        Body planet = state.get(planetIndex);
        // Get the current planet's mass and position
        double massPlanet = planet.getMass();
        double[] positionPlanet = {
                planet.getX(),  // x
                planet.getY(),  // y
                planet.getZ()   // z
        };

        // Iterate through all other planets to calculate the gravitational force on the current planet
        for (int i = 0; i < state.size(); i++) {
            if (i == planetIndex) continue;  // Skip the planet itself
            Body tmpPlanet = state.get(i);
            // Get the mass and position of the other planet
            double massOtherPlanet = tmpPlanet.getMass();
            double[] positionOtherPlanet = {
                    tmpPlanet.getX(),  // x
                    tmpPlanet.getY(),  // y
                    tmpPlanet.getZ()   // z
            };

            // Compute the difference vector (x_i - x_j)
            double[] differenceVector = {
                    positionPlanet[0] - positionOtherPlanet[0],  // dx
                    positionPlanet[1] - positionOtherPlanet[1],  // dy
                    positionPlanet[2] - positionOtherPlanet[2]   // dz
            };
            //System.out.println("diff vector for planet " + planet.getName() +": "  + differenceVector[0] + " " + differenceVector[1] + " " + differenceVector[2]);

            // Compute the distance between the two planets
            double distanceSquared = Math.pow(differenceVector[0], 2) + Math.pow(differenceVector[1], 2) + Math.pow(differenceVector[2], 2);
            if (distanceSquared == 0)
                // If the distance is zero, skip the calculation for this pair
                continue;

            double distance = Math.sqrt(distanceSquared);

            // Gravitational force magnitude (F = G * m1 * m2 / r^2), but we will divide by mass for acceleration (a = F/m)
            double forceMagnitude = -G * massPlanet * massOtherPlanet / Math.pow(distance, 3); // G * m1 * m2 / r^3 (normalize by distance^3)
            //System.out.println("forceMagnitude: " + forceMagnitude);
            // Update acceleration components using the force magnitude and direction
            acceleration[0] += forceMagnitude * differenceVector[0];
            acceleration[1] += forceMagnitude * differenceVector[1];
            acceleration[2] += forceMagnitude * differenceVector[2];
        }

        // Return the computed acceleration for the planet in the form of [ax, ay, az]
        return acceleration;
    }



    static void printPlanetarySystem(ArrayList<Body> planetarySystem) {
    for (Body body : planetarySystem) {
        System.out.println(body.toString());
    }
    }

    static void updatePlanetarySystem(){

    }
    static void updatePlanetaryDerivatives(ArrayList<Body> planetarySystem) {

    }



}
