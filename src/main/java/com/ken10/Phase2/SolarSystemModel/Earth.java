package com.ken10.Phase2.SolarSystemModel;


public class Earth extends CelestialBodies {
    public static final double RADIUS = 6370;
    public static final Vector EARTH_VELOCITY_INITIAL;
    public static final Vector EARTH_INITIAL_POSITION;
    static {
        double sunVX =  1.258146808934948E-02;
        double sunVY = -4.478489369359461E-03;
        double sunVZ = -2.140398498365391E-04;
        double sunX = -7.596727134361322E+05;
        double sunY = -7.764826803462036E+05;
        double sunZ = 2.520871232682851E+04;
        Vector vA = new Vector(sunVX, sunVY, sunVZ);
        Vector pA = new Vector(sunX, sunY, sunZ);
        Vector vEarth = new Vector(5.306839723370035E+00, -2.934993232297309E+01, 6.693785809943620E-04)
                .subtract(vA);
        Vector pEarth = new Vector(-1.474114613044819E+08, -2.972578730668059E+07, 2.745063093019836E+04)
                .subtract(pA);
        EARTH_VELOCITY_INITIAL = vEarth;
        EARTH_INITIAL_POSITION = pEarth;
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
