package com.ken10.Phase2.SolarSystemModel;


public class Earth extends CelestialBodies {
    public final double RADIUS = 6370;
    public static final Vector EARTH_VELOCITY_INITIAL;
    static {
        double sunVX =  1.258146808934948E-02;
        double sunVY = -4.478489369359461E-03;
        double sunVZ = -2.140398498365391E-04;
        Vector vA = new Vector(sunVX, sunVY, sunVZ);
        Vector vEarth = new Vector(5.306839723370035E+00, -2.934993232297309E+01, 6.693785809943620E-04)
                .subtract(vA);
        EARTH_VELOCITY_INITIAL = vEarth;
    }
    public Earth(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }
    public Earth(String name, Vector position, Vector velocity, double mass, double scaling, double size) {
        super(name, position, velocity, mass, scaling, size);
    }

    public static void main(String[] args) {
        System.out.println(EARTH_VELOCITY_INITIAL);
    }
}
