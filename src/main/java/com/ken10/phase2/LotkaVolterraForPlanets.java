//package com.ken10.phase2;
//
//import java.util.Arrays;
//
//public class LotkaVolterraForPlanets {
//    static Object[][] planetStatesHardcoded = hardcodedDataset();
//    static final double gConstant = 6.6743e-20;
//    //had to convert from provided 6.6743e-11 which was expressed in meters
//    //we need to use kilometers. Verify tho //TODO
//
//
//    static double[][] computeDerivatives(double[][] state){
//        double[][] derivatives = new double[state.length][6];
//        for (int i = 0; i < state.length; i++) {
//            derivatives[i][0]=state[i][3]; // dx = dx/dt = Vx
//            derivatives[i][1]=state[i][4]; //dy = dy/dt = Vy
//            derivatives[i][2]=state[i][5]; //dz = dz/dt = Vz
//            double[] accelerations = computeAcceleration(state, i);
//            derivatives[i][3]=accelerations[0];
//            derivatives[i][4]=accelerations[1];
//            derivatives[i][5]=accelerations[2];
//        }
//        return derivatives;
//    }
//
//    static double[] computeAcceleration(double[][] state, int planetIndex) {
//        // Create an array to store the acceleration in x, y, and z directions
//        double[] acceleration = new double[3];
//        Arrays.fill(acceleration, 0.0);// Initialize to 0 for each direction
//
//        // state[0] = planet name
//        // state[1] = x coord
//        // state[2] = y coord
//        // state[3] = z coord
//        // state[4] = Vx
//        // state[5] = Vy
//        // state[6] = Vz
//        // state[7] = planet mass
//
//        // Get the current planet's mass and position
//        double massPlanet = Double.parseDouble(planetStatesHardcoded[planetIndex][7].toString());
//        double[] positionPlanet = {
//                state[planetIndex][0],  // x
//                state[planetIndex][1],  // y
//                state[planetIndex][2]   // z
//        };
//
//        // Iterate through all other planets to calculate the gravitational force on the current planet
//        for (int i = 0; i < state.length; i++) {
//            if (i == planetIndex) continue;  // Skip the planet itself
//
//            // Get the mass and position of the other planet
//            double massOtherPlanet = Double.parseDouble(planetStatesHardcoded[i][7].toString());
//            double[] positionOtherPlanet = {
//                    state[i][0],  // x
//                    state[i][1],  // y
//                    state[i][3]   // z
//            };
//
//            // Compute the difference vector (x_i - x_j)
//            double[] differenceVector = {
//                    positionPlanet[0] - positionOtherPlanet[0],  // dx
//                    positionPlanet[1] - positionOtherPlanet[1],  // dy
//                    positionPlanet[2] - positionOtherPlanet[2]   // dz
//            };
//
//            // Compute the distance between the two planets
//            double distanceSquared = Math.pow(differenceVector[0], 2) + Math.pow(differenceVector[1], 2) + Math.pow(differenceVector[2], 2);
//            double distance = Math.sqrt(distanceSquared);
//
//            // Gravitational force magnitude (F = G * m1 * m2 / r^2), but we will divide by mass for acceleration (a = F/m)
//            double forceMagnitude = -gConstant * massPlanet * massOtherPlanet / Math.pow(distance, 3); // G * m1 * m2 / r^3 (normalize by distance^3)
//
//            // Update acceleration components using the force magnitude and direction
//            acceleration[0] += forceMagnitude * differenceVector[0];
//            acceleration[1] += forceMagnitude * differenceVector[1];
//            acceleration[2] += forceMagnitude * differenceVector[2];
//        }
//
//        // Return the computed acceleration for the planet in the form of [ax, ay, az]
//        return acceleration;
//    }
//}
