package com.ken10.Dillons_Phase2.entities.astro.Lambert;

import com.ken10.Dillons_Phase2.entities.Other.Vector;
import com.ken10.Dillons_Phase2.entities.planet.CelestialBodies;

import java.util.ArrayList;

public class LambertResult {
    private Vector vDepart;
    private Vector vArrival;
    public LambertResult(Vector vDepart, Vector vArrival) {
        this.vDepart = vDepart;
        this.vArrival = vArrival;
    }

    public Vector vDepart() {
        return vDepart;
    }
    public Vector vArrival() {
        return vArrival;
    }
}
