package com.ken10.Entities.probe;

import com.ken10.Entities.CelestialBodies;
import com.ken10.Other.Vector;

public class Probe extends CelestialBodies {
    public Probe(String name, Vector position, Vector velocity, double mass) {
        super(name,position,velocity,mass);
    }

    @Override
    public void update(Vector acceleration, double time) {

    }
}
