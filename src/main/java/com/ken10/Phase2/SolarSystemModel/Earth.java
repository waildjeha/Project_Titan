package com.ken10.Phase2.SolarSystemModel;


public class Earth extends CelestialBodies {
    public final double RADIUS = 6370;
    public Earth(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
    }
    public Earth(String name, Vector position, Vector velocity, double mass, double scaling) {
        super(name, position, velocity, mass, scaling);
    }
}
