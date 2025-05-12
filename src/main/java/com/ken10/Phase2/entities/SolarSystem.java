package com.ken10.Phase2.entities;

import com.ken10.Phase2.StatesCalculations.EphemerisLoader;
import com.ken10.Phase2.newtonCalculations.Vector;

import java.time.LocalDateTime;
import java.util.ArrayList;

import static com.ken10.Phase2.newtonCalculations.Vector.getDistance;

/**
 * no pluto :(
 */
public class SolarSystem {
    public static ArrayList<CelestialBodies> CreatePlanets() {
        ArrayList<CelestialBodies> bodies = new ArrayList<>();
        double sunX = -7.596727134361322E+05;
        double sunY = -7.764826803462036E+05;
        double sunZ = 2.520871232682851E+04;
        double sunVX =  1.258146808934948E-02;
        double sunVY = -4.478489369359461E-03;
        double sunVZ = -2.140398498365391E-04;
        Vector positionAdjustment = new Vector(sunX, sunY, sunZ);
        Vector velocityAdjustment = new Vector(sunVX, sunVY, sunVZ);

        CelestialBodies Sun = new PlanetModel(
                "sun",
                new Vector(0.00E+00, 0.00E+00, 0.00E+00),
                new Vector(0.00E+00, 0.00E+00, 0.00E+00),
                1.99E+30
        );

        CelestialBodies Mercury = new PlanetModel(
                "mercury",
                new Vector(-5.67E+07, -3.23E+07, 2.58E+06)
                        .subtract(positionAdjustment),
                new Vector( 1.39E+01, -4.03E+01, -4.57E+00)
                        .subtract(velocityAdjustment),
                3.30E+23
        );

        CelestialBodies Venus = new PlanetModel(
                "venus",
                new Vector(-1.04E+08, -3.19E+07, 5.55E+06)
                        .subtract(positionAdjustment),
                new Vector( 9.89E+00, -3.37E+01, -1.03E+00)
                        .subtract(velocityAdjustment),
                4.87E+24
        );

        CelestialBodies Earth = new Earth(
                "earth",
                new Vector(-1.474114613044819E+08, -2.972578730668059E+07, 2.745063093019836E+04)
                        .subtract(positionAdjustment),
                new Vector(5.306839723370035E+00, -2.934993232297309E+01, 6.693785809943620E-04)
                        .subtract(velocityAdjustment),
                5.97E+24
        );

        CelestialBodies Moon = new PlanetModel(
                "moon",
                new Vector(-1.471660130896692E+08, -2.946233624390671E+07, 5.289107585630007E+04)
                        .subtract(positionAdjustment),
                new Vector(4.533176913775855E+00, -2.858677469962307E+01, 6.725183765165710E-02)
                        .subtract(velocityAdjustment),
                7.35E+22
        );

        CelestialBodies Mars = new PlanetModel(
                "mars",
                new Vector(-2.15E+08, 1.27E+08, 7.94E+06)
                        .subtract(positionAdjustment),
                new Vector(-1.15E+01, -1.87E+01, -1.11E-01)
                        .subtract(velocityAdjustment),
                6.42E+23
        );

        CelestialBodies Jupiter = new PlanetModel(
                "jupiter",
                new Vector(5.54E+07, 7.62E+08, -4.40E+06)
                        .subtract(positionAdjustment),
                new Vector(-1.318182239145089E+01, 1.572192901176178E+00, 2.885060369130182E-01)
                        .subtract(velocityAdjustment),
                1.90E+27
        );

        CelestialBodies Saturn = new PlanetModel(
                "saturn",
                new Vector(1.422232874477568E+09, -1.907185789783441E+08, -5.331045504162484E+07)
                        .subtract(positionAdjustment),
                new Vector(7.466654196823925E-01, 9.554030161946484E+00, -1.960083815552225E-01)
                        .subtract(velocityAdjustment),
                5.68E+26
        );

        CelestialBodies Titan = new Titan(
                "titan",
                new Vector(1.421787721861711E+09, -1.917156604354737E+08, -5.275190739154144E+07)
                        .subtract(positionAdjustment),
                new Vector(5.951711470718787E+00, 7.676884294391810E+00, 2.538506864185868E-01)
                        .subtract(velocityAdjustment),
                1.35E+23
        );

        CelestialBodies Uranus = new PlanetModel(
                "uranus",
                new Vector(1.62E+09, 2.43E+09, -1.19E+07)
                        .subtract(positionAdjustment),
                new Vector(-5.72E+00, 3.45E+00, 8.70E-02)
                        .subtract(velocityAdjustment),
                8.68E+25
        );

        CelestialBodies Neptune = new PlanetModel(
                "neptune",
                new Vector(4.47E+09, -5.31E+07, -1.02E+08)
                        .subtract(positionAdjustment),
                new Vector(2.87E-02, 5.47E+00, -1.13E-01)
                        .subtract(velocityAdjustment),
                1.02E+26
        );

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

    public static void main(String[] args) {
        EphemerisLoader eph = new EphemerisLoader(2);
        eph.solve();
        LocalDateTime t0 = LocalDateTime.of(2025,4,1,0,0,0);
        LocalDateTime t1 = t0.plusMonths(3);
        double minDistance = Double.MAX_VALUE;
        LocalDateTime timeClosest = t0;
        for(LocalDateTime t = t0; t.isBefore(t1); t = t.plusDays(1)) {
            Vector earth = eph.history.get(t).get(BodyID.EARTH.index()).getPosition();
            Vector titan  = eph.history.get(t).get(BodyID.TITAN.index()).getPosition();
            if(getDistance(earth, titan) < minDistance){
            minDistance = getDistance(earth, titan);
            timeClosest = t;
        }
            System.out.println(minDistance + " " + timeClosest);

    }}
    public static double closestDistance(Vector pos1, Vector pos2, double minDistance) {
        System.out.println("Distance between Titan and SPACESHIP: " + getDistance(pos1,pos2));
        return Math.min(minDistance, getDistance(pos1, pos2));
    }
}
