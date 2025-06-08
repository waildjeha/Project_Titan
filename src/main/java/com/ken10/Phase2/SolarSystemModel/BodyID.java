package com.ken10.Phase2.SolarSystemModel;

import java.util.List;

/**
 * Enumeration representing celestial bodies in the solar system simulation.
 * Each body has a unique index used for accessing it from collections.
 * Includes planets, moons, the sun, and spacecraft objects.
 */
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

    /**
     * Returns the numerical index of this celestial body.
     * 
     * @return the index value associated with this body
    */
    public int index() { return idx; }

    /**
     * Retrieves the celestial body from a list using this body's index.
     * 
     * @param bodies the list of celestial bodies to search
     * @return the celestial body at this enum's index position
    */
    public CelestialBodies get(List<CelestialBodies> bodies) {
        return bodies.get(idx);
    }
}

