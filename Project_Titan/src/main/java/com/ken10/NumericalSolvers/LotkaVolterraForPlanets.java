package com.ken10.NumericalSolvers;

import java.util.Arrays;

public class LotkaVolterraForPlanets {
    static Object[][] planetStatesHardcoded = hardcodedDataset();
    static final double gConstant = 6.6743e-20;
    //had to convert from provided 6.6743e-11 which was expressed in meters
    //we need to use kilometers. Verify tho //TODO

    static Object[][] hardcodedDataset() {
        Object[][] hardcodedPlanets = new Object[11][8];

        Object[] sun = {"Sun", "0.00E+00", "0.00E+00", "0.00E+00", "0.00E+00", "0.00E+00", "0.00E+00", "1.99E+30"};
        hardcodedPlanets[0] = sun;

        Object[] mercury = {"Mercury", "-1.47E+08", "-2.97E+07", "2.75E+04", "5.31E+00", "-2.93E+01", "6.69E-04", "3.30E+23"};
        hardcodedPlanets[1] = mercury;

        Object[] venus = {"Venus", "-1.04E+08", "-3.19E+07", "5.55E+06", "9.89E+00", "-3.37E+01", "-1.03E+00", "4.87E+24"};
        hardcodedPlanets[2] = venus;

        Object[] earth = {"Earth", "-1.47E+08", "-2.97E+07", "2.75E+04", "5.31E+00", "-2.93E+01", "6.69E-04", "5.97E+24"};
        hardcodedPlanets[3] = earth;

        Object[] moon = {"Moon", "-1.47E+08", "-2.95E+07", "5.29E+04", "4.53E+00", "-2.86E+01", "6.73E-02", "7.35E+22"};
        hardcodedPlanets[4] = moon;

        Object[] mars = {"Mars", "-2.15E+08", "1.27E+08", "7.94E+06", "-1.15E+01", "-1.87E+01", "-1.11E-01", "6.42E+23"};
        hardcodedPlanets[5] = mars;

        Object[] jupiter = {"Jupiter", "5.54E+07", "7.62E+08", "-4.40E+06", "-1.32E+01", "1.29E+01", "5.22E-02", "1.90E+27"};
        hardcodedPlanets[6] = jupiter;

        Object[] saturn = {"Saturn", "1.42E+09", "-1.91E+08", "-5.33E+07", "7.48E-01", "9.55E+00", "-1.96E-01", "5.68E+26"};
        hardcodedPlanets[7] = saturn;

        Object[] titan = {"Titan", "1.42E+09", "-1.92E+08", "-5.28E+07", "5.95E+00", "7.68E+00", "2.54E-01", "1.35E+23"};
        hardcodedPlanets[8] = titan;

        Object[] uranus = {"Uranus", "1.62E+09", "2.43E+09", "-1.19E+07", "-5.72E+00", "3.45E+00", "8.70E-02", "8.68E+25"};
        hardcodedPlanets[9] = uranus;

        Object[] neptune = {"Neptune", "4.47E+09", "-5.31E+07", "-1.02E+08", "2.87E-02", "5.47E+00", "-1.13E-01", "1.02E+26"};
        hardcodedPlanets[10] = neptune;

        return hardcodedPlanets;
    }

    static double[][] computeDerivatives(double[][] state){
        double[][] derivatives = new double[state.length][6];
        for (int i = 0; i < state.length; i++) {
            derivatives[i][0]=state[i][3]; // dx = dx/dt = Vx
            derivatives[i][1]=state[i][4]; //dy = dy/dt = Vy
            derivatives[i][2]=state[i][5]; //dz = dz/dt = Vz
            double[] accelerations = computeAcceleration(state, i);
            derivatives[i][3]=accelerations[0];
            derivatives[i][4]=accelerations[1];
            derivatives[i][5]=accelerations[2];
        }
        return derivatives;
    }

    static double[] computeAcceleration(double[][] state, int planetIndex) {
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

        // Get the current planet's mass and position
        double massPlanet = Double.parseDouble(planetStatesHardcoded[planetIndex][7].toString());
        double[] positionPlanet = {
                state[planetIndex][0],  // x
                state[planetIndex][1],  // y
                state[planetIndex][2]   // z
        };

        // Iterate through all other planets to calculate the gravitational force on the current planet
        for (int i = 0; i < state.length; i++) {
            if (i == planetIndex) continue;  // Skip the planet itself

            // Get the mass and position of the other planet
            double massOtherPlanet = Double.parseDouble(planetStatesHardcoded[i][7].toString());
            double[] positionOtherPlanet = {
                    state[i][0],  // x
                    state[i][1],  // y
                    state[i][3]   // z
            };

            // Compute the difference vector (x_i - x_j)
            double[] differenceVector = {
                    positionPlanet[0] - positionOtherPlanet[0],  // dx
                    positionPlanet[1] - positionOtherPlanet[1],  // dy
                    positionPlanet[2] - positionOtherPlanet[2]   // dz
            };

            // Compute the distance between the two planets
            double distanceSquared = Math.pow(differenceVector[0], 2) + Math.pow(differenceVector[1], 2) + Math.pow(differenceVector[2], 2);
            double distance = Math.sqrt(distanceSquared);

            // Gravitational force magnitude (F = G * m1 * m2 / r^2), but we will divide by mass for acceleration (a = F/m)
            double forceMagnitude = -gConstant * massPlanet * massOtherPlanet / Math.pow(distance, 3); // G * m1 * m2 / r^3 (normalize by distance^3)

            // Update acceleration components using the force magnitude and direction
            acceleration[0] += forceMagnitude * differenceVector[0];
            acceleration[1] += forceMagnitude * differenceVector[1];
            acceleration[2] += forceMagnitude * differenceVector[2];
        }

        // Return the computed acceleration for the planet in the form of [ax, ay, az]
        return acceleration;
    }
}
