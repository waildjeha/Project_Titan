package com.ken10.phase2;


import java.util.ArrayList;

public class InitialDataset {
    public static Object[][] dataset = hardcodedDataset();
    public static ArrayList<Body> bodyArrayList = convertedDataset(hardcodedDataset());
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
    static ArrayList<Body> convertedDataset(Object[][] hardcodedPlanets){
        ArrayList<Body> dataset = new ArrayList<>();
        for (Object[] hardcodedPlanet : hardcodedPlanets) {
            Body body = new Body();
            body.setName(hardcodedPlanet[0].toString()); // Set the name first (e.g., "Sun")

            // Now parse the numerical values correctly
            body.setX(Double.parseDouble(hardcodedPlanet[1].toString()));
            body.setY(Double.parseDouble(hardcodedPlanet[2].toString()));
            body.setZ(Double.parseDouble(hardcodedPlanet[3].toString()));
            body.setVx(Double.parseDouble(hardcodedPlanet[4].toString()));
            body.setVy(Double.parseDouble(hardcodedPlanet[5].toString()));
            body.setVz(Double.parseDouble(hardcodedPlanet[6].toString()));
            body.setMass(Double.parseDouble(hardcodedPlanet[7].toString()));

            dataset.add(body);
        }
        return dataset;
    }


}
