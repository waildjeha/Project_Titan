package com.ken10.entities;
import com.ken10.entities.planet.PlanetModel;
import com.ken10.Other.Vector;

import java.util.ArrayList;

/**
 * no pluto :(
 */
public class SolarSystem {
    public static ArrayList<CelestialBodies> CreatePlanets() {
        ArrayList<CelestialBodies> bodies = new ArrayList<>();

        CelestialBodies Sun = new PlanetModel("sun",new Vector(0.00E+00, 0.00E+00, 0.00E+00),new Vector(0.00E+00, 0.00E+00, 0.00E+00),1.99E+30);
        CelestialBodies Mercury = new PlanetModel("mercury",new Vector(-5.67E+07, -3.23E+07, 2.58E+06),new Vector( 1.39E+01, -4.03E+01, -4.57E+00), 3.30E+23);
        CelestialBodies Venus = new PlanetModel("venus",new Vector(-1.04E+08, -3.19E+07, 5.55E+06),new Vector( 9.89E+00, -3.37E+01, -1.03E+00), 4.87E+24);
        CelestialBodies Earth = new PlanetModel("earth",new Vector(-1.47E+08, -2.97E+07, 2.75E+04), new Vector(5.31E+00, -2.93E+01, 6.69E-04), 5.97E+24);
        CelestialBodies Moon = new PlanetModel("moon",new Vector(-1.47E+08, -2.95E+07, 5.29E+04), new Vector(4.53E+00, -2.86E+01, 6.73E-02), 7.35E+22);
        CelestialBodies Mars = new PlanetModel("mars",new Vector(-2.15E+08, 1.27E+08, 7.94E+06), new Vector(-1.15E+01, -1.87E+01, -1.11E-01), 6.42E+23);
        CelestialBodies Jupiter = new PlanetModel("jupiter",new Vector(5.54E+07, 7.62E+08, -4.40E+06), new Vector(-1.32E+01, 1.29E+01, 5.22E-02), 1.90E+27);
        CelestialBodies Saturn = new PlanetModel("saturn",new Vector(1.42E+09, -1.91E+08, -5.33E+07), new Vector(7.48E-01, 9.55E+00, -1.96E-01), 5.68E+26);
        CelestialBodies Titan = new PlanetModel("titan",new Vector(1.42E+09, -1.92E+08, -5.28E+07), new Vector(5.95E+00, 7.68E+00, 2.54E-01), 1.35E+23);
        CelestialBodies Uranus = new PlanetModel("uranus",new Vector(1.62E+09, 2.43E+09, -1.19E+07), new Vector(-5.72E+00, 3.45E+00, 8.70E-02), 8.68E+25);
        CelestialBodies Neptune = new PlanetModel("neptune",new Vector(4.47E+09, -5.31E+07, -1.02E+08), new Vector(2.87E-02, 5.47E+00, -1.13E-01), 1.02E+26);

        bodies.add(Sun);
        bodies.add(Mercury);
        bodies.add(Venus);
        bodies.add(Earth);
        bodies.add(Moon);
        bodies.add(Mars);
        bodies.add(Jupiter);
        bodies.add(Saturn);
        bodies.add(Titan);
        bodies.add(Uranus);
        bodies.add(Neptune);

        return bodies;
    }
}
