package com.ken10.Phase2.StatesCalculations;


import com.ken10.Phase2.SolarSystemModel.BodyID;
import com.ken10.Phase2.SolarSystemModel.Vector;

import java.time.LocalDateTime;

public interface EphemerisProvider {
    Vector position(BodyID body, LocalDateTime time);
    Vector velocity(BodyID body, LocalDateTime time);
}
