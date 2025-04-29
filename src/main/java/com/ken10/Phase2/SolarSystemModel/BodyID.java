package com.ken10.Phase2.SolarSystemModel;

import java.util.List;

public enum BodyID {
    SUN     (0),
    MERCURY (1),
    VENUS   (2),
    EARTH   (3),
    MOON    (4),
    MARS    (5),
    JUPITER (6),
    SATURN  (7),
    TITAN   (8),
    URANUS  (9),
    NEPTUNE (10),
    SPACESHIP (11);


    private final int idx;
    BodyID(int idx) { this.idx = idx; }
    public int index() { return idx; }

    public CelestialBodies get(List<CelestialBodies> bodies) {
        return bodies.get(idx);
    }
}

