package com.ken10.Dillons_Phase2.entities.astro.Emhemeris;

import com.ken10.Dillons_Phase2.entities.Other.Vector;
import com.ken10.Dillons_Phase2.entities.planet.BodyID;

import java.time.LocalDateTime;

public interface EphemerisProvider {
    Vector position(BodyID body, LocalDateTime time);
    Vector velocity(BodyID body, LocalDateTime time);
}
