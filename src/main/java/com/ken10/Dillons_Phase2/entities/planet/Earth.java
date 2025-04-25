package com.ken10.Dillons_Phase2.entities.planet;

import com.ken10.Dillons_Phase2.entities.Other.Vector;

public class Earth extends CelestialBodies {
    public final double RADIUS = 6370;
    public Earth(String name, Vector position, Vector velocity, double mass) {
        super(name, position, velocity, mass);
}
}
